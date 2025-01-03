package com.soya.launcher.ui.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_AVR_POWER
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.NetworkUtils
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.shudong.lib_base.ext.ACTIVE_SUCCESS
import com.shudong.lib_base.ext.HOME_EVENT
import com.shudong.lib_base.ext.IS_MAIN_CANBACK
import com.shudong.lib_base.ext.RECREATE_MAIN
import com.shudong.lib_base.ext.REFRESH_HOME
import com.shudong.lib_base.ext.UPDATE_HOME_LIST
import com.shudong.lib_base.ext.UPDATE_WALLPAPER_EVENT
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.downloadPic
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.isRepeatExcute
import com.shudong.lib_base.ext.jsonToString
import com.shudong.lib_base.ext.net.lifecycle
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.obseverLiveEvent
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.replaceFragment
import com.shudong.lib_base.ext.sendLiveEventData
import com.shudong.lib_base.ext.yes
import com.soya.launcher.BaseWallpaperActivity
import com.soya.launcher.R
import com.soya.launcher.SplashFragment
import com.soya.launcher.bean.HomeDataList
import com.soya.launcher.bean.HomeInfoDto
import com.soya.launcher.cache.AppCache
import com.soya.launcher.databinding.ActivityMainBinding
import com.soya.launcher.ext.bindNew
import com.soya.launcher.ext.clearAndNavigate
import com.soya.launcher.ext.clearStack
import com.soya.launcher.ext.compareSizes
import com.soya.launcher.ext.deleteAllImages
import com.soya.launcher.ext.exportToJson
import com.soya.launcher.ext.getBasePath
import com.soya.launcher.ext.isGame
import com.soya.launcher.ext.isHome
import com.soya.launcher.ext.loadJar
import com.soya.launcher.ext.navigateBack
import com.soya.launcher.localWallPaperDrawable
import com.soya.launcher.manager.FilePathMangaer
import com.soya.launcher.net.viewmodel.HomeViewModel
import com.soya.launcher.product.base.product
import com.soya.launcher.ui.fragment.GuideLanguageFragment
import com.soya.launcher.utils.getFileNameFromUrl
import com.soya.launcher.utils.toTrim
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileReader


class MainActivity : BaseWallpaperActivity<ActivityMainBinding, HomeViewModel>() {
    private var canBackPressed = true


