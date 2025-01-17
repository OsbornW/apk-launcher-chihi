package com.soya.launcher.product

import android.content.Context
import com.shudong.lib_base.currentActivity
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.startKtxActivity
import com.shudong.lib_base.ext.yes
import com.soya.launcher.PACKAGE_NAME_FILE_MANAGER_713
import com.soya.launcher.R
import com.soya.launcher.bean.TypeItem
import com.soya.launcher.enums.Types
import com.soya.launcher.ext.convertGameJson
import com.soya.launcher.ext.isGame
import com.soya.launcher.ext.openApp
import com.soya.launcher.product.base.TVDeviceImpl
import com.soya.launcher.ui.activity.ChooseGradientActivity
import com.soya.launcher.ui.activity.GradientActivity
import com.soya.launcher.ui.activity.HomeGuideGroupGradientActivity

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