package com.soya.launcher.ui.activity

import com.shudong.lib_base.base.BaseVMActivity
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.replaceFragment
import com.soya.launcher.BaseWallpaperActivity
import com.soya.launcher.R
import com.soya.launcher.databinding.ActivitySettingBinding
import com.soya.launcher.ui.fragment.InstallModeFragment

class InstallModeActivity : BaseVMActivity<ActivitySettingBinding, BaseViewModel>() {

    override fun initView() {
        replaceFragment(InstallModeFragment.newInstance(), R.id.fl_container)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        const val TYPE = "type"
        const val SELECT_APP_FRAGMENT = "select_app_fragment"

    }
}