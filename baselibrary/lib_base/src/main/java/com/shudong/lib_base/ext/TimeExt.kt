package com.shudong.lib_base.ext

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

/**
 * 用在Chat界面
 * 将时间转换为字符串
 * NEED:
 * 今天
 * 早上10:41 （6 - 12） 21,600,000 - 43,200,000
 * 中午1:00 （12 - 2：30） 43,200,000 - 52,200,000
 * 下午3:00 （2：30-6）52,200,000 - 64,800,000
 * 傍晚7:00 （6-11）64,800,000 - 82,800,000
 * 深夜12:00 （11-12）82,800,000 - 86,400,000
 * 凌晨1:00 （12-6） 0 - 21,600,000
 *
 * 昨天
 * 昨天 早上10:41 （6 - 12）
 * 昨天 中午1:00 （12 - 2：30）
 * 昨天 下午3:00 （2：30-6）
 * 昨天 傍晚7:00 （6-11）
 * 昨天 深夜12:00 （11-12）
 * 昨天 凌晨1:00 （12-6）
 *
 * 前天
 * 周一 早上10:41 （6 - 12）
 * 周一 中午1:00 （12 - 2：30）
 * 周一 下午3:00 （2：30-6）
 * 周一 傍晚7:00 （6-11）
 * 周一 深夜12:00 （11-12）
 * 周一 凌晨1:00 （12-6）
 *
 * 在往后
 * 1月21号 早上10:41 （6 - 12）
 * 1月21号 中午1:00 （12 - 2：30）
 * 1月21号 下午3:00 （2：30-6）
 * 1月21号 傍晚7:00 （6-11）
 * 1月21号 深夜12:00 （11-12）
 * 1月21号 凌晨1:00 （12-6）
 *
 * 这里参考的对象是微信
 * Other：我这里比微信多做了一层判断，具体是周几上午下午这个，微信没有
 * 我加入了年的判断，如果是去年的就是 201x年x月x日 14:00
 * @author NEGIER
 * @date 2018/1/24
 */
fun Long.timeText(): String? {
    val format = SimpleDateFormat()
    // 获取今天零点的时间
    val calendar: Calendar = Calendar.getInstance()
    val year: Int = calendar.get(Calendar.YEAR)
    val month: Int = calendar.get(Calendar.MONTH) + 1
    val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
    var date: Date? = null
    var yearDate: Date? = null
    try {
        format.applyPattern("yyyy-MM-dd")
        date = format.parse("$year-$month-$day")
        format.applyPattern("yyyy")
        yearDate = format.parse(year.toString() + "")
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    if (yearDate!!.time > this) {
        format.applyPattern("yyyy年MM月dd日 HH:mm")
        return format.format(this)
    }

    // 两个时间相减
    val timeInterval: Long = this - date!!.time
    // 判断并返回值 《不准笑话我蹩脚的英文，不准，哼~》
    // 这里有一个Get："yyyy-MM-dd hh:mm:ss" hh写成HH就是24小时制，hh是12小时制
    if (timeInterval > 0) {
        // 凌晨 （00-06）
        val lincheng = 21600000
        // 早上 （06-12）
        val morning = 43200000
        // 中午 （12-14.30）
        val noon = 52200000
        // 下午 （14：30-18）
        val afternoon = 64800000
        // 傍晚 （18-23）
        val bangwan = 82800000
        // 深夜 (23 - 24)
        val shengye = 86400000
        if (timeInterval <= lincheng) {
            format.applyPattern("凌晨hh:mm")
        } else if (timeInterval <= morning) {
            format.applyPattern("早上hh:mm")
        } else if (timeInterval <= noon) {
            format.applyPattern("中午hh:mm")
        } else if (timeInterval <= afternoon) {
            format.applyPattern("下午hh:mm")
        } else if (timeInterval <= bangwan) {
            format.applyPattern("傍晚hh:mm")
        } else if (timeInterval <= shengye) {
            format.applyPattern("深夜hh:mm")
        }
    } else if (timeInterval > -86400000) {
        val absTimeInterval = Math.abs(timeInterval)
        // 这里顺序和上面相比倒过来了
        // 昨天 深夜 (23 - 24) 3,600,000
        val shengye = 3600000
        // 昨天 傍晚 （18-23）21,600,000
        val bangwan = 21600000
        // 昨天 下午 （14：30-18）34,200,000
        val afternoon = 34200000
        // 昨天 中午 （12-14.30）43,200,000
        val noon = 43200000
        // 昨天 早上 （06-12）64,800,000
        val morning = 64800000
        // 昨天 凌晨 （00-06）
        val lincheng = 86400000
        if (absTimeInterval <= shengye) {
            format.applyPattern("昨天 深夜hh:mm")
        } else if (absTimeInterval <= bangwan) {
            format.applyPattern("昨天 傍晚hh:mm")
        } else if (absTimeInterval <= afternoon) {
            format.applyPattern("昨天 下午hh:mm")
        } else if (absTimeInterval <= noon) {
            format.applyPattern("昨天 中午hh:mm")
        } else if (absTimeInterval <= morning) {
            format.applyPattern("昨天 早上hh:mm")
        } else if (absTimeInterval <= lincheng) {
            format.applyPattern("昨天 凌晨hh:mm")
        }
    } else if (timeInterval > -86400000 * 2) {
        val absTimeInterval = Math.abs(timeInterval)
        // 昨天 深夜 (23 - 24) 3,600,000
        val shengye = 3600000 * 2
        // 昨天 傍晚 （18-23）21,600,000
        val bangwan = 21600000 * 2
        // 昨天 下午 （14：30-18）34,200,000
        val afternoon = 34200000 * 2
        // 昨天 中午 （12-14.30）43,200,000
        val noon = 43200000 * 2
        // 昨天 早上 （06-12）64,800,000
        val morning = 64800000 * 2
        // 昨天 凌晨 （00-06）
        val lincheng = 86400000 * 2
        if (absTimeInterval <= shengye) {
            format.applyPattern("E 深夜hh:mm")
        } else if (absTimeInterval <= bangwan) {
            format.applyPattern("E 傍晚hh:mm")
        } else if (absTimeInterval <= afternoon) {
            format.applyPattern("E 下午hh:mm")
        } else if (absTimeInterval <= noon) {
            format.applyPattern("E 中午hh:mm")
        } else if (absTimeInterval <= morning) {
            format.applyPattern("E 早上hh:mm")
        } else if (absTimeInterval <= lincheng) {
            format.applyPattern("E 凌晨hh:mm")
        }
    } else {
        format.applyPattern("MM月dd日 HH:mm")
    }
    return format.format(this)
}