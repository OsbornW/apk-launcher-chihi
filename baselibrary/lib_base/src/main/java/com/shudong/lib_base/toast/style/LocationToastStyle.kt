package com.thumbsupec.lib_base.toast.style

import android.content.Context
import android.view.View
import com.shudong.lib_base.toast.config.IToastStyle

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:   Toast位置包装样式实现
 * @Author:  zengyue
 * @Date:  2022/10/29
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.toast.style
 */
class LocationToastStyle @JvmOverloads constructor(
    private val mStyle: IToastStyle<*>,
    private val mGravity: Int,
    private val mXOffset: Int = 0,
    private val mYOffset: Int = 0,
    private val mHorizontalMargin: Float = 0f,
    private val mVerticalMargin: Float = 0f
) : IToastStyle<View?> {
    override fun createView(context: Context): View {
        return mStyle.createView(context)
    }

    override fun getGravity(): Int {
        return mGravity
    }

    override fun getXOffset(): Int {
        return mXOffset
    }

    override fun getYOffset(): Int {
        return mYOffset
    }

    override fun getHorizontalMargin(): Float {
        return mHorizontalMargin
    }

    override fun getVerticalMargin(): Float {
        return mVerticalMargin
    }
}