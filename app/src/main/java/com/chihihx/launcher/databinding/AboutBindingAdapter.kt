package com.chihihx.launcher.databinding

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.chihihx.launcher.bean.AboutItem

object AboutBindingAdapter {

    @BindingAdapter(value = ["item_about_icon"])
    @JvmStatic
    fun itemAboutIcon(ivIcon: ImageView, dto: AboutItem) {
        ivIcon.setImageResource(dto.icon)

    }

    @BindingAdapter(value = ["item_about_title"])
    @JvmStatic
    fun itemAboutTitle(tvName: TextView, dto: AboutItem) {
        tvName.text = dto.title

    }

    @BindingAdapter(value = ["item_about_desc"])
    @JvmStatic
    fun itemAboutDesc(tvName: TextView, dto: AboutItem) {
        tvName.text = dto.description

    }

}