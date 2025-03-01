package com.chihihx.launcher.databinding

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.chihihx.launcher.bean.SettingItem

object ProjectorBindAdapter {

    @BindingAdapter(value = ["item_projector_icon"])
    @JvmStatic
    fun itemProjectorIcon(ivIcon: ImageView, dto: SettingItem) {
       ivIcon.setImageResource(dto.ico)
    }

    @BindingAdapter(value = ["item_projector_name"])
    @JvmStatic
    fun itemProjectorName(tvName: TextView, dto: SettingItem) {
        tvName.text = dto.name
    }


}