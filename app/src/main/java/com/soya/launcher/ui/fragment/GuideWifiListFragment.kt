package com.soya.launcher.ui.fragment

import android.os.Bundle

object GuideWifiListFragment : AbsWifiListFragment() {
    @JvmStatic
    fun newInstance(): GuideWifiListFragment {
        val args = Bundle()

        val fragment: GuideWifiListFragment = GuideWifiListFragment
        fragment.arguments = args
        return fragment
    }
}
