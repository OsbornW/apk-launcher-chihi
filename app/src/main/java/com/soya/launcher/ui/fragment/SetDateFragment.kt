package com.soya.launcher.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.shudong.lib_base.base.BaseViewModel
import com.soya.launcher.R
import com.soya.launcher.databinding.FragmentSetDateBinding
import com.soya.launcher.ui.fragment.GuideWifiListFragment.showNext

class SetDateFragment : AbsDateFragment<FragmentSetDateBinding,BaseViewModel>() {


    override fun initView() {
        super.initView()
        showNext(false)
        showWifi(false)
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
