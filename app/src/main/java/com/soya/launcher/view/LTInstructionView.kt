package com.soya.launcher.view

import android.content.Context
import android.util.AttributeSet
import com.soya.launcher.R

class LTInstructionView : MyInstructionView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun getLayoutId(): Int {
        return R.layout.include_instructions_lt
    }
}
