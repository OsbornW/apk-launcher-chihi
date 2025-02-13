package com.soya.launcher.ui.activity

import com.shudong.lib_base.base.BaseVMActivity
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.replaceFragment
import com.soya.launcher.BaseVMMainActivity
import com.soya.launcher.BaseWallpaperActivity
import com.soya.launcher.R
import com.soya.launcher.databinding.ActivityDesktopSelectBinding
import com.soya.launcher.ui.fragment.DesktopSelectFragment
import com.soya.launcher.ui.fragment.InstallModeFragment

class DesktopSelectActivity : BaseVMMainActivity<ActivityDesktopSelectBinding,BaseViewModel>() {

    override fun initView() {
        replaceFragment(DesktopSelectFragment.newInstance(), R.id.fl_container)
    }

}