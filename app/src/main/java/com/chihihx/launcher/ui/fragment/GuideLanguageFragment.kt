package com.chihihx.launcher.ui.fragment

import android.os.Bundle
import com.open.system.SystemUtils
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import com.chihihx.launcher.R
import com.chihihx.launcher.bean.Language
import com.chihihx.launcher.databinding.FragmentSetLanguageBinding
import com.chihihx.launcher.enums.Atts
import com.chihihx.launcher.ext.navigateTo
import com.chihihx.launcher.product.base.product
import com.chihihx.launcher.utils.AndroidSystem
import com.chihihx.launcher.utils.PreferencesUtils

class GuideLanguageFragment : AbsLanguageFragment<FragmentSetLanguageBinding,BaseViewModel>() {


    override fun initView() {
        super.initView()
        showNext(false)

    }

    override fun onSelectLanguage(bean: Language) {
        PreferencesUtils.setProperty(Atts.LANGUAGE, bean.language.toLanguageTag())
        SystemUtils.updateLocale(bean.language)
        AndroidSystem.setSystemLanguage(activity, bean.language)
        jump()
    }



    private fun jump() {
        val fManager = requireActivity().supportFragmentManager

        val fragment = product.isJumpGuidGradient().yes { GuideGroupGradientFragment.newInstance() }.otherwise { GuideDateFragment.newInstance() }

        fManager.navigateTo(R.id.main_browse_fragment, fragment)

    }




    companion object {
        @JvmStatic
        fun newInstance(): GuideLanguageFragment {
            val args = Bundle()
            val fragment = GuideLanguageFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
