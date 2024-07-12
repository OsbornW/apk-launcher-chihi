package com.soya.launcher.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

class AppLayoutHouse @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppLayout(context, attrs) {

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            performClick()
            return true
        }
        return super.onTouchEvent(event)
    }
}
