package com.thumbsupec.lib_base.toast

import android.app.Activity

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:   利用Activity弹出 Toast
 * @Author:  zengyue
 * @Date:  2022/10/29
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.toast
 */
class ActivityToast(activity: Activity?) : CustomToast() {
    /** Toast 实现类  */
    private val mToastImpl: ToastImpl

    init {
        mToastImpl = ToastImpl(activity!!, this)
    }

    override fun show() {
        // 替换成 WindowManager 来显示
        mToastImpl.show()
    }

    override fun cancel() {
        // 取消 WindowManager 的显示
        mToastImpl.cancel()
    }
}