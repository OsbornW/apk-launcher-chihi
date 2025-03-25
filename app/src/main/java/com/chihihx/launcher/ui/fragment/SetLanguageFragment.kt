package com.chihihx.launcher.ui.fragment

import android.content.res.Configuration
import android.os.Bundle
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.RECREATE_MAIN
import com.shudong.lib_base.ext.sendLiveEventData
import com.chihihx.launcher.bean.Language
import com.chihihx.launcher.databinding.FragmentSetLanguageBinding
import com.chihihx.launcher.enums.Atts
import com.chihihx.launcher.manager.PreferencesManager
import com.chihihx.launcher.utils.AndroidSystem
import com.chihihx.launcher.utils.PreferencesUtils
import com.open.system.SystemUtils
import com.shudong.lib_base.ext.LANGUAGE_CHANGED
import com.shudong.lib_base.ext.obseverLiveEvent
import java.util.Locale

class SetLanguageFragment : AbsLanguageFragment<FragmentSetLanguageBinding,BaseViewModel>() {


    override fun initView() {
        super.initView()
        showNext(false)
    }



    override fun onSelectLanguage(bean: Language) {
        PreferencesUtils.setProperty(Atts.LANGUAGE, bean.language.toLanguageTag())
        SystemUtils.updateLocale(bean.language)
        AndroidSystem.setSystemLanguage(activity, bean.language)

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
