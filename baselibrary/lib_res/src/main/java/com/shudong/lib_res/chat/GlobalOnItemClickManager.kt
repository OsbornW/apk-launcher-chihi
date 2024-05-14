package com.shudong.lib_res.chat

import android.content.Context
import android.view.KeyEvent
import android.widget.AdapterView
import android.widget.EditText
import com.shudong.lib_res.chat.SpanStringUtils.getEmotionContent

/**
 * Created by xiaokai on 2017/02/07.
 */
class GlobalOnItemClickManager {
    private var mEditText: EditText? = null
    fun attachToEditText(editText: EditText?) {
        mEditText = editText
    }

    fun getOnItemClickListener(emsPos: Int, context: Context?): AdapterView.OnItemClickListener {
        return AdapterView.OnItemClickListener { parent, view, position, id ->
            /* 判断是不是“删除”按钮
                     *      是   -> 删除上一个表情
                     *      不是 -> 加载相应的表情*/
            val emotionNum = position + emsPos * 20 //表情编号
            if (position != 20) {
                val index = mEditText!!.selectionStart //当前光标位置
                val emotionName = "[s:$emotionNum]"
                val currentContent = mEditText!!.text.toString()
                val sb = StringBuilder(currentContent)
                sb.insert(index, emotionName)
                mEditText!!.setText(getEmotionContent(context!!, mEditText!!, sb.toString()))
                mEditText!!.setSelection(index + emotionName.length)
            } else {
                mEditText!!.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
            }
        }
    }

    companion object {
        @JvmStatic
        var instance: GlobalOnItemClickManager? = null
            get() {
                if (field == null) {
                    field = GlobalOnItemClickManager()
                }
                return field
            }
            private set
    }
}