package com.soya.launcher.product

import com.shudong.lib_base.ext.appContext
import com.soya.launcher.PACKAGE_NAME_FILE_MANAGER_713
import com.soya.launcher.R
import com.soya.launcher.bean.TypeItem
import com.soya.launcher.enums.Types
import com.soya.launcher.ext.openApp
import com.soya.launcher.product.base.TVDeviceImpl

object Projector713M : TVDeviceImpl{
    override fun openFileManager()  = PACKAGE_NAME_FILE_MANAGER_713.openApp()

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
            )
            )
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