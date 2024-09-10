package com.soya.launcher.product

import com.soya.launcher.PACKAGE_NAME_SCREENZOOM_RK3326
import com.soya.launcher.ext.openApp
import com.soya.launcher.product.base.TVDeviceImpl

object ProjectorRK3326G : TVDeviceImpl{
    override fun openScreenZoom()  = PACKAGE_NAME_SCREENZOOM_RK3326.openApp()
}