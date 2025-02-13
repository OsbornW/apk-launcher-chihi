package com.soya.launcher.bean

import java.io.Serializable

class TypeItem(
    val name: String,
    var icon: Any,
    val id: Long,
    val type: Int,
    iconType: Int,
    layoutType: Int
) : Serializable {
    var iconType: Int = TYPE_ICON_IMAGE_RES
    var layoutType: Int = TYPE_LAYOUT_STYLE_UNKNOW
    var iconName: String? = null
    var data: List<PackageName>? = null
    var itemPosition: Int = 0
    var isAd = false
    var adId = ""
    var adUrl = ""

    init {
        this.iconType = iconType
        this.layoutType = layoutType
    }


    companion object {
        const val TYPE_ICON_IMAGE_RES: Int = 0
        const val TYPE_ICON_IMAGE_URL: Int = 1
        const val TYPE_ICON_ASSETS: Int = 2
        const val TYPE_LAYOUT_STYLE_UNKNOW: Int = -1
        const val TYPE_LAYOUT_STYLE_1: Int = 0
        const val TYPE_LAYOUT_STYLE_2: Int = 1
    }
}
