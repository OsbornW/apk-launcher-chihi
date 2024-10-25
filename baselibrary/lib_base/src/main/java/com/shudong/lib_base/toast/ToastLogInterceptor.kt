package com.thumbsupec.lib_base.toast

import android.util.Log
import com.shudong.lib_base.toast.config.IToastInterceptor
import java.lang.reflect.Modifier

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:   自定义 Toast 拦截器（用于追踪 Toast 调用的位置）
 * @Author:  zengyue
 * @Date:  2022/10/29
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.toast
 */
class ToastLogInterceptor : IToastInterceptor {
    override fun intercept(text: CharSequence): Boolean {
        printToast(text)
        return false
    }

    protected fun printToast(text: CharSequence) {
        if (!isLogEnable) {
            return
        }

        // 获取调用的堆栈信息
        val stackTraces = Throwable().stackTrace
        for (stackTrace in stackTraces) {
            // 获取代码行数
            val lineNumber = stackTrace.lineNumber
            if (lineNumber <= 0) {
                continue
            }

            // 获取类的全路径
            val className = stackTrace.className
            try {
                val clazz = Class.forName(className)
                if (!filterClass(clazz)) {
                    printLog("(" + stackTrace.fileName + ":" + lineNumber + ") " + text.toString())
                    break
                }
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    protected val isLogEnable: Boolean
        protected get() = ToastUtils.isDebugMode

    protected fun printLog(msg: String?) {
        // 这里解释一下，为什么不用 Log.d，而用 Log.i，因为 Log.d 在魅族 16th 手机上面无法输出日志
        Log.i("ToastUtils", msg!!)
    }

    protected fun filterClass(clazz: Class<*>): Boolean {
        // 排除自身
        if (ToastLogInterceptor::class.java == clazz) {
            return true
        }

        // 排除 ToastUtils 类
        if (ToastUtils::class.java == clazz) {
            return true
        }

        // 是否为接口类
        if (clazz.isInterface) {
            return true
        }

        // 是否为抽象类
        return Modifier.isAbstract(clazz.modifiers)
    }
}