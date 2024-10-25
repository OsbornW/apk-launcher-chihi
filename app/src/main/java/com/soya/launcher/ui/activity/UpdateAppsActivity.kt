package com.soya.launcher.ui.activity

import android.annotation.SuppressLint
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.replaceFragment
import com.soya.launcher.BaseVMMainActivity
import com.soya.launcher.BaseWallpaperActivity
import com.soya.launcher.R
import com.soya.launcher.databinding.ActivitySettingBinding
import com.soya.launcher.databinding.ActivityUpdateAppsBinding
import com.soya.launcher.ui.fragment.InstallModeFragment
import com.soya.launcher.ui.fragment.UpdateAppsFragment


class UpdateAppsActivity : BaseVMMainActivity<ActivityUpdateAppsBinding, BaseViewModel>() {

    override fun initView() {
        replaceFragment(UpdateAppsFragment.newInstance(), R.id.fl_container)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        //super.onBackPressed()
    }

}