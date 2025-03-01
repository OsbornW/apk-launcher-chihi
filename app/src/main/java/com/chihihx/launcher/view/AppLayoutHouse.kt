package com.chihihx.launcher.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

class AppLayoutHouse @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppLayout(context, attrs) {

    private var startX = 0f
    private var startY = 0f
    private val threshold = 10 // 可以根据需要调整阈值

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                // 处理拖拽事件
                val endX = event.x
                val endY = event.y
                val distanceX = Math.abs(endX - startX)
                val distanceY = Math.abs(endY - startY)
                if (distanceX > threshold || distanceY > threshold) {
                    // 拖拽事件
                    // 这里可以添加拖拽事件的处理逻辑
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                val endX = event.x
                val endY = event.y
                val distanceX = Math.abs(endX - startX)
                val distanceY = Math.abs(endY - startY)
                if (distanceX <= threshold && distanceY <= threshold) {
                    // 点击事件
                    performClick()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

}



/*class AppLayoutHouse @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppLayout(context, attrs) {

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            performClick()
            return true
        }
        return super.onTouchEvent(event)
    }
}*/

