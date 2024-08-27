package com.soya.launcher.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentManager
import com.shudong.lib_base.base.BaseViewModel
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.cache.AppCache
import com.soya.launcher.databinding.FragmentFocusBinding

class FocusFragment : BaseWallPaperFragment<FragmentFocusBinding,BaseViewModel>(), View.OnClickListener {


    override fun initView() {
        mBind.next.setOnClickListener { v: View -> this.onClick(v) }
        mBind.next.apply { post { requestFocus() } }

    }




    override fun onClick(v: View) {
        if (v == mBind.next) {

            val fManager = activity!!.supportFragmentManager
            if (fManager.backStackEntryCount > 0) {
                fManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
            AppCache.isGuidChageLanguage = false

            val fragmentTag = GuideGroupGradientFragment::class.java.name
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.main_browse_fragment, GuideLanguageFragment.newInstance())
                .addToBackStack(fragmentTag).commit()
        }
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
