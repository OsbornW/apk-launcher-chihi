package com.soya.launcher.enums

interface ServiceStatus {
    companion object {
        const val STATE_SUCCESS: Int = 0
        const val STATE_SERVICE_ERROR: Int = 1
        const val STATE_NET_WORK_ERROR: Int = 2
    }
}
