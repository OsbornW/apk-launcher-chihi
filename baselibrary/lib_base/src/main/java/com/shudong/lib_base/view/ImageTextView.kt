package com.shudong.lib_base.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.hjq.shape.view.ShapeTextView
import com.shudong.lib_base.ext.converDrawable

/**
 * Created by zhangjutao on 16/7/4.
 */
class ImageTextView : ShapeTextView {
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    )

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    )

    fun setImageInView(round: Int, res: Int) {
        val drawable: Drawable = res.converDrawable()
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.minimumHeight)
        when (round) {
            LEFT -> setCompoundDrawables(drawable, null, null, null)
            RIGHT -> setCompoundDrawables(null, null, drawable, null)
            TOP -> setCompoundDrawables(null, drawable, null, null)
            BOTTOM -> setCompoundDrawables(null, null, null, drawable)
        }
    }

    override fun onDraw(canvas: Canvas) {
        //获取左边的图片
        val drawableLeft = compoundDrawables[0]
        if (drawableLeft != null) {
            //取得字符串的宽度值
            val textWidth = paint.measureText(text.toString())
            //获取控件的内边距
            val drawablePadding = compoundDrawablePadding
            //返回图片呢的固有宽度,单位是DP
            val drawableWidth: Int = drawableLeft.intrinsicWidth
            val bodyWidth = textWidth + drawableWidth + drawablePadding
            canvas.translate((width - bodyWidth) / 2, 0f)
        }
        val drawableRight = compoundDrawables[2]
        if (drawableRight != null) {
            val textWidth = paint.measureText(text.toString())
            val drawablePadding = compoundDrawablePadding
            var drawableWidth = 0
            drawableWidth = drawableRight.intrinsicWidth
            val bodyWidth = textWidth + drawableWidth + drawablePadding
            setPadding(0, 0, (width - bodyWidth).toInt(), 0)
            canvas.translate((width - bodyWidth) / 2, 0f)
        }
        super.onDraw(canvas)
    }

    companion object {
        const val LEFT = 1
        const val RIGHT = 2
        const val TOP = 3
        const val BOTTOM = 4
    }
}