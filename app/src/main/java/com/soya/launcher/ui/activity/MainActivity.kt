package com.soya.launcher.ui.activity

import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_AVR_POWER
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.shudong.lib_base.base.BaseVMActivity
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.ACTIVE_SUCCESS
import com.shudong.lib_base.ext.IS_MAIN_CANBACK
import com.shudong.lib_base.ext.REFRESH_HOME
import com.shudong.lib_base.ext.UPDATE_HOME_LIST
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.downloadPic
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.jsonToString
import com.shudong.lib_base.ext.net.lifecycle
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.obseverLiveEvent
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.sendLiveEventData
import com.shudong.lib_base.ext.yes
import com.soya.launcher.App
import com.soya.launcher.R
import com.soya.launcher.bean.HomeDataList
import com.soya.launcher.bean.HomeInfoDto
import com.soya.launcher.cache.AppCache
import com.soya.launcher.config.Config
import com.soya.launcher.databinding.ActivityMainBinding
import com.soya.launcher.enums.IntentAction
import com.soya.launcher.ext.bindNew
import com.soya.launcher.ext.compareSizes
import com.soya.launcher.ext.countImagesWithPrefix
import com.soya.launcher.ext.deleteAllImages
import com.soya.launcher.ext.exportToJson
import com.soya.launcher.ext.getBasePath
import com.soya.launcher.ext.jumpToAuth
import com.soya.launcher.ext.switchFragment
import com.soya.launcher.http.response.HomeResponse
import com.soya.launcher.manager.FilePathMangaer
import com.soya.launcher.manager.PreferencesManager
import com.soya.launcher.net.viewmodel.HomeViewModel
import com.soya.launcher.utils.GlideUtils
import com.soya.launcher.utils.getFileNameFromUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileReader


class MainActivity : BaseVMActivity<ActivityMainBinding, HomeViewModel>() {
    private var canBackPressed = true


