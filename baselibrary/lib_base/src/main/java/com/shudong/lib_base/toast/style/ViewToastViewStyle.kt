package com.thumbsupec.lib_base.toast.style

import android.content.Context
import android.view.Gravity
import android.view.View
import com.shudong.lib_base.toast.config.IToastStyle

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:   Toast View 包装样式实现
 * @Author:  zengyue
 * @Date:  2022/10/29
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.toast.style
 */
class ViewToastViewStyle(private val view: View, private val mStyle: IToastStyle<*>?) :
    IToastStyle<View?> {
    override fun createView(context: Context): View {
        return view
    }

    override fun getGravity(): Int {
        return mStyle?.gravity ?: Gravity.CENTER
    }

    override fun getXOffset(): Int {
        return mStyle?.xOffset ?: 0
    }

    override fun getYOffset(): Int {
        return mStyle?.yOffset ?: 0
    }

    override fun getHorizontalMargin(): Float {
        return mStyle?.horizontalMargin ?: 0f
    }

    override fun getVerticalMargin(): Float {
        return mStyle?.verticalMargin ?: 0f
    }
}