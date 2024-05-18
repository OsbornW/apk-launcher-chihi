package com.soya.launcher.ext

import com.shudong.lib_base.ext.IS_MAIN_CANBACK
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.sendLiveEventData
import com.shudong.lib_base.ext.yes
import com.shudong.lib_base.global.AppCacheBase
import com.soya.launcher.fragment.AuthFragment
import com.soya.launcher.manager.PreferencesManager
import com.soya.launcher.ui.fragment.MainFragment
import com.soya.launcher.ui.fragment.WelcomeFragment

fun switchFragment() = run {

    AppCacheBase.isActive.yes {

        sendLiveEventData(IS_MAIN_CANBACK, false)
        MainFragment.newInstance()

    }.otherwise {
        AuthFragment.newInstance()
    }

}