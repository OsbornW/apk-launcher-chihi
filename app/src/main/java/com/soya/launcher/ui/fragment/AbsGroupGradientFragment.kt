package com.soya.launcher.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.open.system.ASystemProperties
import com.shudong.lib_base.ext.d
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.stringValue
import com.shudong.lib_base.ext.yes
import com.soya.launcher.R

open class AbsGroupGradientFragment : AbsFragment() {
    private var mManualView: View? = null
    private var mSkipView: TextView? = null
    private var mSkipTipView: TextView? = null
    private var root:FrameLayout?=null
    override fun getWallpaperView(): Int {
        return R.id.wallpaper
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_guide_group_gradient
    }

    private fun setCurMode(isEnalbe: Boolean) {
        ASystemProperties.set(
            "persist.vendor.gsensor.enable",
            if (isEnalbe) "1" else "0"
        )

    }

    override fun init(view: View, inflater: LayoutInflater) {
        super.init(view, inflater)
        mManualView = view.findViewById(R.id.manual)
        mSkipView = view.findViewById(R.id.skip)
        mSkipTipView = view.findViewById(R.id.skip_tip)
        root = view.findViewById(R.id.root)
        mManualView?.setOnKeyListener { view, i, keyEvent ->

            false
        }

            setCurMode(true)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestFocus(mManualView)
    }

    override fun initBefore(view: View, inflater: LayoutInflater) {
        super.initBefore(view, inflater)
        mManualView!!.setOnClickListener {
            //setEnable(false)
            //val isEnalbe = ASystemProperties.getInt("persist.vendor.gsensor.enable", 0) == 1
                setCurMode(false)

            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.main_browse_fragment, GradientFragment.newInstance())
                .addToBackStack(null).commit()
        }
        mSkipView!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if (isGuide) activity!!.supportFragmentManager.beginTransaction()
                    .replace(R.id.main_browse_fragment, GuideDateFragment.newInstance())
                    .addToBackStack(null).commit() else activity!!.finish()
            }
        })
    }

    override fun initBind(view: View, inflater: LayoutInflater) {
        super.initBind(view, inflater)
        mSkipView!!.text = if (isGuide) getString(R.string.next) else getString(R.string.done)
        mSkipTipView!!.text =
            if (isGuide) getString(R.string.guide_group_gradient_tip_next) else getString(R.string.guide_group_gradient_tip_done)
    }

    protected open val isGuide: Boolean
        protected get() = true

    private fun setEnable(isEnalbe: Boolean) {
        ASystemProperties.set("persist.vendor.gsensor.enable", if (isEnalbe) "1" else "0")
    }
}
