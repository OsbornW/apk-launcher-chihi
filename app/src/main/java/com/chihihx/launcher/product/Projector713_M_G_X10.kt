package com.chihihx.launcher.product

import android.content.Context
import com.shudong.lib_base.currentActivity
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.startKtxActivity
import com.chihihx.launcher.ui.activity.GradientActivity

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

}