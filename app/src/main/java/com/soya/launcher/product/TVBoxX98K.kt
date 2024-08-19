package com.soya.launcher.product

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.shudong.lib_base.ext.IS_MAIN_CANBACK
import com.shudong.lib_base.ext.sendLiveEventDataDelay
import com.soya.launcher.product.base.TVDeviceImpl
import com.soya.launcher.ui.fragment.MainFragment

data object TVBoxX98K : TVDeviceImpl {
    /**
     *
     * 跳转系统时间设置页面
     */
    override fun openDateSetting(context: Context) =
        context.startActivity(Intent(Settings.ACTION_DATE_SETTINGS))

    /**
     *
     * 跳转语言设置页面
     */
    override fun openLanguageSetting(context: Context) =
        context.startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))


    override fun switchFragment() = run {
        sendLiveEventDataDelay(IS_MAIN_CANBACK, false,1000)
        MainFragment.newInstance()
    }

}