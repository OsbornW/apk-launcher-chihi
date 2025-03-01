package com.chihihx.launcher.ui.fragment

import com.open.system.ASystemProperties
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.startKtxActivity
import com.shudong.lib_base.ext.yes
import com.chihihx.launcher.BaseWallPaperFragment
import com.chihihx.launcher.R
import com.chihihx.launcher.databinding.FragmentGuideGroupGradientBinding
import com.chihihx.launcher.ext.isRK3326
import com.chihihx.launcher.ext.navigateTo
import com.chihihx.launcher.ui.activity.GradientActivity
import com.chihihx.launcher.utils.AndroidSystem

open class AbsGroupGradientFragment<VDB : FragmentGuideGroupGradientBinding, VM : BaseViewModel>
    : BaseWallPaperFragment<VDB,VM>() {


    private fun setCurMode(isEnalbe: Boolean) {
        ASystemProperties.set(
            "persist.vendor.gsensor.enable",
            if (isEnalbe) "1" else "0"
        )

        val isEnalbe = ASystemProperties.getInt("persist.vendor.gsensor.enable", 0)


    }

    override fun initView() {
        mBind.manual.setOnKeyListener { view, i, keyEvent ->
            false
        }
        setCurMode(true)

        mBind.manual.apply {
            post {
                requestFocus()
            }
        }
    }


    override fun initClick() {
        mBind.manual.setOnClickListener {
            setCurMode(false)

            isRK3326().yes {
                AndroidSystem.openActivityName(
                    requireContext(),
                    "com.lei.hxkeystone",
                    "com.lei.hxkeystone.FourPoint"
                )
            }.otherwise {
                startKtxActivity<GradientActivity>()
            }


        }
        mBind.skip.setOnClickListener {
            if (isGuide) {
                requireActivity().supportFragmentManager.navigateTo(
                    R.id.main_browse_fragment,
                    GuideDateFragment.newInstance()
                )
            } else {
                requireActivity().finish()
            }
        }
    }

    override fun initdata() {
        mBind.skip.text = if (isGuide) getString(R.string.next) else getString(R.string.done)
        mBind.skipTip.text =
            if (isGuide) getString(R.string.guide_group_gradient_tip_next) else getString(R.string.guide_group_gradient_tip_done)

    }




    protected open val isGuide: Boolean
        protected get() = true

    private fun setEnable(isEnalbe: Boolean) {
        ASystemProperties.set("persist.vendor.gsensor.enable", if (isEnalbe) "1" else "0")
    }
}
