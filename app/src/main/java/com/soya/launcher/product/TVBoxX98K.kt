package com.soya.launcher.product

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.soya.launcher.product.base.TVDeviceImpl

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
}