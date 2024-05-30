package com.soya.launcher.ui.activity

import androidx.fragment.app.Fragment
import com.soya.launcher.R
import com.soya.launcher.ui.fragment.HomeGuideGroupGradientFragment

class HomeGuideGroupGradientActivity : AbsActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun getContainerId(): Int {
        return R.id.main_browse_fragment
    }

    override fun getFragment(): Fragment {
        return HomeGuideGroupGradientFragment.newInstance()
    }


}
