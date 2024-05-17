package com.soya.launcher.ext

import com.shudong.lib_base.ext.IS_MAIN_CANBACK
import com.shudong.lib_base.ext.sendLiveEventData
import com.soya.launcher.manager.PreferencesManager
import com.soya.launcher.ui.fragment.MainFragment
import com.soya.launcher.ui.fragment.WelcomeFragment

fun switchFragment() = run{

         if (PreferencesManager.isGuide() == 0 ) {
             sendLiveEventData(IS_MAIN_CANBACK,true)
            WelcomeFragment.newInstance()
        } else {
             sendLiveEventData(IS_MAIN_CANBACK,false)
            MainFragment.newInstance()
        }


}