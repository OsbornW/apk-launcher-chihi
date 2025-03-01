package com.chihihx.launcher.ui.activity

import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.replaceFragment
import com.chihihx.launcher.BaseWallpaperActivity
import com.chihihx.launcher.R
import com.chihihx.launcher.databinding.ActivityMainBinding
import com.chihihx.launcher.ui.fragment.WifiSettingsFragment

class WifiListActivity : BaseWallpaperActivity<ActivityMainBinding, BaseViewModel>() {

    override fun initView() {
        replaceFragment(WifiSettingsFragment.newInstance(), R.id.main_browse_fragment)
    }

}
