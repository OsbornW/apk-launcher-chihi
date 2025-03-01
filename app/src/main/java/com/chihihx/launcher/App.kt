package com.chihihx.launcher

import android.app.Application
import com.chihi.m98.hook.JsonSerializeHook
import com.drake.brv.utils.BRV
import com.drake.serialize.serialize.Serialize
import com.shudong.lib_base.base.viewmodel.baseModules
import com.shudong.lib_base.ext.MvvmHelper
import com.shudong.lib_base.ext.appContext
import com.chihihx.launcher.BuildConfig.BASE_URL
import com.chihihx.launcher.bean.AppItem
import com.chihihx.launcher.bean.Data
import com.chihihx.launcher.cache.AppCache
import com.chihihx.launcher.ext.loadBlurDrawable
import com.chihihx.launcher.net.di.homeModules
import com.chihihx.launcher.product.base.product
import com.tencent.mmkv.MMKV
import com.thumbsupec.lib_net.AppCacheNet
import com.thumbsupec.lib_net.di.netModules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import java.util.Arrays
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class App : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    override fun onCreate() {
        super.onCreate()
        MvvmHelper.init(this@App)
        MMKV.initialize(appContext)
        Serialize.hook = JsonSerializeHook()

        if (AppCacheNet.baseUrl != BASE_URL) {
            AppCacheNet.baseUrl = BASE_URL
        }

        if (AppCacheNet.backupDomain != BuildConfig.BACKUP_DOMAIN) {
            AppCacheNet.backupDomain = BuildConfig.BACKUP_DOMAIN
        }

        AppCacheNet.isDebug = BuildConfig.DEBUG

        startKoin {
            androidContext(appContext)
            modules(netModules)
            modules(baseModules)
            modules(homeModules)
        }
        BRV.modelId = BR.m

        product.addWallPaper()

        AppCache.isAppInited = false

        applicationScope.launch {
            val drawable = curWallpaper().loadBlurDrawable()
            localWallPaperDrawable = drawable
        }

    }


    companion object {

        @JvmField
        val MOVIE_MAP: MutableMap<Long, MutableList<Data>> = ConcurrentHashMap()

        @JvmField
        val APP_STORE_ITEMS: MutableList<AppItem> = CopyOnWriteArrayList()

        @JvmField
        val APP_SEARCH_STORE_ITEMS: MutableList<AppItem> = CopyOnWriteArrayList()

        @JvmField
        val SKIP_PAKS: Set<String> = HashSet(
            Arrays.asList(
                *arrayOf(
                    "com.hbo.hbonow",
                    "com.wbd.stream",
                    "com.hulu.plus",
                    "com.hulu.livingroomplus",
                    "com.amazon.amazonvideo.livingroom",
                    "com.amazon.avod.thirdpartyclient",
                    "com.amazon.amazonvideo.livingroom"
                )
            )
        )
    }
}
