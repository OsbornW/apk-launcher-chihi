package com.soya.launcher.ext

import android.widget.EditText

// 光标向左移动
fun EditText.moveCursorLeft() {
    val currentPosition = this.selectionStart
    if (currentPosition > 0) {
        // 设置光标位置为当前位置的前一个字符
        this.setSelection(currentPosition - 1)
    }
}

// 光标向右移动
fun EditText.moveCursorRight() {
    val currentPosition = this.selectionStart
    if (currentPosition < this.text.length) {
        // 设置光标位置为当前位置的后一个字符
        this.setSelection(currentPosition + 1)
    }
}


// 删除光标前的字符
fun EditText.deletePreviousChar() {
    val currentPosition = this.selectionStart
    if (currentPosition > 0) {
        val editableText = this.text
        editableText.delete(currentPosition - 1, currentPosition)
        this.setSelection(currentPosition - 1)
    }
}