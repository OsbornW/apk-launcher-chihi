package com.soya.launcher.ui.fragment

import android.os.Bundle
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.RECREATE_MAIN
import com.shudong.lib_base.ext.sendLiveEventData
import com.soya.launcher.bean.Language
import com.soya.launcher.databinding.FragmentSetLanguageBinding
import com.soya.launcher.enums.Atts
import com.soya.launcher.manager.PreferencesManager
import com.soya.launcher.utils.AndroidSystem
import com.soya.launcher.utils.PreferencesUtils
import java.util.Locale

class SetLanguageFragment : AbsLanguageFragment<FragmentSetLanguageBinding,BaseViewModel>() {


    override fun initView() {
        super.initView()
        showNext(false)
    }

    override fun onSelectLanguage(bean: Language) {
        PreferencesUtils.setProperty(Atts.LANGUAGE, bean.language.toLanguageTag())
        AndroidSystem.setSystemLanguage(
            activity,
            Locale.forLanguageTag(PreferencesManager.getLanguage())
        )
        sendLiveEventData(RECREATE_MAIN,true)
    }

    companion object {
        fun newInstance(): SetLanguageFragment {
            val args = Bundle()

            val fragment = SetLanguageFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
