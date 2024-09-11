package com.soya.launcher.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class MyTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    AppCompatTextView(context, attrs) {
    override fun setSelected(selected: Boolean) {
    }

    fun setSelected2(selected: Boolean) {
        super.setSelected(selected)
    }
}
