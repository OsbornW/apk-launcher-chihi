package com.shudong.lib_base.ext

import androidx.annotation.ColorRes
import com.hjq.shape.view.ShapeTextView

fun ShapeTextView.changeBG(@ColorRes color: Int) {
    val builder = this.shapeDrawableBuilder
    builder.solidColor = color.colorValue()
    builder.intoBackground()
}