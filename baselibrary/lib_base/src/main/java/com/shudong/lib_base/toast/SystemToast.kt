package com.thumbsupec.lib_base.toast

import android.app.Application
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.shudong.lib_base.toast.config.IToast

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:   系统 Toast
 * @Author:  zengyue
 * @Date:  2022/10/29
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.toast
 */
open class SystemToast(application: Application?) : Toast(application), IToast {
    /** 吐司消息 View  */
    private var mMessageView: TextView? = null
    override fun setView(view: View) {
        super.setView(view)
        if (view == null) {
            mMessageView = null
            return
        }
        mMessageView = findMessageView(view)
    }

    override fun setText(text: CharSequence) {
        super.setText(text)
        if (mMessageView == null) {
            return
        }
        mMessageView!!.text = text
    }

}