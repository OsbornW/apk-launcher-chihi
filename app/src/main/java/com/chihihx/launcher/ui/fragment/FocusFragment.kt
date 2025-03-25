package com.chihihx.launcher.ui.fragment

import android.os.Bundle
import android.view.View
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.stringValue
import com.chihihx.launcher.BaseWallPaperFragment
import com.chihihx.launcher.R
import com.chihihx.launcher.databinding.FragmentFocusBinding
import com.chihihx.launcher.ext.navigateTo
import com.shudong.lib_base.ext.REFRESH_GUID
import com.shudong.lib_base.ext.obseverLiveEvent

class FocusFragment : BaseWallPaperFragment<FragmentFocusBinding, BaseViewModel>(),
    View.OnClickListener {


    override fun initView() {
        mBind.tvNext.setOnClickListener { v: View -> this.onClick(v) }
        mBind.tvNext.apply { post { requestFocus() } }

        obseverLiveEvent<Boolean>(REFRESH_GUID){
            mBind.tvTip.text = R.string.focus_guide.stringValue()
            mBind.tvNext.text = R.string.next.stringValue()

        }

    }


    override fun onClick(v: View) {
        if (v == mBind.tvNext) {
            requireActivity().supportFragmentManager.navigateTo(
                R.id.main_browse_fragment,
                GuideLanguageFragment.newInstance(), tag = GuideLanguageFragment::class.simpleName
            )
        }
    }

    override fun excuteLang() {

    }



    companion object {
        @JvmStatic
        fun newInstance(): FocusFragment {
            val args = Bundle()

            val fragment = FocusFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
