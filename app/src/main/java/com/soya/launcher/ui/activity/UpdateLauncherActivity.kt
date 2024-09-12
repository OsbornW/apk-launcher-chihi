package com.soya.launcher.ui.activity

import android.annotation.SuppressLint
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.replaceFragment
import com.soya.launcher.BaseVMMainActivity
import com.soya.launcher.R
import com.soya.launcher.databinding.ActivityUpdateLauncherBinding
import com.soya.launcher.ui.fragment.UpdateLauncherFragment

class UpdateLauncherActivity : BaseVMMainActivity<ActivityUpdateLauncherBinding, BaseViewModel>() {

    override fun initView() {
        replaceFragment(UpdateLauncherFragment.newInstance(), R.id.fl_container)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        //super.onBackPressed()
    }
}