package com.soya.launcher.view

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent

class KeyEventFrameLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    MyFrameLayout(context, attrs) {
    private var keyEventCallback: KeyEventCallback? = null

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (keyEventCallback != null) keyEventCallback!!.onKeyDown(event)
        return super.dispatchKeyEvent(event)
    }

    fun setKeyEventCallback(keyEventCallback: KeyEventCallback?) {
        this.keyEventCallback = keyEventCallback
    }

    interface KeyEventCallback {
        fun onKeyDown(event: KeyEvent?)
    }
}
