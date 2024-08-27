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
import com.soya.launcher.ui.dialog.RemoteDialog
import com.soya.launcher.utils.BluetoothScannerUtils
import com.soya.launcher.utils.FileUtils
import com.soya.launcher.utils.PreferencesUtils
import com.soya.launcher.net.di.homeModules
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
    private var mFailDialog: RemoteDialog? = null
    private var mSuccessDialog: RemoteDialog? = null
    private var remoteRunnable: MyRunnable? = null
    private var lastRemoteTime: Long = -1


    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            // 处理消息
            when (msg.what) {
                1 -> {
                    mFailDialog?.dismiss()
                    (mSuccessDialog?.isShowing == false).yes {
                        mSuccessDialog?.show()
                    }
                    postDelayed({
                        mSuccessDialog?.dismiss()
                    }, 2500)

                }

                else -> {
                    mSuccessDialog?.dismiss()
                    (mFailDialog?.isShowing == false).yes {
                        mFailDialog?.show()
                    }
                    postDelayed({
                        mFailDialog?.dismiss()
                    }, 2500)

                }
            }
        }
    }

    private val applicationScope =
        CoroutineScope(
            SupervisorJob()
                    + Dispatchers.Main
        )


    private val interval: Long = 3000 // 定时任务间隔（毫秒）
    override fun onCreate() {
        super.onCreate()
        instance = this

        MMKV.initialize(this)
        Serialize.hook = JsonSerializeHook()

        AppCacheNet.baseUrl = BuildConfig.BASE_URL
        AppCache.isGuidChageLanguage = false

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


        //AppCache.lastTipTime = 0L
        //AppCache.updateInteval

        OkGo.init(this)

        // 配置 OkGo
        //val builder = OkHttpClient.Builder()

        // 配置日志拦截器
        val loggingInterceptor = HttpLoggingInterceptor("zengyue")
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY) // 设置打印级别
        loggingInterceptor.setColorLevel(Level.INFO) // 设置颜色级别
        //builder.addInterceptor(loggingInterceptor) // 添加 OkGo 日志拦截器

        OkGo.getInstance().addInterceptor(loggingInterceptor)
            .setRetryCount(3) // 全局的超时重试次数
            .setCertificates()

        //OkGo.getInstance().setCertificates()
        HttpRequest.init(this)
        PreferencesUtils.init(this)
        if (TextUtils.isEmpty(PreferencesUtils.getString(Atts.UID, ""))) {
            PreferencesUtils.setProperty(Atts.UID, UUID.randomUUID().toString())
        }
        BluetoothScannerUtils.init(this)
        initRemote()
        if(WALLPAPERS.isEmpty()){
            if (Config.COMPANY == 0) {
                WALLPAPERS.add(Wallpaper(3, R.drawable.wallpaper_22))
                WALLPAPERS.add(Wallpaper(0, R.drawable.wallpaper_1))
                WALLPAPERS.add(Wallpaper(1, R.drawable.wallpaper_20))
                WALLPAPERS.add(Wallpaper(2, R.drawable.wallpaper_21))
                WALLPAPERS.add(Wallpaper(4, R.drawable.wallpaper_24))
                WALLPAPERS.add(Wallpaper(5, R.drawable.wallpaper_25))
            } else {
                WALLPAPERS.add(Wallpaper(0, R.drawable.wallpaper_1))
                WALLPAPERS.add(Wallpaper(1, R.drawable.wallpaper_20))
                WALLPAPERS.add(Wallpaper(2, R.drawable.wallpaper_21))
                WALLPAPERS.add(Wallpaper(3, R.drawable.wallpaper_22))
                WALLPAPERS.add(Wallpaper(4, R.drawable.wallpaper_24))
                WALLPAPERS.add(Wallpaper(5, R.drawable.wallpaper_25))
            }
        }

        com.hs.App.init(this)
        //AndroidSystem.setEnableBluetooth(this, true)
        timeRemote()


        MvvmHelper.init(this@App)

        ToastUtils.init(this@App)
        ContextManager.getInstance().init(this@App)
        //BRV.modelId = BR.m

        initMultiLanguage(this)

        // 启动定时任务
        // handler.post(runnable)

    }


    private val runnable = object : Runnable {
        override fun run() {
            // 执行定时任务
            performTask()
            // 重新安排下一个任务
            handler.postDelayed(this, interval)
        }
    }


    private fun performTask() {
        // 定时任务逻辑
        println("Task executed at ${System.currentTimeMillis()}")
        /* ((System.currentTimeMillis() - lastRemoteTime)>=5000).yes {
             lastRemoteTime = System.currentTimeMillis()
             if (mFailDialog!!.isShowing) mFailDialog!!.dismiss()
             if (mSuccessDialog!!.isShowing) mSuccessDialog!!.dismiss()
         }*/
    }

    override fun onTerminate() {
        super.onTerminate()
        // 停止定时任务
        handler.removeCallbacks(runnable)
        applicationScope.cancel() // 在应用终止时取消协程}
    }

    private fun timeRemote() {

        if (remoteRunnable != null) remoteRunnable!!.interrupt()
        remoteRunnable = object : MyRunnable() {
            override fun run() {
                while (!isInterrupt) {
                    if (lastRemoteTime == -1L) continue
                    if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastRemoteTime) >= 5) {
                        lastRemoteTime = System.currentTimeMillis()
                        /* if (mFailDialog!!.isShowing) mFailDialog!!.dismiss()
                         if (mSuccessDialog!!.isShowing) mSuccessDialog!!.dismiss()*/
                    }
                }
            }
        }
        exec.execute(remoteRunnable)
    }

    private fun initRemote() {
        mFailDialog = RemoteDialog(this, R.layout.dialog_blu_fail)
        mSuccessDialog = RemoteDialog(this, R.layout.dialog_blu_success)
        receiver = InnerReceiver()
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        filter.addAction(IntentAction.ACTION_NOT_SHOW_REMOTE_DIALOG)
        filter.addAction(IntentAction.ACTION_SHOW_REMOTE_DIALOG)
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
                        2 -> {
                            /* if (!mSuccessDialog!!.isShowing && canDrawOverlays() && useRemoteDialog){
                                 mSuccessDialog!!.show()
                             }
                             if (mFailDialog!!.isShowing) {
                                 mFailDialog!!.dismiss()
                                 mSuccessDialog!!.setName(device?.name)
                                 lastRemoteTime = System.currentTimeMillis()
                             }*/
                            val msg = Message()
                            msg.what = 1
                            handler.sendMessage(msg)


                        }

                        0 -> {
                            if (prevState == 3) {

                                val msg = Message()
                                msg.what = 2
                                handler.sendMessage(msg)

                                /*if (!mFailDialog!!.isShowing && canDrawOverlays() && useRemoteDialog){
                                    mFailDialog!!.show()
                                }
                                if (mSuccessDialog!!.isShowing) {
                                    mSuccessDialog!!.dismiss()
                                    mFailDialog!!.setName(device?.name)
                                    lastRemoteTime = System.currentTimeMillis()
                                }*/


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

                IntentAction.ACTION_SHOW_REMOTE_DIALOG -> useRemoteDialog = true
                IntentAction.ACTION_NOT_SHOW_REMOTE_DIALOG -> useRemoteDialog = false
            }
        }
    }

    private fun canDrawOverlays(): Boolean {
        return Settings.canDrawOverlays(this)
    }

    companion object {
        @JvmStatic
        var instance: App? = null
            private set
        private val exec = Executors.newCachedThreadPool()

        @JvmField
        val MOVIE_MAP: MutableMap<Long, MutableList<Data>> = ConcurrentHashMap()

        /*@JvmField
        val WALLPAPERS: MutableList<Wallpaper> = ArrayList()*/

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
        var useRemoteDialog = true
    }
}
