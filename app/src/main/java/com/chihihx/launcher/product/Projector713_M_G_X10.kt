package com.chihihx.launcher.product

import android.content.Context
import com.chihihx.launcher.R
import com.chihihx.launcher.bean.DateItem
import com.shudong.lib_base.currentActivity
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.startKtxActivity
import com.chihihx.launcher.ui.activity.GradientActivity
import com.shudong.lib_base.ext.appContext
import java.util.TimeZone

object Projector713_M_G_X10 : Projector713_M_G() {

    override fun isShowDefaultVideoApp() = false

    override fun openHomeTopKeystoneCorrection(context: Context) {
        context.startKtxActivity<GradientActivity>()
    }

    override fun openKeystoneCorrectionOptions() {
        currentActivity?.let {
            "我要跳转到梯形校正了哦".e("chihi_error")
            it.startKtxActivity<GradientActivity>()
        }
    }

    override fun addTimeSetItem(isAutoTime: Boolean, date: String, time: String, is24: Boolean) =
        mutableListOf<DateItem>().apply {
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
                    TimeZone.getDefault().id,
                    false,
                    false
                )
            )
        }

    override fun isShowMemoryInfo() = true

}