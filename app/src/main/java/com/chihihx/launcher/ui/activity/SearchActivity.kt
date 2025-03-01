package com.chihihx.launcher.ui.activity

import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.replaceFragment
import com.chihihx.launcher.BaseWallpaperActivity
import com.chihihx.launcher.R
import com.chihihx.launcher.databinding.ActivityMainBinding
import com.chihihx.launcher.enums.Atts
import com.chihihx.launcher.ui.fragment.SearchFragment

class SearchActivity : BaseWallpaperActivity<ActivityMainBinding, BaseViewModel>() {

    override fun initView() {
        replaceFragment(SearchFragment.newInstance(intent.getStringExtra(Atts.WORD)), R.id.main_browse_fragment)
    }

}
