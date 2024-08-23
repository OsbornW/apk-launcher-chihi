package com.soya.launcher.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentManager
import com.soya.launcher.R
import com.soya.launcher.cache.AppCache

class FocusFragment : AbsFragment(), View.OnClickListener {
    private var mNextView: View? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_focus
    }

    override fun init(view: View, inflater: LayoutInflater) {
        super.init(view, inflater)
        mNextView = view.findViewById(R.id.next)
    }

    override fun initBind(view: View, inflater: LayoutInflater) {
        super.initBind(view, inflater)
        mNextView!!.setOnClickListener { v: View -> this.onClick(v) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestFocus(mNextView)
    }


    override fun onClick(v: View) {
        if (v == mNextView) {

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

    override fun getWallpaperView(): Int {
        return R.id.wallpaper
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
