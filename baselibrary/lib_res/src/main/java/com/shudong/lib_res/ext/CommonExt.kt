package com.shudong.lib_res.ext

import android.content.Context
import com.shudong.lib_res.enum_.ChatType
import com.shudong.lib_res.view.otherwise
import com.shudong.lib_res.view.yes

fun ChatType.convertToNum() = run {
    (this == ChatType.SINGLE_CHAT).yes {
        0
    }.otherwise {
        1
    }

}

fun Int.convertToChatType() = run{
    (this==0).yes {
        ChatType.SINGLE_CHAT
    }.otherwise {
        ChatType.GROUP_CHAT
    }
}