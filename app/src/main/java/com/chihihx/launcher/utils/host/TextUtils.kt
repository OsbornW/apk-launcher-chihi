package com.chihihx.launcher.utils.host

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 字符串常用函数
 *
 */
object TextUtils {
    /**
     * 判断字符串是否相等
     * @param s1 字符串1
     * @param s2 字符串2
     * @return true 是；false 不是
     */
    fun equals(s1: String?, s2: String?): Boolean {
        if (s1 === s2) {
            return true
        } else {
            val ss1 = (if ((null == s1)) "" else s1)
            val ss2 = (if ((null == s2)) "" else s2)
            return ss1 == ss2
        }
    }

    /**
     * 判断字符串是否相等，忽略大小写
     * @param s1 字符串1
     * @param s2 字符串2
     * @return true 是；false 不是
     */
    fun equalsIgnoreCase(s1: String?, s2: String?): Boolean {
        return if ((null == s1) && (null == s2)) {
            true
        } else if ((null != s1) && (null != s2)) {
            s1.equals(s2, ignoreCase = true)
        } else {
            false
        }
    }

    /**
     * 判断字符串s是否以字符串start开头
     * @param s 字符串1
     * @param start 字符串2
     * @return true 是；false 不是
     */
    fun startsWith(s: String?, start: String?): Boolean {
        return if ((null == s) && (null == start)) {
            true
        } else if ((null != s) && (null != start)) {
            s.startsWith(start)
        } else {
            false
        }
    }

    /**
     * 判断字符串是否为空
     * @param s 字符串
     * @return true 空；false 非空
     */
    fun empty(s: String?): Boolean {
        return ((null == s) || (s.length <= 0))
    }

    /**
     * 判断数组是否为空
     * @param a 数组
     * @return true 空；false 非空
     */
    fun empty(a: ByteArray?): Boolean {
        return ((null == a) || (a.size <= 0))
    }

    /**
     * 判断字符串是否为空
     * @param s 字符串
     * @return true 空；false 非空
     */
    fun <T> empty(s: Array<T>?): Boolean {
        return ((null == s) || (s.size <= 0))
    }

    /**
     * 获取当前的时间，字符串格式为"yyyyMMddHHmmss"
     * @return 字符串格式的时间
     */
    fun NOW(): String {
        try {
            val pattern = "yyyyMMddHHmmss"
            val formater = SimpleDateFormat(pattern, Locale.getDefault())
            return formater.format(Date())
        } catch (e: Exception) {
            return ""
        }
    }

    /**
     * 格式化指定时间，字符串格式为"yyyyMMddHHmmss"
     * @param time 指定时间，长整型
     * @return 字符串格式的时间
     */
    fun TSTR(time: Long): String {
        try {
            val pattern = "yyyyMMddHHmmss"
            val formater = SimpleDateFormat(pattern, Locale.getDefault())
            return formater.format(time)
        } catch (e: Exception) {
            return ""
        }
    }

    /**
     * 格式化指定时间
     * @param time 指定时间，长整型
     * @return 字符串格式的时间
     */
    fun TSTR(pattern: String?, time: Long): String {
        try {
            val formater = SimpleDateFormat(pattern, Locale.getDefault())
            return formater.format(time)
        } catch (e: Exception) {
            return ""
        }
    }

    /**
     * 判断字符串s是否包含字符串sub
     * @param s 字符串s
     * @param sub 字符串sub
     * @return true 包含；false 不包含
     */
    fun contains(s: String?, sub: String?): Boolean {
        return if ((null == s) && (null == sub)) {
            true
        } else if ((null != s) && (null != sub)) {
            s.contains(sub)
        } else {
            false
        }
    }

    /**
     * 比较两个字符串的大小，忽略为null的情况，按照空字符串处理
     * @param s1 其中的一个字符串
     * @param s2 另一个字符串
     * @return 如果s1小于s2返回负整数；相等返回0；s1大于s2返回正整数
     */
    fun compare(s1: String?, s2: String?): Int {
        var s1 = s1
        var s2 = s2
        s1 = if ((null == s1)) "" else s1
        s2 = if ((null == s2)) "" else s2
        return s1.compareTo(s2)
    }

