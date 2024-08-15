package com.soya.launcher.ext

import androidx.fragment.app.FragmentActivity
import com.shudong.lib_base.ext.IS_MAIN_CANBACK
import com.shudong.lib_base.ext.sendLiveEventData
import com.shudong.lib_base.ext.sendLiveEventDataDelay
import com.soya.launcher.manager.PreferencesManager
import com.soya.launcher.ui.fragment.MainFragment
import com.soya.launcher.ui.fragment.WelcomeFragment

fun switchFragment() = run {

    sendLiveEventDataDelay(IS_MAIN_CANBACK, false,1000)
    MainFragment.newInstance()

}

fun FragmentActivity.jumpToAuth(){

}