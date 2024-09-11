package com.soya.launcher.utils

import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.EditText

class AutoSeparateTextWatcher(
    /** EditText  */
    private val editText: EditText
) : TextWatcher {
    /** */
    private val mStringBuffer = StringBuffer()

    /** 分割符  */
    private var separator = ' '

    /** 分割符插入位置规则  */
    private var RULES = intArrayOf(3, 4, 4)

    /** 最大输入长度  */
    private var MAX_INPUT_LENGTH = 0

    /** 最大输入长度InputFilter  */
    private var mLengthFilter: LengthFilter? = null

    /**
     * @param editText 目标EditText
     */
    init {
        //更新输入最大长度
        setupMaxInputLength()
    }

    /**
     * 设置分割规则
     * @param RULES 分割规则数组
     * 例如：138 383 81438的分割数组是{3,3,5}
     */
    fun setRULES(RULES: IntArray) {
        this.RULES = RULES
        setupMaxInputLength()
        val originalText = removeSpecialSeparator(
            editText, this.separator
        )
        if (!TextUtils.isEmpty(originalText)) {
            editText.setText(originalText)
            editText.setSelection(editText.text.length)
        }
    }

    /**
     * 设置分割符
     * @param separator 分隔符，默认为空格
     */
    fun setSeparator(separator: Char) {
        val originalText = removeSpecialSeparator(
            editText, this.separator
        )
        this.separator = separator
        if (!TextUtils.isEmpty(originalText)) {
            editText.setText(originalText)
            editText.setSelection(editText.text.length)
        }
    }

    fun getSeparator(): Char {
        return separator
    }

    /** 更新最大输入长度  */
    private fun setupMaxInputLength() {
        MAX_INPUT_LENGTH = RULES.size - 1
        for (value in RULES) {
            MAX_INPUT_LENGTH += value
        }
        //更新LengthFilter
        val filters = editText.filters
        if (filters.size > 0 && mLengthFilter != null) {
            //判断editText的InputFilter中是否已经包含mLengthFilter
            for (i in filters.indices) {
                val filter = filters[i]
                if (mLengthFilter === filter) {
                    mLengthFilter = LengthFilter(MAX_INPUT_LENGTH)
                    filters[i] = mLengthFilter
                    return
                }
            }
        }
        addLengthFilter(filters)
    }

    /**
     * @param filters
     */
    private fun addLengthFilter(filters: Array<InputFilter?>) {
        var filters: Array<InputFilter?>? = filters
        if (filters == null) {
            filters = arrayOfNulls(0)
        }
        val newFilters = arrayOfNulls<InputFilter>(filters.size + 1)
        System.arraycopy(filters, 0, newFilters, 0, filters.size)
        mLengthFilter = LengthFilter(MAX_INPUT_LENGTH)
        newFilters[newFilters.size - 1] = mLengthFilter
        editText.filters = newFilters
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable) {
        if (!TextUtils.equals(s, mStringBuffer)) {
            //删除mStringBuffer中的文本
            mStringBuffer.delete(0, mStringBuffer.length)
            //添加分隔符
            mStringBuffer.append(handleText(s, RULES, separator))
            //删除多余字符
            if (mStringBuffer.length > MAX_INPUT_LENGTH) {
                mStringBuffer.delete(MAX_INPUT_LENGTH, mStringBuffer.length)
            }
            val currSelectStart = editText.selectionStart
            //计算分隔符导致的光标offset
            val separatorOffset = calculateSeparatorOffset(s, mStringBuffer, currSelectStart)
            editText.setText(mStringBuffer)
            //计算并设置当前的selectStart位置
            var selectStart = currSelectStart + separatorOffset
            if (selectStart < 0) {
                selectStart = 0
            } else if (selectStart > mStringBuffer.length) {
                selectStart = mStringBuffer.length
            }
            editText.setSelection(selectStart)
        }
    }

    /**
     * 计算符号的offset
     *
     * @param before
     * @param after
     * @param selectionStart
     *
     * @return
     */
    private fun calculateSeparatorOffset(
        before: CharSequence,
        after: CharSequence,
        selectionStart: Int
    ): Int {
        var offset = 0
        val beforeLength = before.length
        val afterLength = after.length
        val length = if (afterLength > beforeLength) beforeLength else afterLength
        for (i in 0 until length) {
            if (i >= selectionStart) {
                break
            }
            val bc = before[i]
            val ac = after[i]
            if (bc == separator && ac != separator) {
                offset--
            } else if (bc != separator && ac == separator) {
                offset++
            }
        }
        return offset
    }

    companion object {
        /**
         * @param s
         * @param rules
         * @param separator
         *
         * @return
         */
        fun handleText(s: Editable, rules: IntArray?, separator: Char): String {
            val stringBuffer = StringBuffer()
            var i = 0
            val length = s.length
            while (i < length) {
                val c = s[i]
                if (c != separator) {
                    stringBuffer.append(c)
                }
                if (length != stringBuffer.length && isSeparationPosition(
                        rules,
                        stringBuffer.length
                    )
                ) {
                    stringBuffer.append(separator)
                }
                i++
            }
            return stringBuffer.toString()
        }

        /**
         * @param RULES
         * @param length
         *
         * @return
         */
        private fun isSeparationPosition(RULES: IntArray?, length: Int): Boolean {
            if (RULES == null) {
                return false
            }
            var standardPos = 0
            var offset = 0
            for (pos in RULES) {
                standardPos += pos
                if (length == standardPos + offset++) {
                    return true
                }
            }
            return false
        }

        /**
         * @param editText
         * @param specialSeparator
         *
         * @return
         */
        fun removeSpecialSeparator(editText: EditText?, specialSeparator: Char): String? {
            if (editText == null) {
                return null
            }
            val text = editText.text
            return text?.toString()?.replace(specialSeparator.toString(), "")
        }
    }
}