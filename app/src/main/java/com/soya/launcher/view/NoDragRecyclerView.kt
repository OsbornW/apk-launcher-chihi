package com.soya.launcher.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class NoDragRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        // 检查是否为鼠标拖拽事件
        if (e?.action == MotionEvent.ACTION_MOVE) {
            // 返回 true 以拦截触摸事件，禁止拖拽
            return true
        }
        return super.onInterceptTouchEvent(e)
    }
}
