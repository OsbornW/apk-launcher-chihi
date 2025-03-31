package com.shudong.lib_base.ext

import androidx.lifecycle.LifecycleOwner
import com.jeremyliao.liveeventbus.LiveEventBus

inline fun <reified T> sendLiveEventData(key: String, data: T) {
    LiveEventBus.get(key, T::class.java).post(data)
}

inline fun <reified T> sendLiveEventDataDelay(key: String, data: T, delayTime:Long = 0) {
    LiveEventBus.get(key, T::class.java).postDelay(data,delayTime)
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


const val ACTIVE_SUCCESS = "ACTIVE_SUCCESS"
const val IS_MAIN_CANBACK = "IS_MAIN_CANBACK"
const val REFRESH_HOME = "REFRESH_HOME"
const val UPDATE_HOME_LIST = "UPDATE_HOME_LIST"
const val HOME_EVENT = "HOME_EVENT"
const val UPDATE_WALLPAPER_EVENT = "UPDATE_WALLPAPER_EVENT"
const val LANGUAGE_CHANGED = "LANGUAGE_CHANGED"
const val RECREATE_MAIN = "RECREATE_MAIN"
const val ADD_APPSTORE = "ADD_APPSTORE"
const val LOAD_DEFULT_RESOURCE = "LOAD_DEFULT_RESOURCE"
const val REGET_HOMEDATA = "REGET_HOMEDATA"
const val SWITCH_HOME = "SWITCH_HOME"

