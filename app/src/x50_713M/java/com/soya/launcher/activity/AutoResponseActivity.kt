package com.soya.launcher.activity

import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.replaceFragment
import com.soya.launcher.BaseWallpaperActivity
import com.soya.launcher.R
import com.soya.launcher.databinding.ActivityAutoResponseBinding
import com.soya.launcher.fragment.AutoResponseFragment

class AutoResponseActivity: BaseWallpaperActivity<ActivityAutoResponseBinding, BaseViewModel>() {
    override fun initView() {
        replaceFragment(AutoResponseFragment.newInstance(),R.id.main)
    }
}