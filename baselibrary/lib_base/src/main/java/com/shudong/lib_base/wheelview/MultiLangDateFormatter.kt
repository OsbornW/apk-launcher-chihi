package com.shudong.lib_base.wheelview

import com.github.gzuliyujiang.wheelpicker.contract.DateFormatter
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import com.thumbsupec.lib_base.ext.language.CHINESE
import com.thumbsupec.lib_base.ext.language.getCurrentLanguage

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/12/9 16:17
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.common
 */
class MultiLangDateFormatter:DateFormatter {
    override fun formatYear(year: Int) = "${year}${(getCurrentLanguage()== CHINESE).yes { "年" }.otherwise { "" }}"

    override fun formatMonth(month: Int) = "${month}${(getCurrentLanguage()== CHINESE).yes { "月" }.otherwise { "" }}"

    override fun formatDay(day: Int) = "${day}${(getCurrentLanguage()== CHINESE).yes { "日" }.otherwise { "" }}"
}