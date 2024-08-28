package com.soya.launcher.product

import com.soya.launcher.PACKAGE_NAME_FILE_MANAGER_713
import com.soya.launcher.ext.openApp
import com.soya.launcher.product.base.TVDeviceImpl

object Projector713G: TVDeviceImpl {
    override fun openFileManager()  = PACKAGE_NAME_FILE_MANAGER_713.openApp()

}