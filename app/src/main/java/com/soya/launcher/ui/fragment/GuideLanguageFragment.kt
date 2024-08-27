package com.soya.launcher.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentManager
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.yes
import com.soya.launcher.R
import com.soya.launcher.bean.Language
import com.soya.launcher.cache.AppCache
import com.soya.launcher.cache.AppCache.isGuidChageLanguage
import com.soya.launcher.databinding.FragmentSetLanguageBinding
import com.soya.launcher.enums.Atts
import com.soya.launcher.utils.AndroidSystem
import com.soya.launcher.utils.PreferencesUtils

class GuideLanguageFragment : AbsLanguageFragment<FragmentSetLanguageBinding,BaseViewModel>() {


    override fun initView() {
        super.initView()
        showNext(false)
        "我又进来了引导语言：：$isGuidChageLanguage".e("zengyue2")
        isGuidChageLanguage.yes {
            jump()
        }
    }

    override fun onSelectLanguage(bean: Language) {
        isGuidChageLanguage = true
        PreferencesUtils.setProperty(Atts.LANGUAGE, bean.language.toLanguageTag())
        AndroidSystem.setSystemLanguage(activity, bean.language)
        jump()
    }

    override fun onNext() {
        super.onNext()
        jump()
    }


    val fragmentTag = GuideGroupGradientFragment::class.java.name

    private fun jump() {
        val fManager = activity!!.supportFragmentManager

        // 检查当前显示的 Fragment 是否已经是目标 Fragment
        /*val currentFragment = fManager.findFragmentById(R.id.main_browse_fragment)
        if (currentFragment != null && currentFragment::class.java.name == fragmentTag) {
            // 如果当前 Fragment 就是目标 Fragment，直接返回，不做任何操作
            return
        }*/

        for (i in fManager.backStackEntryCount - 1 downTo 0) {
            val entry = fManager.getBackStackEntryAt(i)
            if (entry.name == fragmentTag) {
                fManager.popBackStack(entry.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                break
            }
        }


        // 否则，执行替换操作，并将新的 Fragment 添加到回退栈中
        fManager.beginTransaction()
            .replace(R.id.main_browse_fragment, GuideGroupGradientFragment.newInstance(), fragmentTag)
            .addToBackStack(null)
            .commitAllowingStateLoss()
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
