package com.chihihx.launcher.ui.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_AVR_POWER
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.blankj.utilcode.util.NetworkUtils
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.shudong.lib_base.ext.ACTIVE_SUCCESS
import com.shudong.lib_base.ext.ADD_APPSTORE
import com.shudong.lib_base.ext.CANCLE_MAIN_LIFECYCLESCOPE
import com.shudong.lib_base.ext.HOME_EVENT
import com.shudong.lib_base.ext.IS_MAIN_CANBACK
import com.shudong.lib_base.ext.RECREATE_MAIN
import com.shudong.lib_base.ext.REFRESH_HOME
import com.shudong.lib_base.ext.REGET_HOMEDATA
import com.shudong.lib_base.ext.RERUN_SCOPE
import com.shudong.lib_base.ext.UPDATE_HOME_LIST
import com.shudong.lib_base.ext.UPDATE_WALLPAPER_EVENT
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.downloadApkNopkName
import com.shudong.lib_base.ext.downloadPic
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.jsonToBean
import com.shudong.lib_base.ext.jsonToString
import com.shudong.lib_base.ext.net.lifecycle
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.obseverLiveEvent
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.replaceFragment
import com.shudong.lib_base.ext.sendLiveEventData
import com.shudong.lib_base.ext.yes
import com.chihihx.launcher.BaseWallpaperActivity
import com.chihihx.launcher.R
import com.chihihx.launcher.SplashFragment
import com.chihihx.launcher.ad.AdSdk.loadAd
import com.chihihx.launcher.ad.Plugin
import com.chihihx.launcher.ad.config.AdIds
import com.chihihx.launcher.ad.config.PluginCache
import com.chihihx.launcher.ad.unzipAndKeepApk
import com.chihihx.launcher.bean.HomeDataList
import com.chihihx.launcher.bean.HomeInfoDto
import com.chihihx.launcher.bean.HomeStoreFileList
import com.chihihx.launcher.bean.HomeStoreList
import com.chihihx.launcher.bean.SearchDto
import com.chihihx.launcher.cache.AppCache
import com.chihihx.launcher.databinding.ActivityMainBinding
import com.chihihx.launcher.ext.bindNew
import com.chihihx.launcher.ext.clearStack
import com.chihihx.launcher.ext.compareSizes
import com.chihihx.launcher.ext.count
import com.chihihx.launcher.ext.deleteAdAndPluginDirectories
import com.chihihx.launcher.ext.deleteAllImages
import com.chihihx.launcher.ext.deleteApkFilesInPluginDir
import com.chihihx.launcher.ext.exportToJson
import com.chihihx.launcher.ext.getBasePath
import com.chihihx.launcher.ext.isGame
import com.chihihx.launcher.ext.navigateBack
import com.chihihx.launcher.localWallPaperDrawable
import com.chihihx.launcher.manager.FilePathMangaer
import com.chihihx.launcher.net.viewmodel.HomeViewModel
import com.chihihx.launcher.product.base.product
import com.chihihx.launcher.ui.fragment.BlankFragment
import com.chihihx.launcher.utils.getFileNameFromUrl
import com.chihihx.launcher.utils.getZipFileNameFromUrl
import com.chihihx.launcher.utils.host.HostUtils
import com.chihihx.launcher.utils.toTrim
import com.thumbsupec.lib_net.AppCacheNet
import com.thumbsupec.lib_net.di.domains
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
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

    }

    private fun initHomeDataScope() {
        lifecycleScope.launch {
            while (true) {
                delay(300)
                if (NetworkUtils.isConnected()) {
                    fetchHomeData()
                    println("发起请求1")
                    reqPluginInfo()
                    cancel()
                }
                delay(3500)
            }
        }
    }

    private fun startPluginCheckPolling() {
        lifecycleScope.launch {

                while (true) {
                    delay(24 * 60 * 60 * 1000L) // 延迟 24 小时
                    reqPluginInfo() // 执行任务

                }

        }

        lifecycleScope.launch {
            repeat(5) {
                if (AppCacheNet.isDomainTryAll.get()) {
                    AppCacheNet.isDomainTryAll.set(false) // 原子化设置为 false
                    delay(5000) // 延迟 3.5秒
                    initHomeDataScope()
                    sendLiveEventData(RERUN_SCOPE, true)
                }else{
                    delay(5000)
                }

            }

        }



    }

    private fun reqPluginInfo() {
        errorJobPlugin?.cancel()
        if (PluginCache.pluginPath.isNotEmpty()) {
            val path = PluginCache.pluginPath
            Plugin.install(appContext, path)
        }
        mViewModel.reqPluginInfo().lifecycle(this, errorCallback = {
            //deleteAdAndPluginDirectories()
            errorJobPlugin?.cancel()
            errorJobPlugin = lifecycleScope.launch(Dispatchers.Main) {
                while (true) {
                    if (NetworkUtils.isConnected() && !AppCacheNet.isDomainTryAll.get()) {
                        if(AppCacheNet.successfulDomain.isEmpty())reqPluginInfo() else cancel()

                    }
                    delay(2000)

                }

            }
        }) {
            val dto = this
            if (dto.sdkAddr.isNullOrEmpty()) {
                deleteAdAndPluginDirectories()
                Plugin.uninstall()
                return@lifecycle
            }
            PluginCache.pluginInfo = dto
            if (PluginCache.pluginVersion != this.sdkVersion) {
                val fileName = dto.sdkAddr.getZipFileNameFromUrl()
                val destPath = "${"plugin".getBasePath()}/${fileName}"
                deleteOldPlugin()
                dto.sdkAddr.downloadApkNopkName(
                    lifecycleScope,
                    downloadPath = destPath,
                    downloadError = {
                        errorJobPluginDownload?.cancel()
                        errorJobPluginDownload = lifecycleScope.launch(Dispatchers.Main) {
                            while (true) {
                                if (NetworkUtils.isConnected() && !AppCacheNet.isDomainTryAll.get()) {
                                    if(AppCacheNet.successfulDomain.isEmpty())reqPluginInfo() else cancel()

                                }
                                delay(2000)

                            }

                        }
                    },
                    downloadComplete = { it, path ->
                        val apkPath = destPath.unzipAndKeepApk()
                        apkPath.isNullOrEmpty().no {
                            PluginCache.pluginPath = apkPath!!
                            PluginCache.pluginVersion = dto.sdkVersion.toString()
                            Plugin.install(appContext, apkPath)
                        }
                    }) {
                }
            }

        }
    }

    private fun deleteOldPlugin() {
        deleteApkFilesInPluginDir()
    }

    private fun fetchStoreData() {
        errorJobStore?.cancel()
        mViewModel.reqSearchAppList(
            tag = "hot", pageSize = 5
        )
            .lifecycle(this, errorCallback = {
                errorJobStore = lifecycleScope.launch(Dispatchers.Main) {
                    while (true) {
                        if (NetworkUtils.isConnected() && !AppCacheNet.isDomainTryAll.get()) {
                            fetchStoreData()
                        }
                        delay(2000)

                    }

                }
            }) {
                val dto = this.jsonToBean<SearchDto>()
                if ((dto.result?.appList?.size ?: 0) > 0) {
                    dto.result?.appList?.let {
                        var cacheStoreData = AppCache.homeStoreData.dataList
                        cacheStoreData.clear()
                        cacheStoreData.addAll(it)
                        AppCache.homeStoreData = HomeStoreList(cacheStoreData)
                        it.forEach {
                            mViewModel.loadImageToFileInViewModel(it.appIcon ?: "") { path ->

                                if (!path.isNullOrEmpty()) {
                                    val cacheList = AppCache.homeStoreFileData.dataList
                                    cacheList[it.appIcon!!] = path
                                    AppCache.homeStoreFileData = HomeStoreFileList(cacheList)
                                    if (AppCache.homeStoreFileData.dataList.size == 5) {
                                        //store图片下载完成
                                        sendLiveEventData(ADD_APPSTORE, true)
                                    }
                                }
                            }
                        }

                    }

                }

            }
    }


    private var fetchJob: Job? = null
    private var errorJob: Job? = null
    private var errorJobStore: Job? = null
    private var errorJobPluginDownload: Job? = null
    private var errorJobPlugin: Job? = null
    private fun fetchHomeData(isRefresh: Boolean = false) {
        // 取消之前的任务（如果存在）
        isHandleUpdateList = false
        fetchJob?.cancel()
        errorJob?.cancel()
        mViewModel.reqHomeInfo().lifecycle(this, errorCallback = {
            errorJob = lifecycleScope.launch(Dispatchers.Main) {
                while (true) {
                    if (NetworkUtils.isConnected() && !AppCacheNet.isDomainTryAll.get()) {
                        fetchHomeData()
                    }
                    delay(2000)

                }

            }

        }, isShowError = false) {
            val dto = this
            val file = File(FilePathMangaer.getJsonPath(appContext) + "/Home.json")
            if ((dto.reqId ?: 0) != AppCache.reqId || AppCache.isGame != isGame()) {
                if (file.exists()) AppCache.isReload = true

            }
            dto.jsonToString().exportToJson("Home.json")
            AppCache.homeInfo = dto


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
            if ((dto.reqId
                    ?: 0) != AppCache.reqId || !compareSizes(dto) || AppCache.isGame != isGame()
            ) {
                withContext(Dispatchers.IO) {
                    if ((dto.reqId ?: 0) != AppCache.reqId || AppCache.isGame != isGame()) {
                        AppCache.isAllDownload = false
                        deleteAllPic()
                        AppCache.homeStoreFileData.dataList.clear()
                    }

                    AppCache.reqId = dto.reqId ?: 0
                    AppCache.isGame = isGame()
                    startPicTask(this)


                }

            }
        }
        if ((dto.reqId
                ?: 0) != AppCache.reqId || AppCache.isGame != isGame() || AppCache.homeStoreFileData.dataList.size < 4
        ) {
            fetchStoreData()
        }
    }

    private fun deleteAllPic() {
        val headerDirPath = "${appContext.filesDir.absolutePath}/header"
        val contentDirPath = "${appContext.filesDir.absolutePath}/content"
        val appstoreDirPath = "${appContext.filesDir.absolutePath}/appstore"
        headerDirPath.deleteAllImages()
        contentDirPath.deleteAllImages()
        appstoreDirPath.deleteAllImages()
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

    var hostJob: Job? = null
    var mainHostJob: Job? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        "重新执行MainActivity的oncreate了哦".e("zengyue3")
        count = 0
        lifecycleScope.launch {
            registerReceiver(
                homeReceiver,
                IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS),
                Context.RECEIVER_EXPORTED
            )
            commit()

            loadAd {
                adView = mBind.flAd
                adId = AdIds.AD_ID_SPLASH
                isLoadFromLocal = true
                isAutoFocus = true
                onAdCallback {
                    onAdDataFetchFailed { println("拉取失败") }
                    onAdLoadFailed { println("加载失败") }
                    onNoLocalAd {
                        sendLiveEventData(CANCLE_MAIN_LIFECYCLESCOPE, true)
                    }
                    onAdLoadSuccess {
                        sendLiveEventData(CANCLE_MAIN_LIFECYCLESCOPE, true)
                        replaceFragment(BlankFragment.newInstance(), R.id.main_browse_fragment)
                    }
                    onAdCountdownFinished {
                        mBind.flAd.isVisible = false
                        commit()
                    }
                }
            }

        }

        startPluginCheckPolling()

        if (AppCacheNet.randomUrl.contains("localhost")) {
            mainHostJob = lifecycleScope.launch {
                while (true){
                    withContext(Dispatchers.IO){
                        if(NetworkUtils.isConnected()&&NetworkUtils.isAvailable()){
                            withContext(Dispatchers.Main){
                                hostJob = lifecycleScope.launch {
                                    repeat(3) {
                                        delay(500)
                                        HostUtils.getSlaveAvailableHost {
                                            it?.let {
                                                AppCacheNet.randomUrl = it
                                                domains[domains.size - 1] = AppCacheNet.randomUrl
                                                hostJob?.cancel()
                                            }
                                        }
                                        delay(5000)
                                    }


                                }
                                mainHostJob?.cancel()
                            }

                        }
                    }

                    delay(5000)
                }
            }
        }

        initHomeDataScope()

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

        obseverLiveEvent<Boolean>(UPDATE_WALLPAPER_EVENT) {
            updateWallPaper()
        }

        obseverLiveEvent<Boolean>(RECREATE_MAIN) {
            recreate()
        }

        this.obseverLiveEvent<Boolean>(REGET_HOMEDATA) {
            fetchHomeData()
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
            if (!isCanNavi) {
                finish()
            }
        } else {
            sendLiveEventData(HOME_EVENT, false)
        }

    }

}
