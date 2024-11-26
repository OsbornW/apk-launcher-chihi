package com.soya.launcher.view

import android.content.Context
import android.util.AttributeSet
import androidx.leanback.widget.VerticalGridView
import androidx.recyclerview.widget.GridLayoutManager

class CustomVerticalGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VerticalGridView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        if (adapter == null || adapter!!.itemCount == 0) {
            // 没有数据时设置高度为 0
            val zeroHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY)
            super.onMeasure(widthSpec, zeroHeightSpec)
        } else {
            // 如果是 wrap_content 模式
            if (MeasureSpec.getMode(heightSpec) == MeasureSpec.AT_MOST) {
                val gridLayoutManager = layoutManager as? GridLayoutManager
                val spanCount = gridLayoutManager?.spanCount ?: 1
                val itemHeight = calculateItemHeight() // 获取每个 item 的高度
                val itemCount = adapter?.itemCount ?: 0

                // 计算内容的高度：每列的 item 数量 * 每个 item 的高度
                val totalHeight = itemHeight * Math.ceil(itemCount.toDouble() / spanCount).toInt()

                // 根据计算出的内容高度来设置高度
                val heightSpecWrap = MeasureSpec.makeMeasureSpec(totalHeight, MeasureSpec.AT_MOST)
                super.onMeasure(widthSpec, heightSpecWrap)
            } else {
                // 其他情况下，直接使用传递的 heightSpec
                super.onMeasure(widthSpec, heightSpec)
            }
        }
    }

    // 获取每个 item 的高度，可以根据你的 item 的布局进行测量
    private fun calculateItemHeight(): Int {
        val firstItem = getChildAt(0) ?: return 0
        return firstItem.measuredHeight
    }
}

