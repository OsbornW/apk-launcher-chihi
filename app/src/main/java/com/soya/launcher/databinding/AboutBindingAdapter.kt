package com.soya.launcher.databinding

import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.soya.launcher.bean.AboutItem
import com.soya.launcher.bean.KeyBoardDto
import com.soya.launcher.bean.SettingItem

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