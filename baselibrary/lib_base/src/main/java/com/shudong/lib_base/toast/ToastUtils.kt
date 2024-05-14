package com.thumbsupec.lib_base.toast

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.res.Resources.NotFoundException
import android.view.View
import com.shudong.lib_base.toast.config.IToastInterceptor
import com.shudong.lib_base.toast.config.IToastStrategy
import com.shudong.lib_base.toast.config.IToastStyle
import com.thumbsupec.lib_base.toast.style.BlackToastStyle
import com.thumbsupec.lib_base.toast.style.LocationToastStyle
import com.thumbsupec.lib_base.toast.style.ViewToastIdStyle
import com.thumbsupec.lib_base.toast.style.ViewToastViewStyle

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:   Toast 框架
 * @Author:  zengyue
 * @Date:  2022/10/29
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.toast
 */
object ToastUtils {
    /** Application 对象  */
    private var sApplication: Application? = null

    /** Toast 处理策略  */
    private var sToastStrategy: IToastStrategy? = null

    /** Toast 样式  */
    private var sToastStyle: IToastStyle<*>? = null
    /**
     * 设置 Toast 拦截器（可以根据显示的内容决定是否拦截这个Toast）
     * 场景：打印 Toast 内容日志、根据 Toast 内容是否包含敏感字来动态切换其他方式显示（这里可以使用我的另外一套框架 XToast）
     */
    /** Toast 拦截器（可空）  */
    var interceptor: IToastInterceptor? = null

    /** 调试模式  */
    private var sDebugMode: Boolean? = null

    /**
     * 初始化 Toast，需要在 Application.create 中初始化
     *
     * @param application       应用的上下文
     */
    @JvmOverloads
    fun init(application: Application?, style: IToastStyle<*>? = sToastStyle) {
        init(application, null, style)
    }

    /**
     * 初始化 Toast
     *
     * @param application       应用的上下文
     * @param strategy          Toast 策略
     * @param style             Toast 样式
     */
    @JvmOverloads
    fun init(application: Application?, strategy: IToastStrategy?, style: IToastStyle<*>? = null) {
        var strategy = strategy
        var style = style
        sApplication = application

        // 初始化 Toast 策略
        if (strategy == null) {
            strategy = ToastStrategy()
        }
        ToastUtils.strategy = strategy

        // 设置 Toast 样式
        if (style == null) {
            style = BlackToastStyle()
        }
        ToastUtils.style = style
    }

    /**
     * 判断当前框架是否已经初始化
     */
    val isInit: Boolean
        get() = sApplication != null && sToastStrategy != null && sToastStyle != null

    /**
     * 延迟显示 Toast
     */
    fun delayedShow(id: Int, delayMillis: Long) {
        show(id, delayMillis)
    }

    fun delayedShow(text: CharSequence?, delayMillis: Long) {
        show(text, delayMillis)
    }

    fun delayedShow(`object`: Any?, delayMillis: Long) {
        show(`object`, delayMillis)
    }

    /**
     * debug 模式下显示 Toast
     */
    fun debugShow(id: Int) {
        if (!isDebugMode) {
            return
        }
        show(id, 0)
    }

    fun debugShow(text: CharSequence?) {
        if (!isDebugMode) {
            return
        }
        show(text, 0)
    }

    fun debugShow(`object`: Any?) {
        if (!isDebugMode) {
            return
        }
        show(`object`, 0)
    }

    /**
     * 显示 Toast
     */
    fun show(id: Int) {
        show(id, 0)
    }

    fun show(`object`: Any?) {
        show(`object`, 0)
    }

    fun show(text: CharSequence?) {
        show(text, 0)
    }

    private fun show(id: Int, delayMillis: Long) {
        try {
            // 如果这是一个资源 id
            show(sApplication!!.resources.getText(id))
        } catch (ignored: NotFoundException) {
            // 如果这是一个 int 整数
            show(id.toString())
        }
    }

    private fun show(`object`: Any?, delayMillis: Long) {
        show(`object`?.toString() ?: "null", delayMillis)
    }

    private fun show(text: CharSequence?, delayMillis: Long) {
        // 如果是空对象或者空文本就不显示
        if (text == null || text.length == 0) {
            return
        }
        if (interceptor == null) {
            interceptor = ToastLogInterceptor()
        }
        if (interceptor!!.intercept(text)) {
            return
        }
        sToastStrategy!!.showToast(text, delayMillis)
    }

    /**
     * 取消吐司的显示
     */
    fun cancel() {
        sToastStrategy!!.cancelToast()
    }

    /**
     * 设置吐司的位置
     *
     * @param gravity           重心
     */
    fun setGravity(gravity: Int) {
        setGravity(gravity, 0, 0)
    }

    fun setGravity(gravity: Int, xOffset: Int, yOffset: Int) {
        setGravity(gravity, xOffset, yOffset, 0f, 0f)
    }

    fun setGravity(
        gravity: Int,
        xOffset: Int,
        yOffset: Int,
        horizontalMargin: Float,
        verticalMargin: Float
    ) {
        sToastStrategy!!.bindStyle(
            LocationToastStyle(
                sToastStyle!!,
                gravity,
                xOffset,
                yOffset,
                horizontalMargin,
                verticalMargin
            )
        )
    }

    /**
     * 给当前 Toast 设置新的布局
     */
    fun setView(id: Int) {
        if (id <= 0) {
            return
        }
        style = ViewToastIdStyle(id, sToastStyle)
    }

    fun setView(layoutView: View?) {
        if (layoutView == null) {
            return
        }
        style = ViewToastViewStyle(layoutView, sToastStyle)
    }

    /**
     * 初始化全局的 Toast 样式
     *
     * @param style         样式实现类，框架已经实现两种不同的样式
     * 黑色样式：[BlackToastStyle]
     * 白色样式：[WhiteToastStyle]
     */
    var style: IToastStyle<*>?
        get() = sToastStyle
        set(style) {
            sToastStyle = style
            sToastStrategy!!.bindStyle(style)
        }

    /**
     * 设置 Toast 显示策略
     */
    var strategy: IToastStrategy?
        get() = sToastStrategy
        set(strategy) {
            sToastStrategy = strategy
            sToastStrategy!!.registerStrategy(sApplication)
        }

    /**
     * 是否为调试模式
     */
    var isDebugMode: Boolean
        get() {
            if (sDebugMode == null) {
                sDebugMode =
                    sApplication!!.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
            }
            return sDebugMode!!
        }
        set(debug) {
            sDebugMode = debug
        }
}