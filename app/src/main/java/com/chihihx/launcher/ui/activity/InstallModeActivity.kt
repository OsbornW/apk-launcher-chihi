package com.chihihx.launcher.ui.activity

import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.replaceFragment
import com.chihihx.launcher.BaseVMMainActivity
import com.chihihx.launcher.R
import com.chihihx.launcher.databinding.ActivitySettingBinding
import com.chihihx.launcher.ui.fragment.InstallModeFragment

class InstallModeActivity : BaseVMMainActivity<ActivitySettingBinding, BaseViewModel>() {

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