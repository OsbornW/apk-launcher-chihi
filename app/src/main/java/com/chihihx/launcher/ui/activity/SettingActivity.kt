package com.chihihx.launcher.ui.activity

import android.content.res.Configuration
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.LANGUAGE_CHANGED
import com.shudong.lib_base.ext.UPDATE_WALLPAPER_EVENT
import com.shudong.lib_base.ext.obseverLiveEvent
import com.shudong.lib_base.ext.replaceFragment
import com.shudong.lib_base.ext.sendLiveEventDataDelay
import com.chihihx.launcher.BaseWallpaperActivity
import com.chihihx.launcher.R
import com.chihihx.launcher.databinding.ActivityMainBinding
import com.chihihx.launcher.ui.fragment.SettingFragment

class SettingActivity : BaseWallpaperActivity<ActivityMainBinding,BaseViewModel>() {

    override fun initView() {
        replaceFragment(SettingFragment.newInstance(),R.id.main_browse_fragment)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // 检测语言变化
        if (newConfig.locale != null) {
            sendLiveEventDataDelay(LANGUAGE_CHANGED,true)
        }
    }

    override fun initObserver() {
        obseverLiveEvent<Boolean>(UPDATE_WALLPAPER_EVENT){
            updateWallPaper()
        }
    }
}
