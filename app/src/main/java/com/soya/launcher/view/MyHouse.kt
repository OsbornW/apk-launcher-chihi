package com.soya.launcher.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

class MyHouse @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MyFrameLayout (context, attrs, defStyleAttr) {


    init {
        setOnHoverListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_HOVER_ENTER -> {
                    // 显示悬停效果
                }
                MotionEvent.ACTION_HOVER_EXIT -> {
                    // 隐藏悬停效果
                }
            }
            false
        }

        setOnClickListener {
            // 响应点击事件
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            performClick()
            return true
        }
        return super.onTouchEvent(event)
    }
}
