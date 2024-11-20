package com.soya.launcher.databinding

import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.soya.launcher.bean.KeyBoardDto

object KeyBoardBindingAdapter {
    @BindingAdapter(value = ["item_keyboard_icon"])
    @JvmStatic
    fun itemKeyboardIcon(ivIcon: ImageView, dto: KeyBoardDto) {
        ivIcon.isVisible = dto.keyboardIcon!=null
        dto.keyboardIcon?.let { ivIcon.setImageResource(it) }

    }
}