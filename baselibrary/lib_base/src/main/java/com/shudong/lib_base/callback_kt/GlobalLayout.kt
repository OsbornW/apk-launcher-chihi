package com.thumbsupec.lib_base.cllback_kt

import android.view.ViewTreeObserver.OnGlobalLayoutListener

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/11/10 09:45
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.cllback_kt
 */
open class GlobalLayout:OnGlobalLayoutListener {
    private var listener:(()->Unit)?=null
    fun globalLayout(listener:(()->Unit)){
        this.listener = listener
    }
    override fun onGlobalLayout() {
        listener?.invoke()
    }
}