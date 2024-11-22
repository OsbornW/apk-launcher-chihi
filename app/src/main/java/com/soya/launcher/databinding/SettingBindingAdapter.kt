package com.soya.launcher.databinding

import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.soya.launcher.bean.KeyBoardDto
import com.soya.launcher.bean.SettingItem

object SettingBindingAdapter {

    @BindingAdapter(value = ["item_setting_icon"])
    @JvmStatic
    fun itemSettingIcon(ivIcon: ImageView, dto: SettingItem) {
        ivIcon.setImageResource(dto.ico)

    }

    @BindingAdapter(value = ["item_setting_text"])
    @JvmStatic
    fun itemSettingText(tvName: TextView, dto: SettingItem) {
        tvName.text = dto.name

    }

}