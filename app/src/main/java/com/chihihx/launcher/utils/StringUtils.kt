package com.chihihx.launcher.utils

import android.content.Context
import android.text.TextUtils
import com.chihihx.launcher.R
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

object StringUtils {
    fun firstCharToUpperCase(text: String): String {
        if (TextUtils.isEmpty(text)) return text
        val array = toArrays(text)
        array[0] = array[0]!!.uppercase(Locale.getDefault())
        val sb = StringBuilder(array.size)
        for (item in array) {
            sb.append(item)
        }
        return sb.toString()
    }

    fun isEmpty(vararg strings: String?): Boolean {
        for (string in strings) {
            if (TextUtils.isEmpty(string)) {
                return true
            }
        }

        return false
    }

    fun toArrays(text: CharSequence): Array<String?> {
        val codePoints = text.codePoints().toArray()
        val words = arrayOfNulls<String>(codePoints.size)
        for (i in codePoints.indices) {
            val code = codePoints[i]
            words[i] = String(Character.toChars(code))
        }
        return words
    }

    fun numberToText(number: Float): String {
        if (number < 1000) {
            return number.toString()
        } else {
            val value = number / 10000f
            return if (value < 1) {
                round((value * 10f).toDouble(), 2).toString() + "千"
            } else {
                round(value.toDouble(), 2).toString() + "万"
            }
        }
    }

    fun round(value: Double, places: Int): Double {
        require(places >= 0)
        var bd = BigDecimal(value)
        bd = bd.setScale(places, RoundingMode.HALF_UP)
        return bd.toDouble()
    }

    fun dayToWeek(context: Context, type: Int): String {
        var name = context.getString(R.string.sunday)
        when (type) {
            1 -> name = context.getString(R.string.monday)
            2 -> name = context.getString(R.string.tuesday)
            3 -> name = context.getString(R.string.wednesday)
            4 -> name = context.getString(R.string.thursday)
            5 -> name = context.getString(R.string.friday)
            6 -> name = context.getString(R.string.saturday)
            7 -> name = context.getString(R.string.sunday)
        }
        return name
    }
}
