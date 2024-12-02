package com.soya.launcher.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.drake.brv.utils.addModels
import com.drake.brv.utils.grid
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.LANGUAGE_CHANGED
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.dimenValue
import com.shudong.lib_base.ext.margin
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.obseverLiveEvent
import com.shudong.lib_base.ext.widthAndHeight
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.PACKAGE_NAME_713_BOX_DISPLAY
import com.soya.launcher.R
import com.soya.launcher.SETTING_ABOUT
import com.soya.launcher.SETTING_BLUETOOTH
import com.soya.launcher.SETTING_DATE
import com.soya.launcher.SETTING_DISPLAY
import com.soya.launcher.SETTING_KEYBOARD
import com.soya.launcher.SETTING_LAUNGUAGE
import com.soya.launcher.SETTING_MORE
import com.soya.launcher.SETTING_NETWORK
import com.soya.launcher.SETTING_PROJECTOR
import com.soya.launcher.SETTING_SOUND
import com.soya.launcher.SETTING_WALLPAPER
import com.soya.launcher.bean.Projector
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.databinding.FragmentMoreBinding
import com.soya.launcher.databinding.FragmentSettingBinding
import com.soya.launcher.databinding.HolderSettingBinding
import com.soya.launcher.ext.openApp
import com.soya.launcher.product.base.product
import com.soya.launcher.ui.activity.AboutActivity
import com.soya.launcher.ui.activity.WallpaperActivity
import com.soya.launcher.utils.AndroidSystem

class MoreFragment : BaseWallPaperFragment<FragmentMoreBinding, BaseViewModel>() {



    override fun initObserver() {


    }



    override fun initdata() {
        setContent()
        mBind.content.apply {
            post {
                requestFocus()
            }
        }
    }


    private fun setContent() {
        mBind.content.setup {
            addType<SettingItem>(R.layout.holder_setting)
            onBind {
                val binding = getBinding<HolderSettingBinding>()
                val dto = getModel<SettingItem>()

                itemView.setOnFocusChangeListener { v, hasFocus ->
                    binding.title.isSelected = hasFocus
                    val animation = AnimationUtils.loadAnimation(
                        v.context, if (hasFocus) R.anim.zoom_in_max else R.anim.zoom_out_max
                    )
                    v.startAnimation(animation)
                    animation.fillAfter = true
                }
                itemView.clickNoRepeat {
                    clickItem(dto)
                }

            }
        }

        mBind.content.setNumColumns(3)

        mBind.content.addModels(product.addMoreItem())

    }


    private fun clickItem(bean: SettingItem?){
        when (bean?.type) {
            SETTING_NETWORK -> product.openWifi()
            SETTING_WALLPAPER -> startActivity(
                Intent(
                    requireContext(),
                    WallpaperActivity::class.java
                )
            )

            SETTING_PROJECTOR -> product.openProjector()
            SETTING_LAUNGUAGE -> product.openLanguageSetting(requireContext())
            SETTING_DATE -> product.openDateSetting(requireContext())
            SETTING_BLUETOOTH -> product.openBluetooth()
            SETTING_ABOUT -> startActivity(
                Intent(
                    requireContext(),
                    AboutActivity::class.java
                )
            )

            SETTING_MORE -> product.openMore()
            SETTING_SOUND -> product.openSound()
            SETTING_KEYBOARD -> AndroidSystem.openInputSetting(requireContext())
            SETTING_DISPLAY -> PACKAGE_NAME_713_BOX_DISPLAY.openApp()

        }
    }




    companion object {
        @JvmStatic
        fun newInstance(): MoreFragment {
            val args = Bundle()

            val fragment = MoreFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
