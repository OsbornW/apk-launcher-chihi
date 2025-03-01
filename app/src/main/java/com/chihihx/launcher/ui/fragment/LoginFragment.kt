package com.chihihx.launcher.ui.fragment

import android.os.Bundle
import com.shudong.lib_base.base.BaseViewModel
import com.chihihx.launcher.BaseWallPaperFragment
import com.chihihx.launcher.R
import com.chihihx.launcher.databinding.FragmentLoginBinding

class LoginFragment : BaseWallPaperFragment<FragmentLoginBinding,BaseViewModel>() {


    override fun initView() {
        mBind.layout.title.text = getString(R.string.login)
        mBind.login.apply { post { requestFocus() } }

    }




    companion object {
        fun newInstance(): LoginFragment {
            val args = Bundle()

            val fragment = LoginFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
