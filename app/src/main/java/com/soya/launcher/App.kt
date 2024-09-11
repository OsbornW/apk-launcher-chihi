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
import com.chihi.m98.hook.JsonSerializeHook
import com.drake.serialize.serialize.Serialize
import com.lzy.okgo.OkGo
import com.lzy.okgo.interceptor.HttpLoggingInterceptor
import com.shudong.lib_base.ContextManager
import com.shudong.lib_base.base.viewmodel.baseModules
import com.shudong.lib_base.ext.MvvmHelper
import com.shudong.lib_base.ext.d
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
    private var receiver: InnerReceiver? = null

    override fun onCreate() {
        super.onCreate()
        instance = this

        MMKV.initialize(this)
        Serialize.hook = JsonSerializeHook()

        AppCacheNet.baseUrl = BuildConfig.BASE_URL
        AppCache.isGuidChageLanguage = false

        MvvmHelper.init(this@App)

        ToastUtils.init(this@App)
        ContextManager.getInstance().init(this@App)

        startKoin {
            androidContext(this@App)
            modules(netModules)
            //modules(appLoginModule)
            modules(baseModules)
            modules(homeModules)
        }

        val sslContext = getSSLContext()
        val sslSocketFactory = sslContext.socketFactory
        RxHttpPlugins.init(OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, MyX509TrustManager())
            .hostnameVerifier { _, _ -> true }
            .build())


        OkGo.init(this)

        // 配置日志拦截器
        val loggingInterceptor = HttpLoggingInterceptor("zengyue")
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY) // 设置打印级别
        loggingInterceptor.setColorLevel(Level.INFO) // 设置颜色级别
        //builder.addInterceptor(loggingInterceptor) // 添加 OkGo 日志拦截器

        OkGo.getInstance().addInterceptor(loggingInterceptor)
            .setRetryCount(3) // 全局的超时重试次数
            .setCertificates()

        HttpRequest.init(this)
        if (TextUtils.isEmpty(PreferencesUtils.getString(Atts.UID, ""))) {
            PreferencesUtils.setProperty(Atts.UID, UUID.randomUUID().toString())
        }
        BluetoothScannerUtils.init(this)
        initRemote()
        com.hs.App.init(this)
        //BRV.modelId = BR.m
        initMultiLanguage(this)
        product.addWallPaper()
    }


    private fun initRemote() {
        receiver = InnerReceiver()
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
        if (Config.COMPANY != 3) {
            registerReceiver(receiver, filter)
        }
    }

    inner class InnerReceiver : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            //"当前收到的广播是====${intent.action}"
            when (intent.action) {
                BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED -> {
                    val state = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_CONNECTION_STATE,
                        BluetoothAdapter.STATE_DISCONNECTED
                    )
                    val prevState = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_PREVIOUS_CONNECTION_STATE,
                        BluetoothAdapter.STATE_DISCONNECTED
                    )
                    when (state) {
                        2 -> {}
                        0 -> {
                            if (prevState == 3) {
                            }
                        }
                    }
                }

                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    if (device!!.type != BluetoothDevice.DEVICE_TYPE_LE) {
                    }
                }

                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    if (device!!.type != BluetoothDevice.DEVICE_TYPE_LE) {
                    }
                }

            }
        }
    }


    companion object {
        @JvmStatic
        var instance: App? = null
            private set
        private val exec = Executors.newCachedThreadPool()

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
