package com.thumbsupec.lib_base.cllback_kt

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/10/31 09:28
 * @PACKAGE_NAME:  com.thumbsupec.login
 */
abstract class SpanClick : ClickableSpan() {
    private var click: ((widget: View) -> Unit)? = null
    private var updateView: ((ds: TextPaint) -> Unit)? = null
    fun click(click: ((widget: View) -> Unit)){
        this.click = click
    }
    fun updateView(updateView: ((ds: TextPaint) -> Unit)){
        this.updateView = updateView
    }
    override fun onClick(widget: View) {
        click?.invoke(widget)
    }

    override fun updateDrawState(ds: TextPaint) {
        updateView?.invoke(ds)
    }
}