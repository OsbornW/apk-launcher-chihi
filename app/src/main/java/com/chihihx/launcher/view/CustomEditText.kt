package com.chihihx.launcher.view

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent

class CustomEditText(context: Context, attrs: AttributeSet) : androidx.appcompat.widget.AppCompatEditText(context, attrs) {

    // 定义一个高阶函数用于处理确认点击的逻辑回调
    var onConfirmClick: (() -> Unit)? = null

    init {
        // 防止软键盘弹出
        showSoftInputOnFocus = false
    }

    // 拦截鼠标点击事件
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            // 如果用户定义了点击确认回调，则调用
            onConfirmClick?.invoke()
            return true  // 拦截点击，防止软键盘弹出
        }
        return super.onTouchEvent(event)
    }

    // 拦截遥控器的按键事件
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_CENTER,  // 遥控器确认键
                KeyEvent.KEYCODE_ENTER -> {    // 回车键
                    // 调用用户定义的点击确认回调
                    onConfirmClick?.invoke()
                    return true
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    // 处理点击事件，必须确保重写了 performClick
    override fun performClick(): Boolean {
        // 执行确认点击回调
        onConfirmClick?.invoke()
        return super.performClick()
    }
}
