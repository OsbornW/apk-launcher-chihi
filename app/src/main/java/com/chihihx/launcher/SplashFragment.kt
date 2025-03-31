package com.chihihx.launcher

import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.lifecycleScope
import com.shudong.lib_base.ContextManager
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.appContext
import com.chihihx.launcher.cache.AppCache
import com.chihihx.launcher.databinding.ActivitySplashBinding
import com.chihihx.launcher.enums.Atts
import com.chihihx.launcher.ext.navigateTo
import com.chihihx.launcher.product.base.product
import com.chihihx.launcher.ui.fragment.PrivacyPolicyFragment
import com.chihihx.launcher.utils.PreferencesUtils
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.replaceFragment
import com.thumbsupec.lib_base.ext.language.initMultiLanguage
import com.thumbsupec.lib_base.toast.ToastUtils
import com.thumbsupec.lib_net.http.MyX509TrustManager
import com.thumbsupec.lib_net.http.getSSLContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import rxhttp.RxHttpPlugins
import java.util.UUID

class SplashFragment : BaseWallPaperFragment<ActivitySplashBinding, BaseViewModel>() {
    override fun initView() {
        //"开始应用启动3".e("zengyue3")
        initApp()
    }

    var job: Job? = null
    var job1: Job? = null
    private fun initApp() {
        //delay(1000)
        job?.cancel()
        job = lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (!AppCache.isAppInited) {
                    //"开始应用启动4".e("zengyue3")

                   // AppCacheNet.baseUrl = BuildConfig.BASE_URL
                    AppCache.isGuidChageLanguage = false

                    ToastUtils.init(appContext)
                    ContextManager.getInstance().init(appContext)

                    val sslContext = getSSLContext()
                    val sslSocketFactory = sslContext.socketFactory
                    RxHttpPlugins.init(
                        OkHttpClient.Builder()
                            .sslSocketFactory(sslSocketFactory, MyX509TrustManager())
                            .hostnameVerifier { _, _ -> true }
                            .build())


                    if (TextUtils.isEmpty(PreferencesUtils.getString(Atts.UID, ""))) {
                        PreferencesUtils.setProperty(Atts.UID, UUID.randomUUID().toString())
                    }
                    //com.hs.App.init(appContext)
                    //BRV.modelId = BR.m
                    initMultiLanguage(appContext)
                    //"开始应用启动5".e("zengyue3")
                    AppCache.isAppInited = true
                    //finish()
                }
            }

        }

        job1?.cancel()
        job1 = lifecycleScope.launch {
            while (isActive) { // 确保协程在生命周期内运行
                delay(100) // 每 100 毫秒检测一次
                if (localWallPaperDrawable != null) {
                    withContext(Dispatchers.Main) { // 在主线程中启动 Activity
                        enterHome()


                    }
                    break // 检测到条件满足后退出循环
                }
            }
        }
    }

    private fun enterHome() {
       /* AppCache.isPrivacyPolicyAgreed.no {
            requireActivity().replaceFragment(PrivacyPolicyFragment(), R.id.main_browse_fragment)
        }.otherwise {*/
            product.switchFragment()?.let {
                requireActivity().supportFragmentManager.navigateTo(R.id.main_browse_fragment, it)
                //requireActivity().replaceFragment(it, R.id.main_browse_fragment)
            }
        //}

    }

    companion object {
        @JvmStatic
        fun newInstance(): SplashFragment {
            val args = Bundle()

            val fragment = SplashFragment()
            fragment.arguments = args
            return fragment
        }
    }
}