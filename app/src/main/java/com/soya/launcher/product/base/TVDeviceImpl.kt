package com.soya.launcher.product.base

import android.content.Context
import com.shudong.lib_base.ext.startKtxActivity
import com.soya.launcher.ui.activity.LanguageActivity
import com.soya.launcher.ui.activity.SetDateActivity

interface TVDeviceImpl: TVDevice {

    // 跳转到时间设置页面
    override fun openDateSetting(context: Context)  = context.startKtxActivity<SetDateActivity>()

    // 跳转到语言设置页面
    override fun openLanguageSetting(context: Context)  = context.startKtxActivity<LanguageActivity>()

}