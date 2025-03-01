package com.chihihx.launcher.view

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import com.chihihx.launcher.callback.SelectedCallback

open class MyCardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    CardView(context, attrs) {
    private var callback: SelectedCallback? = null

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        if (callback != null) callback!!.onSelect(selected)
    }

    fun setCallback(callback: SelectedCallback?) {
        this.callback = callback
    }
}
