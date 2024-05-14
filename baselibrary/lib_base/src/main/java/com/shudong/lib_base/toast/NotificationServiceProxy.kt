package com.thumbsupec.lib_base.toast

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:   通知服务代理 代理对象
 * @Author:  zengyue
 * @Date:  2022/10/29
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.toast
 */
internal class NotificationServiceProxy(
    /** 被代理的对象  */
    private val mSource: Any
) : InvocationHandler {
    @Throws(Throwable::class)
    override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any {
        when (method.name) {
            "enqueueToast", "enqueueToastEx", "cancelToast" ->                 // 将包名修改成系统包名，这样就可以绕过系统的拦截
                // 部分华为机将 enqueueToast 修改成了 enqueueToastEx
                args[0] = "android"
            else -> {}
        }
        // 使用动态代理
        return method.invoke(mSource, *args)
    }
}