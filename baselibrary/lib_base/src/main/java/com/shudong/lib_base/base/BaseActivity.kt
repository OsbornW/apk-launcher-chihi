package com.shudong.lib_base.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.KeyboardUtils
import com.shudong.lib_base.R
import com.shudong.lib_base.databinding.ViewRootBinding
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.colorValue
import com.shudong.lib_base.ext.loading.showLoadingView
import com.shudong.lib_base.ext.loading.showLoadingViewDismiss
import com.thumbsupec.lib_base.ext.language.changeContextLocale
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.util.Locale

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/11/2 17:31
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.base
 */
abstract class BaseActivity<VDB : ViewDataBinding> : FragmentActivity(), BaseVMView {


    /**
     * 是否显示TitleBar，默认不显示
     */
    open fun isShowTitlerBar(): Boolean = false

    open fun isUseImmerBar(): Boolean = false

    fun Int.setBackRes() {
        inflate.findViewById<ImageView>(R.id.iv_back).setImageResource(this)
    }

    fun String.setTitlebarText() {
        inflate.findViewById<TextView>(R.id.tv_title).text = this
    }

    fun Int.setTitlebarTextColor() {
        inflate.findViewById<TextView>(R.id.tv_title).setTextColor(this.colorValue())
    }


    fun Int.setTitlebarBg() {
        inflate.findViewById<RelativeLayout>(R.id.rl_titlebar).setBackgroundColor(this.colorValue())
    }

    fun String.setTitlebarRightText(clickListener: () -> Unit) {
        val tvRight = inflate.findViewById<TextView>(R.id.tv_right)
        tvRight.text = this
        tvRight.clickNoRepeat {
            clickListener.invoke()
        }
    }

    val rootBinding: ViewRootBinding by lazy {
        ViewRootBinding.inflate(layoutInflater)
    }

    protected val mBind: VDB by lazy {
        var type = javaClass.genericSuperclass
        var vbClass: Class<VDB> = (type as ParameterizedType).actualTypeArguments[0] as Class<VDB>
        val method = vbClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        method.invoke(this, layoutInflater) as VDB
    }

    override fun setContentView(view: View?) {
        rootBinding.layoutBody.addView(
            view, 0, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    lateinit var inflate: View
    override fun onCreate(savedInstanceState: Bundle?) {
        initBeforeContent()
        super.setContentView(rootBinding.root)
        super.onCreate(savedInstanceState)
        initBeforeBaseLayout()
    }

     fun loadLayout() {
        setContentView(mBind.root)
        initPage()
    }


    fun initPage() {
        initView()
        initdata()
        initClick()
        initObserver()
        handleSoftInput()
    }

    fun statusBarFitWindow(isFitWindow: Boolean) {
       /* when (isFitWindow) {
            true -> statusBarOnly {
                fitWindow = true
                colorRes = com.shudong.lib_res.R.color.color_white
                light = true
            }
            false -> statusBarOnly {
                transparent()
                light = true
            }
        }*/

    }

    fun registerSoftInput(activity: AppCompatActivity, callback: (height: Int) -> Unit) {
        KeyboardUtils.registerSoftInputChangedListener(activity) {
            callback.invoke(it)
        }
    }

    fun unRegisterSoftInput(activity: AppCompatActivity) {
        KeyboardUtils.unregisterSoftInputChangedListener(activity.window)
    }

    override fun onDestroy() {
        super.onDestroy()
        closeSoftInput()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(changeContextLocale(newBase))
    }

    var title = ""

    fun titlerBar(bar: ViewRootBinding.() -> Unit) = run {
        bar.invoke(rootBinding)
        rootBinding
        //inflate.findViewById<TextView>(R.id.tv_title).text = title

    }

    override fun loadingView(msg: String) {
        showLoadingView(msg)
    }

    override fun hideLoading() {
        showLoadingViewDismiss()
    }

    open fun setLocale(locale: Locale?) {
        Locale.setDefault(locale)
        val context: Context = appContext
        val resources: Resources = context.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(
            config,
            resources.displayMetrics
        )
    }

    override fun moveTaskToBack(nonRoot: Boolean): Boolean {
        return super.moveTaskToBack(true)
    }



    @SuppressLint("SoonBlockedPrivateApi")
    fun hookWebView() {
        val sdkInt = Build.VERSION.SDK_INT
        try {
            val factoryClass = Class.forName("android.webkit.WebViewFactory")
            val field = factoryClass.getDeclaredField("sProviderInstance")
            field.isAccessible = true
            var sProviderInstance = field.get(null)
            if (sProviderInstance != null) {
                return // 如果 sProviderInstance 已存在，直接返回
            }

            // 根据 SDK 版本选择方法
            val getProviderClassMethod = when {
                sdkInt > 22 -> factoryClass.getDeclaredMethod("getProviderClass")
                sdkInt == 22 -> factoryClass.getDeclaredMethod("getFactoryClass")
                else -> return // 不支持低于 22 的版本
            }
            getProviderClassMethod.isAccessible = true
            val factoryProviderClass = getProviderClassMethod.invoke(factoryClass) as Class<*>

            // 获取 WebViewDelegate 类并创建实例
            val delegateClass = Class.forName("android.webkit.WebViewDelegate")
            val delegateConstructor = delegateClass.getDeclaredConstructor()
            delegateConstructor.isAccessible = true

            // 根据 SDK 版本创建 sProviderInstance
            sProviderInstance = if (sdkInt < 26) {
                val providerConstructor = factoryProviderClass.getConstructor(delegateClass)
                providerConstructor.isAccessible = true
                providerConstructor.newInstance(delegateConstructor.newInstance())
            } else {
                val chromiumMethodName = factoryClass.getDeclaredField("CHROMIUM_WEBVIEW_FACTORY_METHOD")
                chromiumMethodName.isAccessible = true
                val methodName = chromiumMethodName.get(null) as? String ?: "create"
                val staticFactory = factoryProviderClass.getMethod(methodName, delegateClass)
                staticFactory.invoke(null, delegateConstructor.newInstance())
            }

            // 设置 sProviderInstance
            if (sProviderInstance != null) {
                field.set(null, sProviderInstance)
            } else {
                Log.e("WebViewHook", "Failed to create sProviderInstance")
            }
        } catch (e: Throwable) {
            Log.e("WebViewHook", "Error during WebView hooking", e)
        }
    }


}