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


const val ACTIVE_SUCCESS = "ACTIVE_SUCCESS"
const val IS_MAIN_CANBACK = "IS_MAIN_CANBACK"
