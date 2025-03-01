package com.chihihx.launcher.ui.fragment

import android.os.Bundle
import com.shudong.lib_base.base.BaseViewModel
import com.chihihx.launcher.databinding.FragmentSetDateBinding

class SetDateFragment : AbsDateFragment<FragmentSetDateBinding,BaseViewModel>() {


    override fun initView() {
        super.initView()
        showNext(false)
    }



    companion object {
        fun newInstance(): SetDateFragment {
            val args = Bundle()

            val fragment = SetDateFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
