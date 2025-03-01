package com.chihihx.launcher.databinding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.chihihx.launcher.bean.Language

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