    private val homeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
                val reason = intent.getStringExtra("reason")
                if (reason == "homekey") {
                    // 处理 Home 键按下的逻辑
                    sendBroadcast(Intent(IntentAction.ACTION_RESET_SELECT_HOME))
                }
            }
        }
    }



    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KEYCODE_AVR_POWER) {
            // 在这里处理 Home 按键按下事件
            // 如果需要特殊处理，需要使用 System UI 相关权限
            // 处理 Home 键按下的逻辑
            sendBroadcast(Intent(IntentAction.ACTION_RESET_SELECT_HOME))
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /* override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
         if (keyCode == KeyEvent.KEYCODE_HOME) {
             // 在这里处理 Home 按键抬起事件
             // 如果需要特殊处理，需要使用 System UI 相关权限
             return true
         }
         return super.onKeyUp(keyCode, event)
     }*/

    override fun initBeforeContent() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    override fun onStart() {
        super.onStart()
        fetchHomeData()
    }


    private var fetchJob: Job? = null
    private fun fetchHomeData(isRefresh: Boolean = false) {
        // 取消之前的任务（如果存在）
        fetchJob?.cancel()
        mViewModel.reqHomeInfo().lifecycle(this, errorCallback = {
            "数据请求错误${it.message}".e("zengyue1")
        }, isShowError = false){
            val dto = this
            dto.jsonToString().exportToJson("Home.json")

            if(isRefresh){
                if ((dto.reqId ?: 0) != AppCache.reqId){
                    startCoroutineScope(dto)
                }
            }else{
                startCoroutineScope(dto)
            }



        }
    }

    private fun startCoroutineScope(dto: HomeInfoDto) {
        fetchJob = lifecycleScope.launch {
            if ((dto.reqId ?: 0) != AppCache.reqId || !compareSizes(dto)) {
                withContext(Dispatchers.IO) {
                    if ((dto.reqId ?: 0) != AppCache.reqId){
                        AppCache.isAllDownload = false
                        deleteAllPic()
                    }
                    "开始下载图片:${AppCache.reqId}::::${dto.reqId}".e("zengyue1")
                    AppCache.reqId =dto.reqId ?: 0
                    startPicTask(this)
                }

            }

        }
    }

    private fun deleteAllPic() {
        val headerDirPath = "${appContext.filesDir.absolutePath}/header"
        val contentDirPath = "${appContext.filesDir.absolutePath}/content"
        headerDirPath.deleteAllImages()
        contentDirPath.deleteAllImages()
    }

    private fun startPicTask(coroutineScope: CoroutineScope) {
        coroutineScope.launch(Dispatchers.IO) {
            while (true) {
                checkPicDownload(coroutineScope)
                delay(15000)

            }

        }
    }

    private fun checkPicDownload(coroutineScope: CoroutineScope) {
        coroutineScope.launch (Dispatchers.IO){
            val path = FilePathMangaer.getJsonPath(this@MainActivity) + "/Home.json"
            if (File(path).exists()) {
                val result = Gson().fromJson<HomeInfoDto>(
                    JsonReader(FileReader(path)),
                    HomeInfoDto::class.java
                )

               // val size = result.movies?.size

                result.movies?.forEachIndexed { index, homeItem ->

                    val destPath =
                        "${"header".getBasePath()}/header_${homeItem?.name}_${index}_${(homeItem?.icon as String).getFileNameFromUrl()}"

                    if (!File(destPath).exists()) {

                        (homeItem.icon).downloadPic(lifecycleScope, destPath,
                            downloadComplete = { _, path ->
                                val filePathCache = AppCache.homeData.dataList
                                filePathCache[homeItem.icon] = path
                                AppCache.homeData = HomeDataList(filePathCache)
                                if(compareSizes(result)) {
                                    AppCache.isAllDownload = true
                                    sendLiveEventData(UPDATE_HOME_LIST,true)
                                }

                            },
                            downloadError = {

                            }
                        )
                    }


                    homeItem.datas?.forEachIndexed { position, it ->
                        val destContentPath =
                            "${"content".getBasePath()}/content_${homeItem.name}_${position}_${(it?.imageUrl as String).getFileNameFromUrl()}"
                        var isDownload = false
                        when (homeItem.name) {
                            "Youtube", "Disney+", "Hulu", "Prime video" -> {
                                isDownload = position < 8
                            }

                            else -> {
                                isDownload = true
                            }
                        }


                        isDownload.yes {
                            if (!File(destContentPath).exists()) {
                                "当前下载：${it.imageUrl}:::".e("zengyue1")
                                (it.imageUrl).downloadPic(lifecycleScope, destContentPath,
                                    downloadComplete = { _, path ->
                                        "下载成功：${it.imageUrl}:::".e("zengyue1")
                                        val filePathCache = AppCache.homeData.dataList
                                        filePathCache[it.imageUrl] = path
                                        AppCache.homeData = HomeDataList(filePathCache)
                                        if(compareSizes(result)) {
                                            AppCache.isAllDownload = true
                                            sendLiveEventData(UPDATE_HOME_LIST,true)
                                        }


                                    },
                                    downloadError = {
                                    }
                                )
                            }
                        }


                    }
                }

            }
        }

    }

    override fun initView() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                // Simulate a long running task
                setBG(mBind.ivBg)
            }

            withContext(Dispatchers.Main) {
                // Simulate a long running task
                registerReceiver(homeReceiver, IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
                commit()
            }
        }

        //val model = ReflectUtils.getProperty("persist.vendor.launcher.platform","")
        //"当前设备型号是====$model"
    }


    fun setBG(view: ImageView?) {
        var id = if (Config.COMPANY == 0) R.drawable.wallpaper_22 else R.drawable.wallpaper_1
        for (wallpaper in App.WALLPAPERS) {
            if (wallpaper.id == PreferencesManager.getWallpaper()) {
                id = wallpaper.picture
                break
            }
        }
        GlideUtils.bindBlur(this, view, id)
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(homeReceiver)
    }

    override fun initObserver() {
        this.obseverLiveEvent<Boolean>(ACTIVE_SUCCESS) {
            commit()
        }

        this.obseverLiveEvent<Boolean>(IS_MAIN_CANBACK) {

            it.yes {
                canBackPressed = true
            }.otherwise {
                canBackPressed = false
            }
        }

        this.obseverLiveEvent<String>("loadnet"){
           it.bindNew()
        }
        this.obseverLiveEvent<Boolean>(REFRESH_HOME) {
           fetchHomeData(true)
        }

    }


    private fun commit() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_browse_fragment, getFragment())
            .commitAllowingStateLoss()
    }

    fun switchAuthFragment() {
        canBackPressed = true
        jumpToAuth()
    }



    fun getFragment(): Fragment = switchFragment()

    override fun onBackPressed() {
        if (canBackPressed) {
            super.onBackPressed()
        } else {
            sendBroadcast(Intent(IntentAction.ACTION_RESET_SELECT_HOME))
        }

    }

}
