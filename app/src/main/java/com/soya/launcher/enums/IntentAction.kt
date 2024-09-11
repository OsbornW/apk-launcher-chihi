package com.soya.launcher.enums

interface IntentAction {
    companion object {
        const val ACTION_UPDATE_WALLPAPER: String = "action_update_wallpaper"
        const val ACTION_DOWNLOAD_APK: String = "android.intent.action.store.download.apk"
        const val ACTION_DELETE_PACKAGE: String = "action.delete.package"
        const val ACTION_RESET_SELECT_HOME: String = "action_reset_select_home"
    }
}
