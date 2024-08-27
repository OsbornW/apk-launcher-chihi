package com.soya.launcher.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.e
import com.soya.launcher.cache.AppCache
import com.soya.launcher.databinding.FragmentGuideGroupGradientBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GuideGroupGradientFragment : AbsGroupGradientFragment<FragmentGuideGroupGradientBinding,BaseViewModel>() {
    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(1500)
            AppCache.isGuidChageLanguage = false
        }
    }


    override val isGuide: Boolean
        get() = true

    companion object {
        fun newInstance(): GuideGroupGradientFragment {
            val args = Bundle()

            val fragment = GuideGroupGradientFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
