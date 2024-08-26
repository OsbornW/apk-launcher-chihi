package com.soya.launcher.ui.fragment

import android.os.Bundle

class WifiListFragment : AbsWifiListFragment() {
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
