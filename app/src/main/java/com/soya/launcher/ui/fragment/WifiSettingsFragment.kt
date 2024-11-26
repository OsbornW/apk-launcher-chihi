package com.soya.launcher.ui.fragment

import android.os.Bundle
import com.shudong.lib_base.base.BaseViewModel
import com.soya.launcher.databinding.FragmentWifiListBinding

class WifiSettingsFragment : AbsWifiListFragment<FragmentWifiListBinding,BaseViewModel>() {
    override fun initView() {
        super.initView()
        showNext(false)
    }


    override fun useNext(): Boolean {
        return false
    }

    companion object {
        fun newInstance(): WifiSettingsFragment {
            val args = Bundle()

            val fragment = WifiSettingsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