    /**
     * 将字符串按照分隔符分隔成数组
     * @param s 字符串
     * @param separator 分隔符
     * @return 数组
     */
    fun toArray(s: String, separator: String): Array<String> {
        val l: MutableList<String> = ArrayList()

        if (s.isNotEmpty()) { // 假设 empty() 是检查字符串是否为空的自定义方法
            val array = s.split(separator.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            for (element in array) {
                if (!element.isEmpty()) { // 同上，假设 empty() 检查空字符串
                    l.add(element.trim { it <= ' ' })
                }
            }
        }

        return l.toTypedArray() // 直接使用 toTypedArray()
    }

    /**
     * 将字符串按照分隔符分隔成数组
     * @param s 字符串
     * @param separator 分隔符
     * @return 数组
     */
    fun split2List(s: String, separator: String): List<String> {
        val l: MutableList<String> = ArrayList()

        if (!empty(s)) {
            val array = s.split(separator.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            for (element in array) {
                if (!empty(element)) {
                    l.add(element.trim { it <= ' ' })
                }
            }
        }

        return l
    }

    /**
     * 对应字符串的trim方法
     * @param s 字符串
     * @return 执行trim后的返回字符串
     */
    fun trim(s: String): String {
        return (if ((!empty(s))) s.trim { it <= ' ' } else s)
    }

    /**
     * 将字符串转化为整型
     * @param s 字符串
     * @return 整型
     */
    fun parseInt(s: String): Int {
        var i = 0

        try {
            if (!empty(s)) {
                i = s.toInt()
            }
        } catch (e: Exception) {
            i = 0
        }

        return i
    }

    /**
     * 判断一个数组是是否包含指定元素
     * @param array 指定数据
     * @param v 指定元素
     * @return true 包含；false 不包含
     */
    fun <T> contains(array: Array<T>?, v: T?): Boolean {
        if ((null != array) && (array.size > 0)) {
            for (e in array) {
                if ((e === v) || ((null != v) && (null != e) && v == e)) {
                    return true
                }
            }
        }

        return false
    }

    /**
     * 将文本数组序列化为字符串
     * @param array 文本数组
     * @return 序列化后字符串
     */
    fun <T> toText(array: Array<T>?): String {
        if ((null != array) && (array.size > 0)) {
            var first = true
            val buffer = StringBuffer()

            for (e in array) {
                if (first) {
                    first = false
                } else {
                    buffer.append(";")
                }

                buffer.append(e)
            }

            return buffer.toString()
        } else {
            return ""
        }
    }

    /**
     * 将文本数组序列化为字符串
     * @param array 文本数组
     * @return 序列化后字符串
     */
    fun <T> toText(array: Set<T>?): String {
        if ((null != array) && (array.size > 0)) {
            var first = true
            val buffer = StringBuffer()

            for (e in array) {
                if (first) {
                    first = false
                } else {
                    buffer.append(";")
                }

                buffer.append(e)
            }

            return buffer.toString()
        } else {
            return ""
        }
    }

    /**
     * 对应StringBuilder的append方法，忽略空字符串
     * @param builder StringBuilder对象
     * @param value 字符串，为空则忽略
     * @return StringBuilder对象
     */
    fun append(builder: StringBuilder?, value: String?): StringBuilder? {
        if ((null != builder) && (!empty(value))) {
            builder.append(value)
        }

        return builder
    }

    /**
     * 对应字符串机型整理
     * @param s 字符串
     * @return 整理后的字符串
     */
    fun tidy(s: String): String {
        val buffer = StringBuffer()

        try {
            val a = s.toCharArray()

            for (i in a.indices) {
                if ((31 < a[i].code) && (a[i].code < 127)) {
                    buffer.append(a[i])
                }
            }
        } catch (t: Throwable) {
        }

        return buffer.toString()
    }
}

