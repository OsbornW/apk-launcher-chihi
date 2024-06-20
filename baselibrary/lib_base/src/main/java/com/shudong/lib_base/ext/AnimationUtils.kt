package com.shudong.lib_base.ext

import android.view.View
import androidx.core.view.ViewCompat


fun View?.animScale( isFocus:Boolean,radis:Float = 1.05f) {

    val scaleX = if (isFocus) radis else 1f
    val scaleY = if (isFocus) radis else 1f
    ViewCompat.animate(this!!)
        .scaleX(scaleX)
        .scaleY(scaleY)
        .translationZ(1f)
        .start()
}

object AnimationUtils