package com.chihihx.launcher.ui.fragment

import android.os.Bundle
import com.shudong.lib_base.base.BaseViewModel
import com.chihihx.launcher.databinding.FragmentSetDateBinding

class GuideDateFragment : AbsDateFragment<FragmentSetDateBinding,BaseViewModel>() {


    override fun initView() {
        super.initView()
        showNext(true)
    }

    override var isShowAllItem: Boolean = false

    companion object {
        fun newInstance(): GuideDateFragment {
            val args = Bundle()

            val fragment = GuideDateFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
