package com.soya.launcher.ui.fragment

import com.shudong.lib_base.base.BaseViewModel
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.databinding.FragmentBlankBinding

class BlankFragment: BaseWallPaperFragment<FragmentBlankBinding, BaseViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(): BlankFragment {
            val fragment = BlankFragment()
            return fragment
        }
    }

}