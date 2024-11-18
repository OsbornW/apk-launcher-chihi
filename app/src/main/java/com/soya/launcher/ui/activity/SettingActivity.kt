package com.soya.launcher.ui.activity

import android.content.res.Configuration
import androidx.fragment.app.Fragment
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.LANGUAGE_CHANGED
import com.shudong.lib_base.ext.UPDATE_WALLPAPER_EVENT
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.obseverLiveEvent
import com.shudong.lib_base.ext.replaceFragment
import com.shudong.lib_base.ext.sendLiveEventDataDelay
import com.soya.launcher.BaseWallpaperActivity
import com.soya.launcher.R
import com.soya.launcher.databinding.ActivityMainBinding
import com.soya.launcher.ui.fragment.SettingFragment

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
