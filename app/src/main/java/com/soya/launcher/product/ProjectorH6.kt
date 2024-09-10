package com.soya.launcher.product

import android.content.Context
import com.shudong.lib_base.currentActivity
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.startKtxActivity
import com.soya.launcher.R
import com.soya.launcher.SETTING_ABOUT
import com.soya.launcher.SETTING_BLUETOOTH
import com.soya.launcher.SETTING_DATE
import com.soya.launcher.SETTING_LAUNGUAGE
import com.soya.launcher.SETTING_MORE
import com.soya.launcher.SETTING_NETWORK
import com.soya.launcher.SETTING_PROJECTOR
import com.soya.launcher.SETTING_WALLPAPER
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.ext.openBluetoothSettings
import com.soya.launcher.product.base.TVDeviceImpl
import com.soya.launcher.ui.activity.GradientActivity
import com.soya.launcher.ui.activity.InstallModeActivity
import com.soya.launcher.utils.AndroidSystem

object ProjectorH6: TVDeviceImpl {
    override fun openHomeTopKeystoneCorrection(context: Context) {
        context.startKtxActivity<GradientActivity>()
    }

    override fun openProjectorMode(callback: (isSuccess: Boolean) -> Unit) {
        currentActivity?.let {
            it.startKtxActivity<InstallModeActivity>()
        }
    }

    override fun openKeystoneCorrectionOptions() {
        currentActivity?.let {
            it.startKtxActivity<GradientActivity>()
        }
    }


    override fun openBluetooth() {
        currentActivity?.openBluetoothSettings()
    }

    override fun openMore() {
        currentActivity?.let { AndroidSystem.openSystemSetting2(it) }
    }

}