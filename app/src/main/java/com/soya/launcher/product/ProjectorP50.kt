package com.soya.launcher.product

import android.content.Context
import com.soya.launcher.PACKAGE_NAME_FILE_MANAGER_713
import com.soya.launcher.ext.openApp
import com.soya.launcher.p50.autoFocus
import com.soya.launcher.product.base.TVDeviceImpl

object ProjectorP50 : TVDeviceImpl{
    override fun openFileManager()  = PACKAGE_NAME_FILE_MANAGER_713.openApp()
    override fun openKeystoneCorrection(context: Context) {
        autoFocus()
    }
}