package com.soya.launcher.ui.fragment

import android.os.Bundle
import com.shudong.lib_base.base.BaseViewModel
import com.soya.launcher.databinding.FragmentWifiListBinding

class WifiListFragment : AbsWifiListFragment<FragmentWifiListBinding,BaseViewModel>() {
    override fun initView() {
        super.initView()
        showNext(false)
    }


    override fun useNext(): Boolean {
        return false
    }

    companion object {
        fun newInstance(): WifiListFragment {
            val args = Bundle()

            val fragment = WifiListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
