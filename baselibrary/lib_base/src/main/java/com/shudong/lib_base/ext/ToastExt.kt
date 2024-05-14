package com.shudong.lib_base.ext

import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.shudong.lib_base.R
import com.thumbsupec.lib_base.toast.ToastUtils

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/10/28 11:45
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.ext
 */

fun showSuccessToast(msg:String){
    val view = LayoutInflater.from(appContext).inflate(R.layout.layout_toast,null)
    val tvMsg = view.findViewById<TextView>(android.R.id.message)
    val ivStatus = view.findViewById<ImageView>(R.id.iv_status)
    ivStatus.setImageResource(R.drawable.icon_toast_success)
    tvMsg.setTextColor(appContext.getColor(com.shudong.lib_res.R.color.color_06CB86))
    ToastUtils.setView(view)
    appContext.resources.getDimensionPixelSize(com.shudong.lib_dimen.R.dimen.qb_px_10)
        .let { ToastUtils.setGravity(Gravity.TOP, 0, it) }
    ToastUtils.show(msg)

}

fun showWarningToast(msg:String){
    val view = LayoutInflater.from(appContext).inflate(R.layout.layout_toast,null)
    val tvMsg = view.findViewById<TextView>(android.R.id.message)
    val ivStatus = view.findViewById<ImageView>(R.id.iv_status)
    ivStatus.setImageResource(R.drawable.icon_toast_warning)
    tvMsg.setTextColor(appContext.getColor(com.shudong.lib_res.R.color.color_E6AC30))
    ToastUtils.setView(view)
    appContext.resources.getDimensionPixelSize(com.shudong.lib_dimen.R.dimen.qb_px_10)
        .let { ToastUtils.setGravity(Gravity.TOP, 0, it) }
    ToastUtils.show(msg)

}

fun showErrorToast(msg:String){
    val view = LayoutInflater.from(appContext).inflate(R.layout.layout_toast,null)
    val tvMsg = view.findViewById<TextView>(android.R.id.message)
    val ivStatus = view.findViewById<ImageView>(R.id.iv_status)
    ivStatus.setImageResource(R.drawable.icon_toast_error)
    tvMsg.setTextColor(appContext.getColor(com.shudong.lib_res.R.color.color_FF323C))
    ToastUtils.setView(view)
    appContext.resources.getDimensionPixelSize(com.shudong.lib_dimen.R.dimen.qb_px_10)
        .let { ToastUtils.setGravity(Gravity.TOP, 0, it) }
    ToastUtils.show(msg)
}

fun showTipToast(msg:String){
    val view = LayoutInflater.from(appContext).inflate(R.layout.layout_toast,null)
    val tvMsg = view.findViewById<TextView>(android.R.id.message)
    val ivStatus = view.findViewById<ImageView>(R.id.iv_status)
    ivStatus.isVisible = false
    tvMsg.setTextColor(appContext.getColor(com.shudong.lib_res.R.color.color_585CE5))
    ToastUtils.setView(view)
    appContext.resources.getDimensionPixelSize(com.shudong.lib_dimen.R.dimen.qb_px_10)
        .let { ToastUtils.setGravity(Gravity.TOP, 0, it) }
    ToastUtils.show(msg)

}