package com.shudong.lib_res.chat

fun ChatInputView.initChatInputView(setup: (ChatInputView.() -> Unit)? = null){
    setup?.invoke(this)
}