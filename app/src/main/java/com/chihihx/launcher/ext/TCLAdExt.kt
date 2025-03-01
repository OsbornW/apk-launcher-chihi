package com.chihihx.launcher.ext

import android.content.Context
import android.view.View
import androidx.lifecycle.LifecycleOwner

// 初始化 Ad 和 Controller
fun Context.initializeAd(
    rlAD: View,
    lifecycleOwner: LifecycleOwner
) {
    loadAAR()
    val adClassName = "com.tcl.ff.component.vastad.Ad"
    val controllerClassName = "com.tcl.ff.component.vastad.controller.Controller"
    try {
        val adClass = getAARClassInstance(adClassName) ?: return
        val adClassType = adClass::class.java

        val getMethod = adClassType.getMethod("get")
        val adInstance = getMethod.invoke(null)

        val setEnableLogMethod =
            adClassType.getMethod("setEnableLog", Boolean::class.javaPrimitiveType)
        setEnableLogMethod.invoke(adInstance, true)

        val beginMethod = adClassType.getMethod("begin", Context::class.java)
        val adController = beginMethod.invoke(adInstance, this)

        val containerMethod = adClassType.getMethod("container", View::class.java)
        containerMethod.invoke(adController, rlAD)

        val lifecycleOwnerMethod =
            adClassType.getMethod("lifecycleOwner", LifecycleOwner::class.java)
        lifecycleOwnerMethod.invoke(adController, lifecycleOwner)

        // 保存 adController 和 controllerClassName 供后续使用
        AdControllerState.adController = adController
        AdControllerState.controllerClassName = controllerClassName

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// 启动广告
fun Context.startAd() {
    AdControllerState.adController?.let { adController ->
        try {
            val controllerClass =
                getAARClassInstance(AdControllerState.controllerClassName) ?: return
            val controllerClassType = controllerClass::class.java

            val startMethod = controllerClassType.getMethod("start", View::class.java)
            startMethod.invoke(adController, AdControllerState.adView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

// 暂停广告
fun Context.pauseAd() {
    AdControllerState.adController?.let { adController ->
        try {
            val controllerClass =
                getAARClassInstance(AdControllerState.controllerClassName) ?: return
            val controllerClassType = controllerClass::class.java

            val pauseMethod = controllerClassType.getMethod("pause")
            pauseMethod.invoke(adController)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

// 状态对象来保存 adController 和相关信息
object AdControllerState {
    var adController: Any? = null
    var adView: View? = null
    var controllerClassName: String = ""
}
