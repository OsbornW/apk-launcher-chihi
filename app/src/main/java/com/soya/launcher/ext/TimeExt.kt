package com.soya.launcher.ext

import com.shudong.lib_base.currentActivity
import com.soya.launcher.cache.AppCache
import com.soya.launcher.ui.activity.UpdateAppsActivity
import java.util.concurrent.TimeUnit

fun isShowUpdate(): Boolean {
    if (AppCache.lastTipTime == 0L) {
        // 如果 lastTipTime 等于 0，则直接返回 true
        return true
    }

    if(currentActivity!=null&& currentActivity is UpdateAppsActivity){
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
        "week" -> {
            // 判断从上次提示到现在是否已经超过一周
            TimeUnit.MILLISECONDS.toDays(now - lastTipTime) >= 7
        }
        else -> false
    }
}
