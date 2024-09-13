package com.soya.launcher

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.sip.SipErrorCode.TIME_OUT
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import com.chihi.m98.hook.JsonSerializeHook
import com.drake.serialize.serialize.Serialize
import com.lzy.okgo.OkGo
import com.lzy.okgo.interceptor.HttpLoggingInterceptor
import com.shudong.lib_base.ContextManager
import com.shudong.lib_base.base.viewmodel.baseModules
import com.shudong.lib_base.ext.MvvmHelper
import com.shudong.lib_base.ext.d
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.yes
import com.shudong.lib_base.global.AppCacheBase
import com.soya.launcher.bean.AppItem
import com.soya.launcher.bean.CacheWeather
import com.soya.launcher.bean.Data
import com.soya.launcher.bean.Movice
import com.soya.launcher.bean.MyRunnable
import com.soya.launcher.bean.Wallpaper
import com.soya.launcher.cache.AppCache
import com.soya.launcher.cache.AppCache.WALLPAPERS
import com.soya.launcher.config.Config
import com.soya.launcher.enums.Atts
import com.soya.launcher.enums.IntentAction
import com.soya.launcher.http.HttpRequest
import com.soya.launcher.utils.BluetoothScannerUtils
import com.soya.launcher.utils.FileUtils
import com.soya.launcher.utils.PreferencesUtils
import com.soya.launcher.net.di.homeModules
import com.soya.launcher.product.base.product
import com.tencent.mmkv.MMKV
import com.thumbsupec.lib_base.ext.language.initMultiLanguage
import com.thumbsupec.lib_base.toast.ToastUtils
import com.thumbsupec.lib_net.AppCacheNet
import com.thumbsupec.lib_net.di.httpLoggingInterceptor
import com.thumbsupec.lib_net.di.netModules
import com.thumbsupec.lib_net.http.MyX509TrustManager
import com.thumbsupec.lib_net.http.SSL
import com.thumbsupec.lib_net.http.createSslContext
import com.thumbsupec.lib_net.http.getSSLContext
import com.thumbsupec.lib_net.http.intercept.AuthorizationInterceptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import rxhttp.RxHttpPlugins
import java.util.Arrays
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.logging.Level

class App : Application() {

    override fun onCreate() {
        "开始应用启动1".e("zengyue3")
        super.onCreate()
        AppCache.isAppInited = false
        MvvmHelper.init(this@App)
        "开始应用启动2".e("zengyue3")
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
