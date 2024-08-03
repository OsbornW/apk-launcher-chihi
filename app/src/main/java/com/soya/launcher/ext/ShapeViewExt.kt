package com.soya.launcher.ext

import com.hjq.shape.layout.ShapeRelativeLayout
import com.hjq.shape.view.ShapeTextView
import com.shudong.lib_base.ext.colorValue

fun ShapeTextView.setShapeTextViewStrokeColor(resColor:Int){
    this.shapeDrawableBuilder
        .setStrokeColor(resColor.colorValue())
        .intoBackground()
}