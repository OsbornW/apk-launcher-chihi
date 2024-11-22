package com.soya.launcher.databinding

import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.soya.launcher.bean.AboutItem
import com.soya.launcher.bean.KeyBoardDto
import com.soya.launcher.bean.Language
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.bean.SimpleTimeZone

object LanguageBindingAdapter {

    @BindingAdapter(value = ["item_language_name"])
    @JvmStatic
    fun itemLanguageName(tvName: TextView, dto: Language) {
        tvName.text = dto.name

    }

    @BindingAdapter(value = ["item_language_desc"])
    @JvmStatic
    fun itemLanguageDesc(tvName: TextView, dto: Language) {
        tvName.text = dto.desc

    }



}