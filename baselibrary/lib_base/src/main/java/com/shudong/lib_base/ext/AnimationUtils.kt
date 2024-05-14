package com.shudong.lib_base.ext

import android.view.View
import androidx.core.view.ViewCompat


fun View?.animScale( isFocus:Boolean) {

    val scaleX = if (isFocus) 1.05f else 1f
    val scaleY = if (isFocus) 1.05f else 1f
    ViewCompat.animate(this!!)
        .scaleX(scaleX)
        .scaleY(scaleY)
        .translationZ(1f)
        .start()
}

object AnimationUtils