package com.soya.launcher.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.shudong.lib_base.base.BaseViewModel
import com.soya.launcher.databinding.FragmentSetDateBinding
import com.soya.launcher.ui.fragment.GuideWifiListFragment.showNext

class GuideDateFragment : AbsDateFragment<FragmentSetDateBinding,BaseViewModel>() {


    override fun initView() {
        super.initView()
        showNext(true)
        showWifi(false)
    }

    companion object {
        fun newInstance(): GuideDateFragment {
            val args = Bundle()

            val fragment = GuideDateFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
