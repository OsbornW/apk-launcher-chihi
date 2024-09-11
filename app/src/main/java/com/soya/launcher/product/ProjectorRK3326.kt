package com.soya.launcher.product

import com.open.system.ASystemProperties
import com.shudong.lib_base.currentActivity
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.startKtxActivity
import com.shudong.lib_base.ext.yes
import com.soya.launcher.PACKAGE_NAME_AUTO_KEYSTONE_CORRECTION
import com.soya.launcher.PACKAGE_NAME_AUTO_KEYSTONE_CORRECTION_RK3326
import com.soya.launcher.PACKAGE_NAME_Manual_KEYSTONE_CORRECTION_RK3326
import com.soya.launcher.PACKAGE_NAME_SCREENZOOM_RK3326
import com.soya.launcher.R
import com.soya.launcher.bean.TypeItem
import com.soya.launcher.enums.Types
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

    override fun addHeaderItem(): MutableList<TypeItem> {
        return mutableListOf<TypeItem>().apply {

            // APP Store
            add(TypeItem(
                appContext.getString(R.string.app_store),
                R.drawable.store,
                0,
                Types.TYPE_APP_STORE,
                TypeItem.TYPE_ICON_IMAGE_RES,
                TypeItem.TYPE_LAYOUT_STYLE_UNKNOW
            ))

            // Local APP
            add(TypeItem(
                appContext.getString(R.string.apps),
                R.drawable.app_list,
                0,
                Types.TYPE_MY_APPS,
                TypeItem.TYPE_ICON_IMAGE_RES,
                TypeItem.TYPE_LAYOUT_STYLE_UNKNOW
            ))

            // 投影仪
            add(
                TypeItem(
                appContext.getString(R.string.pojector),
                R.drawable.projector,
                0,
                Types.TYPE_PROJECTOR,
                TypeItem.TYPE_ICON_IMAGE_RES,
                TypeItem.TYPE_LAYOUT_STYLE_UNKNOW
            )
            )
        }
    }

}