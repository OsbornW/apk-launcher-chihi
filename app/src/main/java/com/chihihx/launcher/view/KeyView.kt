package com.chihihx.launcher.view

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import androidx.appcompat.widget.AppCompatImageView

class KeyView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    AppCompatImageView(context, attrs) {
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return event.keyCode == 21 || super.onKeyDown(keyCode, event)
    }
}
