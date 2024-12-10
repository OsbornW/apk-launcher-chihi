package com.soya.launcher.product

import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import com.soya.launcher.PACKAGE_NAME_FILE_MANAGER_713
import com.soya.launcher.R
import com.soya.launcher.bean.TypeItem
import com.soya.launcher.enums.Types
import com.soya.launcher.ext.convertGameJson
import com.soya.launcher.ext.isGame
import com.soya.launcher.ext.openApp
import com.soya.launcher.product.base.TVDeviceImpl

object Projector713_M_G_2X : Projector713_M_G() {

    override fun isShowDefaultVideoApp() = false

}