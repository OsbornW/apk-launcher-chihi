package com.soya.launcher.product

import com.shudong.lib_base.ext.appContext
import com.soya.launcher.PACKAGE_NAME_SCREENZOOM_RK3326
import com.soya.launcher.R
import com.soya.launcher.bean.TypeItem
import com.soya.launcher.enums.Types
import com.soya.launcher.ext.convertGameJson
import com.soya.launcher.ext.openApp
import com.soya.launcher.product.base.TVDeviceImpl

object ProjectorRK3326G : TVDeviceImpl{
    override fun openScreenZoom()  = PACKAGE_NAME_SCREENZOOM_RK3326.openApp()

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

    override fun addGameItem(): MutableList<TypeItem> = convertGameJson()

}