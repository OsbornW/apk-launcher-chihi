package com.soya.launcher.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.shudong.lib_base.ext.e
import com.soya.launcher.R
import com.soya.launcher.ui.fragment.AppsFragment

class AppsActivity : AbsActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        "开始启动2".e("zy1998")
        super.onCreate(savedInstanceState)
    }
    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun getContainerId(): Int {
        return R.id.main_browse_fragment
    }

    override fun getFragment(): Fragment {
        return AppsFragment.newInstance()
    }

    override fun onResume() {
        super.onResume()
        "开始启动3".e("zy1998")
    }
}
