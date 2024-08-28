package com.soya.launcher.product.base

import android.content.Context
import androidx.fragment.app.Fragment
import com.shudong.lib_base.ext.IS_MAIN_CANBACK
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.sendLiveEventDataDelay
import com.shudong.lib_base.ext.startKtxActivity
import com.shudong.lib_base.ext.yes
import com.soya.launcher.cache.AppCache
import com.soya.launcher.ext.openFileM
import com.soya.launcher.manager.PreferencesManager
import com.soya.launcher.ui.activity.LanguageActivity
import com.soya.launcher.ui.activity.SetDateActivity
import com.soya.launcher.ui.fragment.GuideLanguageFragment
import com.soya.launcher.ui.fragment.MainFragment
import com.soya.launcher.ui.fragment.WelcomeFragment

interface TVDeviceImpl : TVDevice {

    // 跳转到时间设置页面
    override fun openDateSetting(context: Context) = context.startKtxActivity<SetDateActivity>()

    // 跳转到语言设置页面
    override fun openLanguageSetting(context: Context) =
        context.startKtxActivity<LanguageActivity>()

    /*
    *
    * 默认是走引导页逻辑
    * */
    override fun switchFragment(): Fragment? = run {
        AppCache.isSkipGuid.no {
            sendLiveEventDataDelay(IS_MAIN_CANBACK, true, 1000)
            AppCache.isGuidChageLanguage.yes {
                GuideLanguageFragment.newInstance()
            }.otherwise {
                WelcomeFragment.newInstance()
            }

        }.otherwise {
            sendLiveEventDataDelay(IS_MAIN_CANBACK, false, 1000)
            MainFragment.newInstance()
        }
    }

    override fun openFileManager() {
        openFileM()
    }
}