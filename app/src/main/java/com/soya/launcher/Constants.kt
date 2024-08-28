package com.soya.launcher

import com.soya.launcher.cache.AppCache.WALLPAPERS
import com.soya.launcher.config.Config
import com.soya.launcher.manager.PreferencesManager
import com.soya.launcher.utils.GlideUtils

const val FLAVOUR_H6 = "flavour_h6"
const val PACKAGE_NAME_FILE_MANAGER_713 = "com.softwinner.TvdFileManager"
const val CLASS_NAME_AUTHFRAGMENT = "com.soya.launcher.fragment.AuthFragment"

fun defaultId() = if (Config.COMPANY == 0) R.drawable.wallpaper_22 else R.drawable.wallpaper_1
fun curWallpaper() = run {
    val wallpaperResId =
        WALLPAPERS.find { it.id == PreferencesManager.getWallpaper() }?.picture ?: defaultId()
    wallpaperResId
}

