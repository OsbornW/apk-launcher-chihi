package com.soya.launcher.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentManager
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.obseverLiveEvent
import com.shudong.lib_base.ext.stringValue
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.cache.AppCache
import com.soya.launcher.databinding.FragmentFocusBinding
import com.soya.launcher.ext.navigateTo

class FocusFragment : BaseWallPaperFragment<FragmentFocusBinding, BaseViewModel>(),
    View.OnClickListener {


    override fun initView() {
        mBind.tvNext.setOnClickListener { v: View -> this.onClick(v) }
        mBind.tvNext.apply { post { requestFocus() } }

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
        mBind.tvTip.text = R.string.focus_guide.stringValue()
        mBind.tvNext.text = R.string.next.stringValue()

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
