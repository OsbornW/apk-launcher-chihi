package com.chihihx.launcher.product

import com.chihihx.launcher.R
import com.chihihx.launcher.bean.DateItem
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import com.chihihx.launcher.bean.TypeItem
import com.chihihx.launcher.ext.convertGameJson
import com.chihihx.launcher.ext.getFormattedTimeZone
import com.chihihx.launcher.ext.isGame
import com.shudong.lib_base.ext.appContext
import java.util.TimeZone

object ProjectorX50 : ProjectorP50() {
    override fun isShowDefaultVideoApp() = false

    override fun addGameItem(): MutableList<TypeItem>? = run {
        isGame().yes {
            convertGameJson()
        }.otherwise { null }
    }

    override fun isGameRes(): Int = run {
        isGame().yes {
            1
        }.otherwise { 0 }
    }

    override fun addTimeSetItem(
        isAutoTime: Boolean,
        date: String,
        time: String,
        is24: Boolean,
        isShowAllItem: Boolean
    ) =
        mutableListOf<DateItem>().apply {
            if(isShowAllItem){
                add(DateItem(
                    0,
                    appContext.getString(R.string.auto_time_title),
                    if (isAutoTime) appContext.getString(R.string.open) else appContext.getString(R.string.close),
                    isAutoTime,
                    true
                ))
                add(DateItem(1, appContext.getString(R.string.set_date_title), date, false, false))
                add(DateItem(2, appContext.getString(R.string.set_time_title), time, false, false))
            }
            add(
                DateItem(
                    3,
                    appContext.getString(R.string.time_display),
                    if (is24) appContext.getString(R.string.open) else appContext.getString(R.string.close),
                    is24,
                    true
                )
            )
            add(
                DateItem(
                    4,
                    appContext.getString(R.string.time_zone),
                    TimeZone.getTimeZone(TimeZone.getDefault().id).getFormattedTimeZone(),
                    false,
                    false
                )
            )
        }

    override fun isShowMemoryInfo() = true

}