    private val homeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
                val reason = intent.getStringExtra("reason")
                if (reason == "homekey") {
                    // 处理 Home 键按下的逻辑
                    sendLiveEventData(HOME_EVENT, true)
                }
            }
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_HOME || keyCode == KEYCODE_AVR_POWER) {
            // 处理 Home 键按下的逻辑
            sendLiveEventData(HOME_EVENT, true)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun initBeforeContent() {
        if (localWallPaperDrawable != null) {
            //设置后没效果
            //window.setBackgroundDrawableResource(R.drawable.wallpaper_1)
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    override fun onStart() {
        super.onStart()
        supportFragmentManager.clearStack()
        //loadJar()
        lifecycleScope.launch {
            delay(300)
            fetchHomeData()
        }

    }



    private var fetchJob: Job? = null
    private var errorJob: Job? = null
    private fun fetchHomeData(isRefresh: Boolean = false) {
        // 取消之前的任务（如果存在）
        isHandleUpdateList = false
        fetchJob?.cancel()
        errorJob?.cancel()
        mViewModel.reqHomeInfo().lifecycle(this, errorCallback = {
            errorJob = lifecycleScope.launch(Dispatchers.Main) {
                while (true) {
                    if(NetworkUtils.isConnected()){
                        fetchHomeData()
                    }
                    delay(2000)

                }

            }

        }, isShowError = false) {
            val dto = this
            val file = File(FilePathMangaer.getJsonPath(appContext) + "/Home.json")
            if ((dto.reqId ?: 0) != AppCache.reqId || AppCache.isGame != isGame()) {
                if(file.exists())AppCache.isReload = true

            }
            dto.jsonToString().exportToJson("Home.json")


            if (isRefresh) {
                if ((dto.reqId ?: 0) != AppCache.reqId || AppCache.isGame != isGame()) {
                    deleteAllPic()
                    startCoroutineScope(dto)
                }
            } else {
                startCoroutineScope(dto)

            }


        }
    }

    private fun startCoroutineScope(dto: HomeInfoDto) {
        fetchJob = lifecycleScope.launch {
            if ((dto.reqId ?: 0) != AppCache.reqId || !compareSizes(dto)|| AppCache.isGame != isGame()) {
                withContext(Dispatchers.IO) {
                    if ((dto.reqId ?: 0) != AppCache.reqId|| AppCache.isGame != isGame()) {
                        AppCache.isAllDownload = false
                        deleteAllPic()
                    }

                    AppCache.reqId = dto.reqId ?: 0
                    AppCache.isGame = isGame()
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
                delay(60000)

            }

        }
    }

    private var isHandleUpdateList = false
    private fun checkPicDownload(coroutineScope: CoroutineScope) {
        coroutineScope.launch(Dispatchers.IO) {
            val path = FilePathMangaer.getJsonPath(this@MainActivity) + "/Home.json"
            if (File(path).exists()) {
                val result = Gson().fromJson<HomeInfoDto>(
                    JsonReader(FileReader(path)),
                    HomeInfoDto::class.java
                )


                // val size = result.movies?.size
                if (!compareSizes(result)) {


                    result.movies?.forEachIndexed { index, homeItem ->

                        val destPath =
                            "${"header".getBasePath()}/header_${homeItem?.name}_${index}_${(homeItem?.icon as String).getFileNameFromUrl()}"

                        var isDownloadHeader = false
                        when (homeItem.name) {

                            "Google play", "media center" -> {
                                isDownloadHeader = false
                            }

                            else -> {
                                isDownloadHeader = true
                            }
                        }

                        isDownloadHeader.yes {
                            if (!File(destPath).exists()) {
                                val filePathCache = AppCache.homeData.dataList
                                filePathCache[homeItem.icon] = destPath
                                AppCache.homeData = HomeDataList(filePathCache)

                                (homeItem.icon).downloadPic(lifecycleScope, destPath,
                                    downloadComplete = { _, path ->

                                        if (compareSizes(result) && !isHandleUpdateList) {
                                            AppCache.isAllDownload = true
                                            sendLiveEventData(UPDATE_HOME_LIST, true)
                                            isHandleUpdateList = true
                                        }

                                    },
                                    downloadError = {

                                    }
                                )
                            }
                        }



                        homeItem.datas?.forEachIndexed { position, it ->
                            val destContentPath =
                                "${"content".getBasePath()}/content_${homeItem.name?.toTrim()}_${position}_${(it?.imageUrl as String).getFileNameFromUrl()}"
                            var isDownload = false
                            when (homeItem.name) {
                                "Youtube", "Disney+", "Hulu", "Prime video" -> {
                                    isDownload = position < 8
                                }

                                "Google play", "media center" -> {
                                    isDownload = false
                                }

                                else -> {
                                    isDownload = true
                                }
                            }


                            isDownload.yes {
                                if (!File(destContentPath).exists()) {

                                    val filePathCache = AppCache.homeData.dataList
                                    filePathCache[it.imageUrl] = destContentPath
                                    AppCache.homeData = HomeDataList(filePathCache)

                                    (it.imageUrl).downloadPic(lifecycleScope, destContentPath,
                                        downloadComplete = { _, path ->


                                            if (compareSizes(result) && !isHandleUpdateList) {
                                                AppCache.isAllDownload = true
                                                sendLiveEventData(UPDATE_HOME_LIST, true)
                                                isHandleUpdateList = true
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

    }

    override fun initView() {
        lifecycleScope.launch {
            registerReceiver(homeReceiver, IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
            commit()
        }

        /*lifecycleScope.launch {

            while (true) {
                delay(3000)
                isHome()
                //"当前是否在桌面：：：${isHome()}".e("zengyue3")
            }

        }*/

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

        this.obseverLiveEvent<String>("loadnet") {
            it.bindNew()
        }
        this.obseverLiveEvent<Boolean>(REFRESH_HOME) {
            "执行网络请求2".e("zengyue3")
           // fetchHomeData(true)
        }

        obseverLiveEvent<Boolean>(UPDATE_WALLPAPER_EVENT){
            updateWallPaper()
        }

        obseverLiveEvent<Boolean>(RECREATE_MAIN){
            recreate()
        }

    }


    private fun commit() {
        replaceFragment(SplashFragment.newInstance(), R.id.main_browse_fragment)
    }

    fun switchAuthFragment() {
        canBackPressed = true
        product.jumpToAuth(this)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (canBackPressed) {
            //super.onBackPressed()
            val isCanNavi = supportFragmentManager.navigateBack()
            if(!isCanNavi){
                finish()
            }
        } else {
            sendLiveEventData(HOME_EVENT, false)
        }

    }

}
