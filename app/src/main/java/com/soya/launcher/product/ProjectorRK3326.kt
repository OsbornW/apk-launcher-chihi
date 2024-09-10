package com.soya.launcher.product

import com.open.system.ASystemProperties
import com.shudong.lib_base.currentActivity
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.startKtxActivity
import com.shudong.lib_base.ext.yes
import com.soya.launcher.PACKAGE_NAME_AUTO_KEYSTONE_CORRECTION
import com.soya.launcher.PACKAGE_NAME_AUTO_KEYSTONE_CORRECTION_RK3326
import com.soya.launcher.PACKAGE_NAME_Manual_KEYSTONE_CORRECTION_RK3326
import com.soya.launcher.PACKAGE_NAME_SCREENZOOM_RK3326
import com.soya.launcher.ext.openApp
import com.soya.launcher.product.base.TVDeviceImpl
import com.soya.launcher.ui.activity.GradientActivity

object ProjectorRK3326 : TVDeviceImpl{
    override fun openScreenZoom()  = PACKAGE_NAME_SCREENZOOM_RK3326.openApp()

    override fun openManualAutoKeystoneCorrection() {
        val isEnalbe =
            ASystemProperties.getInt("persist.vendor.gsensor.enable", 0) == 1
        isEnalbe.yes {
            PACKAGE_NAME_AUTO_KEYSTONE_CORRECTION_RK3326.openApp()
        }.otherwise {
            PACKAGE_NAME_Manual_KEYSTONE_CORRECTION_RK3326.openApp()
        }
    }

}