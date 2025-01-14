package com.soya.launcher

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.chihi.m98.hook.JsonSerializeHook
import com.drake.brv.utils.BRV
import com.drake.serialize.serialize.Serialize
import com.shudong.lib_base.base.viewmodel.baseModules
import com.shudong.lib_base.ext.MvvmHelper
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.net.lifecycle
import com.shudong.lib_base.ext.no
import com.soya.launcher.ad.Plugin
import com.soya.launcher.ad.Plugin.currentApkPath
import com.soya.launcher.ad.config.PluginCache
import com.soya.launcher.ad.unzipAndKeepApk
import com.soya.launcher.bean.AppItem
import com.soya.launcher.bean.Data
import com.soya.launcher.cache.AppCache
import com.soya.launcher.ext.getBasePath
import com.soya.launcher.ext.loadBlurDrawable
import com.soya.launcher.net.di.homeModules
import com.soya.launcher.net.repository.HomeRepository
import com.soya.launcher.net.viewmodel.HomeViewModel
import com.soya.launcher.product.base.product
import com.tencent.mmkv.MMKV
import com.thumbsupec.lib_net.di.netModules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.inject
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
