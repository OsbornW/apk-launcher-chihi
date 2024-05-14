package com.shudong.lib_base.view.switchbutton

import android.R
import android.content.res.ColorStateList

/**
 * Generate thumb and background color state list use tintColor
 * Created by kyle on 15/11/4.
 */
object ColorUtils {
    private const val ENABLE_ATTR = R.attr.state_enabled
    private const val CHECKED_ATTR = R.attr.state_checked
    private const val PRESSED_ATTR = R.attr.state_pressed
    fun generateThumbColorWithTintColor(tintColor: Int): ColorStateList {
        val states = arrayOf(
            intArrayOf(-ENABLE_ATTR, CHECKED_ATTR),
            intArrayOf(-ENABLE_ATTR),
            intArrayOf(
                PRESSED_ATTR, -CHECKED_ATTR
            ),
            intArrayOf(PRESSED_ATTR, CHECKED_ATTR),
            intArrayOf(CHECKED_ATTR),
            intArrayOf(-CHECKED_ATTR)
        )
        val colors = intArrayOf(
            tintColor - -0x56000000,
            -0x454546,
            tintColor - -0x67000000,
            tintColor - -0x67000000,
            tintColor or -0x1000000,
            -0x111112
        )
        return ColorStateList(states, colors)
    }

    fun generateBackColorWithTintColor(tintColor: Int): ColorStateList {
        val states = arrayOf(
            intArrayOf(-ENABLE_ATTR, CHECKED_ATTR),
            intArrayOf(-ENABLE_ATTR),
            intArrayOf(
                CHECKED_ATTR, PRESSED_ATTR
            ),
            intArrayOf(-CHECKED_ATTR, PRESSED_ATTR),
            intArrayOf(CHECKED_ATTR),
            intArrayOf(-CHECKED_ATTR)
        )
        val colors = intArrayOf(
            tintColor - -0x1f000000,
            0x10000000,
            tintColor - -0x30000000,
            0x20000000,
            tintColor - -0x30000000,
            0x20000000
        )
        return ColorStateList(states, colors)
    }
}