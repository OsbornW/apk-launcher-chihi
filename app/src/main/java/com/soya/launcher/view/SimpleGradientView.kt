package com.soya.launcher.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.soya.launcher.R

class SimpleGradientView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.include_instructions, this)
    }
}
