package com.soya.launcher

import android.os.Bundle
import android.text.TextUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.lifecycleScope
import com.chihi.m98.hook.JsonSerializeHook
import com.drake.serialize.serialize.Serialize
import com.lzy.okgo.OkGo
import com.lzy.okgo.interceptor.HttpLoggingInterceptor
import com.shudong.lib_base.ContextManager
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.base.viewmodel.baseModules
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.startKtxActivity
import com.soya.launcher.cache.AppCache
import com.soya.launcher.databinding.ActivitySplashBinding
import com.soya.launcher.enums.Atts
import com.soya.launcher.http.HttpRequest
import com.soya.launcher.net.di.homeModules
import com.soya.launcher.product.base.product
import com.soya.launcher.ui.activity.MainActivity
import com.soya.launcher.utils.BluetoothScannerUtils
import com.soya.launcher.utils.PreferencesUtils
import com.tencent.mmkv.MMKV
import com.thumbsupec.lib_base.ext.language.initMultiLanguage
import com.thumbsupec.lib_base.toast.ToastUtils
import com.thumbsupec.lib_net.AppCacheNet
import com.thumbsupec.lib_net.di.netModules
import com.thumbsupec.lib_net.http.MyX509TrustManager
import com.thumbsupec.lib_net.http.getSSLContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import rxhttp.RxHttpPlugins
import java.util.UUID
import java.util.logging.Level

class SplashActivity : BaseVMMainActivity<ActivitySplashBinding, BaseViewModel>() {

    override fun initView() {
        //"开始应用启动3".e("zengyue3")
        initApp()
    }

    private fun initApp() {
        //delay(1000)
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                if (!AppCache.isAppInited) {
                    //"开始应用启动4".e("zengyue3")
                    MMKV.initialize(appContext)
                    Serialize.hook = JsonSerializeHook()

                    AppCacheNet.baseUrl = BuildConfig.BASE_URL
                    AppCache.isGuidChageLanguage = false

                    ToastUtils.init(appContext)
                    ContextManager.getInstance().init(appContext)

                    startKoin {
                        androidContext(appContext)
                        modules(netModules)
                        //modules(appLoginModule)
                        modules(baseModules)
                        modules(homeModules)
                    }

                    val sslContext = getSSLContext()
                    val sslSocketFactory = sslContext.socketFactory
                    RxHttpPlugins.init(
                        OkHttpClient.Builder()
                            .sslSocketFactory(sslSocketFactory, MyX509TrustManager())
                            .hostnameVerifier { _, _ -> true }
                            .build())
                    OkGo.init(appContext)
                    // 配置日志拦截器
                    val loggingInterceptor = HttpLoggingInterceptor("zengyue")
                    loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY) // 设置打印级别
                    loggingInterceptor.setColorLevel(Level.INFO) // 设置颜色级别
                    //builder.addInterceptor(loggingInterceptor) // 添加 OkGo 日志拦截器

                    OkGo.getInstance().addInterceptor(loggingInterceptor)
                        .setRetryCount(3) // 全局的超时重试次数
                        .setCertificates()

                    HttpRequest.init(appContext)
                    if (TextUtils.isEmpty(PreferencesUtils.getString(Atts.UID, ""))) {
                        PreferencesUtils.setProperty(Atts.UID, UUID.randomUUID().toString())
                    }
                    com.hs.App.init(appContext)
                    //BRV.modelId = BR.m
                    initMultiLanguage(appContext)
                    product.addWallPaper()
                    //"开始应用启动5".e("zengyue3")
                    AppCache.isAppInited = true
                    finish()
                }
            }

        }

        lifecycleScope.launch {
            startKtxActivity<MainActivity>()
        }



    }

}
