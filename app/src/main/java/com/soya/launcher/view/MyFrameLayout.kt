package com.soya.launcher.view

import android.content.Context
import android.util.AttributeSet
import com.hjq.shape.layout.ShapeFrameLayout
import com.soya.launcher.callback.SelectedCallback

open class MyFrameLayout : ShapeFrameLayout {
    private var callback: SelectedCallback? = null

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs)

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        if (callback != null) callback!!.onSelect(selected)
    }

    fun setCallback(callback: SelectedCallback?) {
        this.callback = callback
    }
}