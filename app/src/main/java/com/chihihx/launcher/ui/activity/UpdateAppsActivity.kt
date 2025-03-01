package com.chihihx.launcher.ui.activity

import android.annotation.SuppressLint
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.replaceFragment
import com.chihihx.launcher.BaseVMMainActivity
import com.chihihx.launcher.R
import com.chihihx.launcher.databinding.ActivityUpdateAppsBinding
import com.chihihx.launcher.ui.fragment.UpdateAppsFragment


class UpdateAppsActivity : BaseVMMainActivity<ActivityUpdateAppsBinding, BaseViewModel>() {

    override fun initView() {
        replaceFragment(UpdateAppsFragment.newInstance(), R.id.fl_container)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        //super.onBackPressed()
    }

}