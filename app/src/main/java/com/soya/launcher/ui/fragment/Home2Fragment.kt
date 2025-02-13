package com.soya.launcher.ui.fragment

import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.drake.brv.utils.addModels
import com.drake.brv.utils.setup
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.getNonSystemApps
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.databinding.FragmentHome2Binding
import com.soya.launcher.databinding.ItemLayoutBinding
import com.soya.launcher.desktop.setAppInfo
import com.soya.launcher.ext.openApp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class Home2Fragment : BaseWallPaperFragment<FragmentHome2Binding,BaseViewModel>() {


    override fun initView() {
        mBind.toolbar.showCurTime(requireActivity())
        mBind.rvApps.apply {
            setup {
                addType<ApplicationInfo>(R.layout.item_layout)
                onBind {
                    val binding = getBinding<ItemLayoutBinding>()
                    val appInfo = getModel<ApplicationInfo>()
                    requireActivity().setAppInfo(
                        appInfo,
                        binding.ivApp,
                        binding.tvAppName,
                        binding.rlSmall,
                        binding.ivAppSmall
                    )
                    binding.rlPic.clickNoRepeat {
                        appInfo.packageName.openApp()
                    }
                    binding.rlPic.setOnFocusChangeListener { view, b ->
                        /*val animation = AnimationUtils.loadAnimation(
                            view.context, if (b) R.anim.zoom_in_max else R.anim.zoom_out_max
                        )
                        animation.fillAfter = true
                        view.startAnimation(animation)*/

                        if(b){
                            view.animate().scaleX(1.2f).scaleY(1.22f)
                                .setDuration(200)
                                .start()
                        }else{
                            view.animate().scaleX(1f).scaleY(1f)
                                .setDuration(200)
                                .start()
                        }

                        if(b)binding.tvAppName.isVisible = true else binding.tvAppName.visibility = INVISIBLE
                    }

                }
            }
        }

        val appsList = requireActivity().getNonSystemApps().map { it.applicationInfo }
        mBind.rvApps.addModels(appsList)
    }



    companion object {

        @JvmStatic
        fun newInstance() =
            Home2Fragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}