package com.shudong.lib_base.ext

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.TextView


/**
 *
 * @ProjectName:  FairyWill
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/7/1 09:50
 * @PACKAGE_NAME:  com.garyliang.lib_base.util
 */

fun TextView.drawLeft(context: Context,  res: Int) {
    if(res==0){
        this.setCompoundDrawables(null, null, null, null)
        return
    }
    val drawable = context.resources.getDrawable(res,null)
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    this.setCompoundDrawables(drawable, null, null, null)
}

fun TextView.drawTop(context: Context,  res: Int) {
    if (res == 0) {
        this.setCompoundDrawables(null, null, null, null)
        return
    }
    val drawable = context.resources.getDrawable(res,null)
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    this.setCompoundDrawables(null, drawable, null, null)
}

fun TextView.drawRight(context: Context,  res: Int) {
    if (res == 0) {
        this.setCompoundDrawables(null, null, null, null)
        return
    }
    val drawable = context.resources.getDrawable(res,null)
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    this.setCompoundDrawables(null, null, drawable, null)
}

fun TextView.drawBottom(context: Context,  res: Int) {
    if (res == 0) {
        this.setCompoundDrawables(null, null, null, null)
        return
    }
    val drawable = context.resources.getDrawable(res,null)
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    this.setCompoundDrawables(null, null, null, drawable)
}


fun Int.converDrawable():Drawable= appContext.resources.getDrawable(this,null)

