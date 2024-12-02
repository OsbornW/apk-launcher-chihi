package com.soya.launcher.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.drake.brv.utils.addModels
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
import com.soya.launcher.R
import com.soya.launcher.SETTING_ABOUT
import com.soya.launcher.SETTING_BLUETOOTH
import com.soya.launcher.SETTING_DATE
import com.soya.launcher.SETTING_KEYBOARD
import com.soya.launcher.SETTING_LAUNGUAGE
import com.soya.launcher.SETTING_MORE
import com.soya.launcher.SETTING_NETWORK
import com.soya.launcher.SETTING_PROJECTOR
import com.soya.launcher.SETTING_SOUND
import com.soya.launcher.SETTING_WALLPAPER
import com.soya.launcher.bean.Projector
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.databinding.FragmentSettingBinding
import com.soya.launcher.databinding.HolderSettingBinding
import com.soya.launcher.product.base.product
import com.soya.launcher.ui.activity.AboutActivity
import com.soya.launcher.ui.activity.WallpaperActivity
import com.soya.launcher.utils.AndroidSystem

class SettingFragment : BaseWallPaperFragment<FragmentSettingBinding, BaseViewModel>() {



    override fun initObserver() {

        obseverLiveEvent<Boolean>(LANGUAGE_CHANGED){
            mBind.content.apply {
                post {
                    initdata()
                    scrollToPosition(4)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rootView = view
        val isShowTitle = product.isShowPageTitle()
        isShowTitle.no { rootView.margin(topMargin = com.shudong.lib_dimen.R.dimen.qb_px_20.dimenValue()) }
    }


    override fun initdata() {
        mBind.layout.title.text = getString(R.string.setting)
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

                when (dto.type) {
                    Projector.TYPE_AUTO_CALIBRATION, Projector.TYPE_MANUAL_CALIBRATION -> {
                        binding.image.widthAndHeight(
                            com.shudong.lib_dimen.R.dimen.qb_px_70.dimenValue(),
                            com.shudong.lib_dimen.R.dimen.qb_px_70.dimenValue()
                        )

                    }

                    else -> {
                        binding.image.widthAndHeight(
                            com.shudong.lib_dimen.R.dimen.qb_px_50.dimenValue(),
                            com.shudong.lib_dimen.R.dimen.qb_px_50.dimenValue()
                        )
                    }
                }
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

        mBind.content.setNumColumns(4)

        mBind.content.addModels(product.addSettingItem())

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

        }
    }




    companion object {
        @JvmStatic
        fun newInstance(): SettingFragment {
            val args = Bundle()

            val fragment = SettingFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
