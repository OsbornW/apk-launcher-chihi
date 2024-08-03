package com.soya.launcher.ext

import com.shudong.lib_base.currentActivity
import com.soya.launcher.cache.AppCache
import com.soya.launcher.ui.activity.UpdateAppsActivity
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

fun isShowUpdate(): Boolean {
    if (AppCache.lastTipTime == 0L) {
        // 如果 lastTipTime 等于 0，则直接返回 true
        return true
    }

    if(currentActivity!=null&& currentActivity is UpdateAppsActivity){
        return false
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

// 扩展函数定义
fun String.formatTimeyyyyMMddHHmm(): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    return try {
        // 解析原始字符串为日期对象
        val date = inputFormat.parse(this)
        // 将日期对象格式化为新的字符串
        outputFormat.format(date)
    } catch (e: Exception) {
        // 如果解析失败，返回原始字符串或空字符串
        ""
    }
}
