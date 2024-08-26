package com.soya.launcher.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import com.shudong.lib_base.base.BaseViewModel
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.databinding.FragmentWelcomeBinding

class WelcomeFragment : BaseWallPaperFragment<FragmentWelcomeBinding, BaseViewModel>() {
    private var uiHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiHandler = Handler()
    }

    override fun initView() {
        uiHandler!!.postDelayed({
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.main_browse_fragment, FocusFragment.newInstance()).commit()
            //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_browse_fragment, RemoteControlFragment.newInstance(true)).commit();
        }, 2000)
    }



    companion object {
        fun newInstance(): WelcomeFragment {
            val args = Bundle()

            val fragment = WelcomeFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
