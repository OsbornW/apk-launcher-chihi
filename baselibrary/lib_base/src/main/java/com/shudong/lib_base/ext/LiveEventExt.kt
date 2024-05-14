package com.shudong.lib_base.ext

import androidx.lifecycle.LifecycleOwner
import com.jeremyliao.liveeventbus.LiveEventBus

inline fun <reified T> sendLiveEventData(key: String, data: T) {
    LiveEventBus.get(key, T::class.java).post(data)
}


inline fun <reified T> LifecycleOwner.obseverLiveEvent(
    key: String,
    noinline listener: (data: T) -> Unit
) {
    LiveEventBus
        .get(key, T::class.java)
        .observe(this) {
            listener.invoke(it)
        }
}


const val SHOW_HOME_BIGIMG = "SHOW_HOME_BIGIMG"
const val SHOW_HOME_TAB = "SHOW_HOME_TAB"
const val REFRESH_ADAPTER = "REFRESH_ADAPTER"
const val UPDATE_HOME_RECOMMOND = "UPDATE_HOME_RECOMMOND"
const val REFRESH_FAVORITE_APPS = "REFRESH_FAVORITE_APPS"
const val GET_UNINSTALL_FOCUS = "GET_UNINSTALL_FOCUS"
const val APP_DELETE_INSTALL = "APP_DELETE_INSTALL"
const val DISABLE_UPDOWN = "DISABLE_UPDOWN"
const val Control_FOCUS = "Control_FOCUS"
const val HANDLE_MAIN_BACK = "HANDLE_MAIN_BACK"