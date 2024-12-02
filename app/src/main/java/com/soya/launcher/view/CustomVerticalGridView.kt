package com.soya.launcher.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.leanback.widget.VerticalGridView
import androidx.recyclerview.widget.GridLayoutManager

class CustomVerticalGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VerticalGridView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthSpec)
        val heightMode = MeasureSpec.getMode(heightSpec)

        // 首先测量宽度
        super.onMeasure(widthSpec, heightSpec)

        // 如果是 wrap_content 模式，计算高度
        if (heightMode == MeasureSpec.AT_MOST) {
            val layoutManager = layoutManager as? GridLayoutManager
            val spanCount = layoutManager?.spanCount ?: 1

            // 获取第一个子项的高度来估算总高度
            val firstChild = getChildAt(0)
            val itemHeight = firstChild?.measuredHeight ?: 0

            if (itemHeight > 0) {
                val itemCount = adapter?.itemCount ?: 0
                val rows = Math.ceil(itemCount.toDouble() / spanCount).toInt()

                // 计算总高度（每列的高度 * 行数）
                val totalHeight = rows * itemHeight

                // 设置测量的高度
                val heightSpecWrap = MeasureSpec.makeMeasureSpec(totalHeight, MeasureSpec.AT_MOST)
                super.onMeasure(widthSpec, heightSpecWrap)
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_MOVE) {
            // 返回 true 以拦截触摸事件，禁止拖拽
            return true
        }
        return super.dispatchTouchEvent(event)
    }
}
