package com.soya.launcher.view

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent

open class AppLayout : MyFrameLayout {
    private var listener: EventListener? = null

    constructor(context: Context) : super(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (listener != null) listener!!.onKeyDown(keyCode, event)
        return super.onKeyUp(keyCode, event)
    }

    fun setListener(listener: EventListener?) {
        this.listener = listener
    }

    fun interface EventListener {
        fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean
    }
}
