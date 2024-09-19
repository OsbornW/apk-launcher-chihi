package com.soya.launcher.databinding

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.shudong.lib_base.ext.clickNoRepeat
import com.soya.launcher.bean.Notify
import com.soya.launcher.product.base.product
import com.soya.launcher.utils.GlideUtils

object NotifyBindAdapter {

    /**
     * 首页通知列表的Item图片
     */
    @BindingAdapter(value = ["item_notify_icon"])
    @JvmStatic
    fun itemNotifyIcon(ivIcon: ImageView, dto: Notify) {
        GlideUtils.bind( ivIcon, dto.icon)

    }

    /**
     * 首页通知列表的Item点击事件
     */
    @BindingAdapter(value = ["item_notify_click"])
    @JvmStatic
    fun itemNotifyClick(view: View, dto: Notify) {
        view.clickNoRepeat {
            when(dto.type) {
                Notify.TYPE_UDisk, Notify.TYPE_TF -> {
                    product.openFileManager()
                }
            }
        }

    }

}