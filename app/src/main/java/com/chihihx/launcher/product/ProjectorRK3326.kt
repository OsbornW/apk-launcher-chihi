package com.chihihx.launcher.product

import com.open.system.ASystemProperties
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import com.chihihx.launcher.PACKAGE_NAME_AUTO_KEYSTONE_CORRECTION_RK3326
import com.chihihx.launcher.PACKAGE_NAME_Manual_KEYSTONE_CORRECTION_RK3326
import com.chihihx.launcher.PACKAGE_NAME_SCREENZOOM_RK3326
import com.chihihx.launcher.R
import com.chihihx.launcher.bean.TypeItem
import com.chihihx.launcher.enums.Types
import com.chihihx.launcher.ext.openApp
import com.chihihx.launcher.product.base.TVDeviceImpl

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