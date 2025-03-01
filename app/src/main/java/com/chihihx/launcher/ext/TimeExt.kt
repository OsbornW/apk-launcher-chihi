package com.chihihx.launcher.ext

import com.shudong.lib_base.currentActivity
import com.chihihx.launcher.cache.AppCache
import com.chihihx.launcher.ui.activity.UpdateAppsActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

fun isShowUpdate(): Boolean {

    if(currentActivity!=null&& currentActivity is UpdateAppsActivity){
        return false
    }

    if (AppCache.lastTipTime == 0L) {
        // 如果 lastTipTime 等于 0，则直接返回 true
        return true
    }

    val now = System.currentTimeMillis()
    val lastTipTime = AppCache.lastTipTime
    val interval = AppCache.updateInteval

    return when (interval) {
        "day" -> {
            // 判断从上次提示到现在是否已经超过 24 小时
            TimeUnit.MILLISECONDS.toHours(now - lastTipTime) >= 24
        }
        "hour" -> {
            // 判断从上次提示到现在是否已经超过 1 小时
            TimeUnit.MILLISECONDS.toHours(now - lastTipTime) >= 1
        }
        "week" -> {
            // 判断从上次提示到现在是否已经超过一周
            TimeUnit.MILLISECONDS.toDays(now - lastTipTime) >= 7
        }
        else -> false
    }
}

fun String.formatTimeyyyyMMddHHmm(): String {
    val inputFormats = arrayOf(
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    )
    val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    return try {
        inputFormats.forEach { format ->
            try {
                val date = format.parse(this)
                return outputFormat.format(date)
            } catch (e: ParseException) {
                // 尝试下一个格式
            }
        }
        // 如果所有格式都解析失败，抛出自定义异常
        throw IllegalArgumentException("Invalid time format: $this")
    } catch (e: Exception) {
        // 日志记录
        // 返回默认值或抛出异常
        return outputFormat.format(Date()) // 或 throw MyCustomException()
    }
}

