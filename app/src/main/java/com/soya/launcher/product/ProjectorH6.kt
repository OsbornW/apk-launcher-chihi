package com.soya.launcher.product

import android.content.Context
import com.shudong.lib_base.ext.startKtxActivity
import com.soya.launcher.product.base.TVDeviceImpl
import com.soya.launcher.ui.activity.GradientActivity

object ProjectorH6: TVDeviceImpl {
    override fun openKeystoneCorrection(context: Context) {
        context.startKtxActivity<GradientActivity>()
    }
}