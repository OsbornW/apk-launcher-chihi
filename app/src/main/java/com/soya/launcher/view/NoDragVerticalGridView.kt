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



    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
              if (event?.action == MotionEvent.ACTION_MOVE) {
            // 返回 true 以拦截触摸事件，禁止拖拽
            return true
        }
        return super.dispatchTouchEvent(event)
    }

}
