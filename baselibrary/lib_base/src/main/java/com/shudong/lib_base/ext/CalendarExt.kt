package com.shudong.lib_base.ext

import java.util.Calendar

/**
 *
 * 根据出生年月日计算年龄
 */
fun MutableList<Int>.calculateAge(): Int {
    val birthDate: Calendar = Calendar.getInstance()
    birthDate.set(this[0], this[1], this[2])
    val currentDate: Calendar = Calendar.getInstance()
    var age: Int = currentDate.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)
    if (currentDate.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
        age--
    }
    return age
}