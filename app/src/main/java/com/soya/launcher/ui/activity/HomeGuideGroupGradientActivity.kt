package com.soya.launcher.ui.activity

import androidx.fragment.app.Fragment
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.replaceFragment
import com.soya.launcher.BaseWallpaperActivity
import com.soya.launcher.R
import com.soya.launcher.databinding.ActivityMainBinding
import com.soya.launcher.ui.fragment.ChooseGradientFragment.Companion.newInstance
import com.soya.launcher.ui.fragment.HomeGuideGroupGradientFragment

class HomeGuideGroupGradientActivity : BaseWallpaperActivity<ActivityMainBinding, BaseViewModel>() {

    override fun initView() {
        replaceFragment(HomeGuideGroupGradientFragment.newInstance(), R.id.main_browse_fragment)
    }


}
