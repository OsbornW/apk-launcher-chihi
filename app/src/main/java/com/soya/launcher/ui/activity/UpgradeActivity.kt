package com.soya.launcher.ui.activity

import androidx.fragment.app.Fragment
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.replaceFragment
import com.soya.launcher.BaseWallpaperActivity
import com.soya.launcher.R
import com.soya.launcher.databinding.ActivityMainBinding
import com.soya.launcher.ui.fragment.ScaleScreenFragment.Companion.newInstance
import com.soya.launcher.ui.fragment.UpgradeFragment

class UpgradeActivity : BaseWallpaperActivity<ActivityMainBinding, BaseViewModel>() {

    override fun initView() {
        replaceFragment(UpgradeFragment.newInstance(), R.id.main_browse_fragment)
    }

    override fun onBackPressed() {
        //super.onBackPressed();
    }
}
