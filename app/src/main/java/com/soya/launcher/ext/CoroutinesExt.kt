package com.chihi.m98_701_android.ext

import kotlinx.coroutines.delay

suspend fun repeatWithDelay(times: Int, delayMillis: Long, action: suspend () -> Unit) {
    repeat(times) {
        action()
        delay(delayMillis)
    }
}