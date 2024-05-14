package com.shudong.lib_base.databinding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.shudong.lib_base.ext.loadFileRadius
import com.shudong.lib_base.ext.loadRadius
import java.io.File

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

    @BindingAdapter(value = ["imgLocal"])
    @JvmStatic
    fun loadLocalResource(iv: ImageView, path: String?) {
        path?.let {
            iv.loadFileRadius(path)
        }

    }



}