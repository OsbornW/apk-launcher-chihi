package com.chihihx.launcher.enums

interface IntentAction {
    companion object {
        const val ACTION_DOWNLOAD_APK: String = "android.intent.action.store.download.apk"
        const val ACTION_DELETE_PACKAGE: String = "action.delete.package"
    }
}
