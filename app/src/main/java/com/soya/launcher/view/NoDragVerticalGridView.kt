package com.soya.launcher.view

import android.content.Context
import android.util.AttributeSet
import android.view.DragEvent
import android.view.MotionEvent
import androidx.leanback.widget.VerticalGridView
import com.shudong.lib_base.ext.e

class NoDragVerticalGridView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : VerticalGridView(context, attrs, defStyle) {

    private var startX: Float = 0f
    private var startY: Float = 0f
    private val CLICK_THRESHOLD = 10 // 设定一个点击阈值


    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
  /*      if (event?.action == MotionEvent.ACTION_MOVE) {
            // 返回 true 以拦截触摸事件，禁止拖拽
            return true
        }
        return super.dispatchTouchEvent(event)
    }*/



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
