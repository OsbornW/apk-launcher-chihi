package com.soya.launcher.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil.setContentView
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.replaceFragment
import com.soya.launcher.BaseWallpaperActivity
import com.soya.launcher.R
import com.soya.launcher.databinding.ActivityMainBinding
import com.soya.launcher.ui.fragment.MoreFragment
import com.soya.launcher.ui.fragment.SettingFragment

class MoreActivity : BaseWallpaperActivity<ActivityMainBinding, BaseViewModel>() {

    override fun initView() {
        replaceFragment(MoreFragment.newInstance(),R.id.main_browse_fragment)
    }
}