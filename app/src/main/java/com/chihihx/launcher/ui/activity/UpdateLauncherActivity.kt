package com.chihihx.launcher.ui.activity

import android.annotation.SuppressLint
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.replaceFragment
import com.chihihx.launcher.BaseVMMainActivity
import com.chihihx.launcher.R
import com.chihihx.launcher.databinding.ActivityUpdateLauncherBinding
import com.chihihx.launcher.ui.fragment.UpdateLauncherFragment

class UpdateLauncherActivity : BaseVMMainActivity<ActivityUpdateLauncherBinding, BaseViewModel>() {

    override fun initView() {
        replaceFragment(UpdateLauncherFragment.newInstance(), R.id.fl_container)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        //super.onBackPressed()
    }
}