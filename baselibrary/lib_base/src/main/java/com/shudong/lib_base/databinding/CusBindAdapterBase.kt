package com.shudong.lib_base.databinding

import android.widget.ImageView
import androidx.databinding.BindingAdapter

object CusBindAdapterBase {

    /**
     * 设置本地图片资源
     */
    @BindingAdapter(value = ["imgResource"])
    @JvmStatic
    fun loadLocalResource(iv: ImageView, resource: Int?) {
        resource?.let {
            iv.setImageResource(resource)
        }

    }




}