package com.chihihx.launcher.ui.fragment

import android.os.Bundle
import com.shudong.lib_base.base.BaseViewModel
import com.chihihx.launcher.databinding.FragmentGuideGroupGradientBinding

class GuideGroupGradientFragment : AbsGroupGradientFragment<FragmentGuideGroupGradientBinding,BaseViewModel>() {


    override val isGuide: Boolean
        get() = true

    companion object {
        fun newInstance(): GuideGroupGradientFragment {
            val args = Bundle()

            val fragment = GuideGroupGradientFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
