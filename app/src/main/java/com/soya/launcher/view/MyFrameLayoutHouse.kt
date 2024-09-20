package com.soya.launcher.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView

class MyFrameLayoutHouse @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MyFrameLayout(context, attrs) {
    private var startX: Float = 0f
    private var startY: Float = 0f
    private val CLICK_THRESHOLD = 10 // 设定一个点击阈值
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                // 记录初始位置
                startX = event.x
                startY = event.y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                // 可以在这里处理拖动事件
                return false

            }
            MotionEvent.ACTION_UP -> {
                // 计算移动距离
                val endX = event.x
                val endY = event.y
                val distanceX = endX - startX
                val distanceY = endY - startY
                val distance = Math.sqrt((distanceX * distanceX + distanceY * distanceY).toDouble())

                if (distance < CLICK_THRESHOLD) {
                    // 认为是点击事件
                    //onClick()
                    performClick()
                    return true
                } /*else {
                    // 认为是拖动事件
                    onDrag()
                }*/
                return false
            }
        }
        return false
    }
}
