package com.soya.launcher.ext

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.shudong.lib_base.ext.IS_MAIN_CANBACK
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.replaceFragment
import com.shudong.lib_base.ext.sendLiveEventData
import com.shudong.lib_base.ext.sendLiveEventDataDelay
import com.shudong.lib_base.ext.yes
import com.shudong.lib_base.global.AppCacheBase
import com.soya.launcher.BuildConfig
import com.soya.launcher.R
import com.soya.launcher.fragment.AuthFragment
import com.soya.launcher.manager.PreferencesManager
import com.soya.launcher.ui.fragment.MainFragment
import com.soya.launcher.ui.fragment.WelcomeFragment

fun switchFragment() = run {

    BuildConfig.DEBUG.yes {
        sendLiveEventDataDelay(IS_MAIN_CANBACK, false, 1000)
        MainFragment.newInstance()
    }.otherwise {
        AppCacheBase.isActive.yes {

            sendLiveEventDataDelay(IS_MAIN_CANBACK, false, 1000)
            MainFragment.newInstance()

        }.otherwise {
            AuthFragment.newInstance()
        }
    }


}

fun FragmentActivity.jumpToAuth() {
    replaceFragment(AuthFragment.newInstance(), R.id.main_browse_fragment)
}
