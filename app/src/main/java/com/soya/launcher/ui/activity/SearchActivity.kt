package com.soya.launcher.ui.activity

import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.replaceFragment
import com.soya.launcher.BaseWallpaperActivity
import com.soya.launcher.R
import com.soya.launcher.databinding.ActivityMainBinding
import com.soya.launcher.enums.Atts
import com.soya.launcher.ui.fragment.SearchFragment.Companion.newInstance

class SearchActivity : BaseWallpaperActivity<ActivityMainBinding, BaseViewModel>() {

    override fun initView() {
        replaceFragment(newInstance(intent.getStringExtra(Atts.WORD)), R.id.main_browse_fragment)
    }

}
