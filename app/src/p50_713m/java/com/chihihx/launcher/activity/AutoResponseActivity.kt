package com.chihihx.launcher.activity

import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.replaceFragment
import com.chihihx.launcher.BaseWallpaperActivity
import com.chihihx.launcher.R
import com.chihihx.launcher.databinding.ActivityAutoResponseBinding
import com.chihihx.launcher.fragment.AutoResponseFragment

class AutoResponseActivity: BaseWallpaperActivity<ActivityAutoResponseBinding, BaseViewModel>() {
    override fun initView() {
        replaceFragment(AutoResponseFragment.newInstance(),R.id.main)
    }
}