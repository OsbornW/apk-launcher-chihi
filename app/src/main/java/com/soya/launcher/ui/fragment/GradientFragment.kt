package com.soya.launcher.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.shudong.lib_base.base.BaseViewModel
import com.soya.launcher.databinding.FragmentGradientBinding

class GradientFragment : AbsGradientFragment<FragmentGradientBinding,BaseViewModel>() {

    override fun useLongClick(): Boolean {
        return false
    }

    override fun onClick(p0: View?) {

    }

    companion object {
        fun newInstance(): GradientFragment {
            val args = Bundle()

            val fragment = GradientFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
