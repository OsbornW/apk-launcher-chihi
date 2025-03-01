package com.chihihx.launcher.ui.fragment

import android.os.Bundle
import android.view.View
import com.shudong.lib_base.base.BaseViewModel
import com.chihihx.launcher.databinding.FragmentGradientBinding

object GuideGradientFragment : AbsGradientFragment<FragmentGradientBinding,BaseViewModel>() {
    fun newInstance(): GuideGradientFragment {
        val args = Bundle()

        val fragment = GuideGradientFragment
        fragment.arguments = args
        return fragment
    }

    override fun onClick(p0: View?) {

    }
}
