package com.soya.launcher.ui.fragment

import android.os.Bundle
import com.shudong.lib_base.base.BaseViewModel
import com.soya.launcher.databinding.FragmentGuideGroupGradientBinding

class HomeGuideGroupGradientFragment : AbsGroupGradientFragment<FragmentGuideGroupGradientBinding,BaseViewModel>() {
    override val isGuide: Boolean
        get() = false

    companion object {
        fun newInstance(): HomeGuideGroupGradientFragment {
            val args = Bundle()

            val fragment = HomeGuideGroupGradientFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
