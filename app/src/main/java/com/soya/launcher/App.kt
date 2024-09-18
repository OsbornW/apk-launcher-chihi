package com.soya.launcher

import android.app.Application
import com.chihi.m98.hook.JsonSerializeHook
import com.drake.serialize.serialize.Serialize
import com.shudong.lib_base.base.viewmodel.baseModules
import com.shudong.lib_base.ext.MvvmHelper
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.e
import com.soya.launcher.bean.AppItem
import com.soya.launcher.bean.Data
import com.soya.launcher.cache.AppCache
import com.soya.launcher.ext.loadBlurDrawable
import com.soya.launcher.net.di.homeModules
import com.soya.launcher.product.base.product
import com.tencent.mmkv.MMKV
import com.thumbsupec.lib_net.di.netModules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

        product.addWallPaper()

        AppCache.isAppInited = false

            startKoin {
                androidContext(appContext)
                modules(netModules)
                modules(baseModules)
                modules(homeModules)

        }


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
