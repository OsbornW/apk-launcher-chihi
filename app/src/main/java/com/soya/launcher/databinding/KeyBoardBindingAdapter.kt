package com.soya.launcher.databinding

import android.text.TextUtils
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.yes
import com.soya.launcher.bean.AppItem
import com.soya.launcher.bean.KeyBoardDto
import com.soya.launcher.h27002.getDrawableByName
import com.soya.launcher.utils.GlideUtils

object KeyBoardBindingAdapter {
    @BindingAdapter(value = ["item_keyboard_icon"])
    @JvmStatic
    fun itemKeyboardIcon(ivIcon: ImageView, dto: KeyBoardDto) {
        ivIcon.isVisible = dto.keyboardIcon!=null
        dto.keyboardIcon?.let { ivIcon.setImageResource(it) }

    }
}