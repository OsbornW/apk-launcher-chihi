package com.soya.launcher.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import com.shudong.lib_base.base.BaseViewModel
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.databinding.FragmentLoginBinding

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
