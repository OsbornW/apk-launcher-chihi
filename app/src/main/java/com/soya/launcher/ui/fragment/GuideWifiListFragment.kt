package com.soya.launcher.ui.fragment

import android.os.Bundle
import com.shudong.lib_base.base.BaseViewModel
import com.soya.launcher.databinding.FragmentWifiListBinding

object GuideWifiListFragment : AbsWifiListFragment<FragmentWifiListBinding, BaseViewModel>() {
    @JvmStatic
    fun newInstance(): GuideWifiListFragment {
        val args = Bundle()

        val fragment = GuideWifiListFragment
        fragment.arguments = args
        return fragment
    }
}
