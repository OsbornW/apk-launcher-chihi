package com.soya.launcher.ext

import android.util.Log
import com.shudong.lib_base.ext.IS_MAIN_CANBACK
import com.shudong.lib_base.ext.sendLiveEventData
import com.shudong.lib_base.ext.sendLiveEventDataDelay
import com.soya.launcher.manager.PreferencesManager
import com.soya.launcher.ui.fragment.MainFragment
import com.soya.launcher.ui.fragment.WelcomeFragment

fun switchFragment() = run{

    if (PreferencesManager.isGuide() == 0 ) {
        sendLiveEventDataDelay(IS_MAIN_CANBACK,true,1000)
        WelcomeFragment.newInstance()
    } else {
        Log.d("zy1996", "switchFragment: 发送false了====")
        sendLiveEventDataDelay(IS_MAIN_CANBACK,false,1000)
        MainFragment.newInstance()
    }


}