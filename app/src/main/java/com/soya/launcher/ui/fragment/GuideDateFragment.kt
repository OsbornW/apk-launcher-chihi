package com.soya.launcher.ui.fragment

import android.os.Bundle
import com.shudong.lib_base.base.BaseViewModel
import com.soya.launcher.databinding.FragmentSetDateBinding

class GuideDateFragment : AbsDateFragment<FragmentSetDateBinding,BaseViewModel>() {


    override fun initView() {
        super.initView()
        showNext(true)
    }

    companion object {
        fun newInstance(): GuideDateFragment {
            val args = Bundle()

            val fragment = GuideDateFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
