package com.chihihx.launcher.ui.fragment

import android.os.Bundle
import com.shudong.lib_base.base.BaseViewModel
import com.chihihx.launcher.databinding.FragmentWifiListBinding

object WifiGuidFragment : AbsWifiListFragment<FragmentWifiListBinding, BaseViewModel>() {
    @JvmStatic
    fun newInstance(): WifiGuidFragment {
        val args = Bundle()

        val fragment = WifiGuidFragment
        fragment.arguments = args
        return fragment
    }
}
