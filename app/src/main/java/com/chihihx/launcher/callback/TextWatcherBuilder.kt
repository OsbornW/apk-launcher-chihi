package com.chihihx.launcher.callback

import android.text.Editable
import android.text.TextWatcher

class TextWatcherBuilder : TextWatcher {
    private var beforeTextChangedAction: ((CharSequence?, Int, Int, Int) -> Unit)? = null
    private var onTextChangedAction: ((CharSequence?, Int, Int, Int) -> Unit)? = null
    private var afterTextChangedAction: ((Editable?) -> Unit)? = null

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        beforeTextChangedAction?.invoke(s, start, count, after)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        onTextChangedAction?.invoke(s, start, before, count)
    }

    override fun afterTextChanged(s: Editable?) {
        afterTextChangedAction?.invoke(s)
    }

    fun beforeTextChanged(action: (CharSequence?, Int, Int, Int) -> Unit) {
        beforeTextChangedAction = action
    }

    fun onTextChanged(action: (CharSequence?, Int, Int, Int) -> Unit) {
        onTextChangedAction = action
    }

    fun afterTextChanged(action: (Editable?) -> Unit) {
        afterTextChangedAction = action
    }
}
