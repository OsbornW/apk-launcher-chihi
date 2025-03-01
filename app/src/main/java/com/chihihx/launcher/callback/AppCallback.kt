package com.chihihx.launcher.callback

import com.chihihx.launcher.bean.Data
import com.chihihx.launcher.bean.TypeItem

/**
 *
 * 首页内容区域点击事件回调
 */
data class ContentCallback(
    val onClick: (Data) -> Unit,
    val onFocus: (Boolean, Data) -> Unit
)

/**
 *
 * 首页头部区域点击事件回调
 */
data class HeaderCallback(
    val onClick: (TypeItem) -> Unit,
    val onSelect: (Boolean, TypeItem) -> Unit
)