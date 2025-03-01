package com.chihihx.launcher.ui.fragment

import com.shudong.lib_base.base.BaseViewModel
import com.chihihx.launcher.BaseWallPaperFragment
import com.chihihx.launcher.databinding.FragmentBlankBinding

class BlankFragment: BaseWallPaperFragment<FragmentBlankBinding, BaseViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(): BlankFragment {
            val fragment = BlankFragment()
            return fragment
        }
    }

}