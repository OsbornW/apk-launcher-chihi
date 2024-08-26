package com.soya.launcher.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.replaceFragment
import com.soya.launcher.BaseWallpaperActivity
import com.soya.launcher.R
import com.soya.launcher.databinding.ActivityMainBinding
import com.soya.launcher.ui.fragment.AppsFragment

class AppsActivity : BaseWallpaperActivity<ActivityMainBinding,BaseViewModel>() {

    override fun initView() {
        replaceFragment(AppsFragment.newInstance(),R.id.main_browse_fragment)
    }

}
