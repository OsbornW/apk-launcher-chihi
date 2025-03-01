package com.chihihx.launcher.ext

import android.widget.TextView

fun TextView.deleteLastChar() {
    val text = this.text.toString()
    if (text.isNotEmpty()) {
        // 删除最后一个字符
        val newText = text.substring(0, text.length - 1)
        this.text = newText
    }
}