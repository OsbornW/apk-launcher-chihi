package com.soya.launcher.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentManager
import com.open.system.SystemUtils
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.isRepeatExcute
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import com.soya.launcher.R
import com.soya.launcher.bean.Language
import com.soya.launcher.cache.AppCache
import com.soya.launcher.cache.AppCache.isGuidChageLanguage
import com.soya.launcher.databinding.FragmentSetLanguageBinding
import com.soya.launcher.enums.Atts
import com.soya.launcher.ext.navigateTo
import com.soya.launcher.product.base.product
import com.soya.launcher.utils.AndroidSystem
import com.soya.launcher.utils.PreferencesUtils

class GuideLanguageFragment : AbsLanguageFragment<FragmentSetLanguageBinding,BaseViewModel>() {


    override fun initView() {
        super.initView()
        showNext(false)

    }

    override fun onSelectLanguage(bean: Language) {
        PreferencesUtils.setProperty(Atts.LANGUAGE, bean.language.toLanguageTag())
        SystemUtils.updateLocale(bean.language)
        AndroidSystem.setSystemLanguage(activity, bean.language)
        "选中语言跳转了".e("chihi_error")
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
