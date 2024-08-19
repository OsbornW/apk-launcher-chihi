package com.soya.launcher.product

import androidx.fragment.app.Fragment
import com.shudong.lib_base.ext.IS_MAIN_CANBACK
import com.shudong.lib_base.ext.sendLiveEventDataDelay
import com.soya.launcher.product.base.TVDeviceImpl
import com.soya.launcher.ui.fragment.MainFragment

object PrjectorAiboyou: TVDeviceImpl{
    override fun switchFragment() = run {
        sendLiveEventDataDelay(IS_MAIN_CANBACK, false,1000)
        MainFragment.newInstance()
    }
}