package com.chihihx.launcher.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class NoFocuRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    RecyclerView(context, attrs) {
    init {
        isEnabled = false
    }
}
