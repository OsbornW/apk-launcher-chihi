package com.soya.launcher.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.ActivityResultLauncher
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.FocusHighlightHelper
import androidx.leanback.widget.ItemBridgeAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.LANGUAGE_CHANGED
import com.shudong.lib_base.ext.UPDATE_WALLPAPER_EVENT
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.isRepeatExcute
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.obseverLiveEvent
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
import com.soya.launcher.adapter.SettingAdapter
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.databinding.FragmentSettingBinding
import com.soya.launcher.handler.PermissionHandler
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
        val arrayObjectAdapter = ArrayObjectAdapter(
            SettingAdapter(
                requireContext(),
                LayoutInflater.from(appContext),
                newCallback(),
                R.layout.holder_setting
            )
        )
        val itemBridgeAdapter = ItemBridgeAdapter(arrayObjectAdapter)
        FocusHighlightHelper.setupBrowseItemFocusHighlight(
            itemBridgeAdapter,
            FocusHighlight.ZOOM_FACTOR_LARGE,
            false
        )
        mBind.content.adapter = itemBridgeAdapter
        mBind.content.setNumColumns(4)

        arrayObjectAdapter.addAll(0, product.addSettingItem())


    }



    fun newCallback(): SettingAdapter.Callback {
        return object : SettingAdapter.Callback {
            override fun onSelect(selected: Boolean, bean: SettingItem?) {
            }

            override fun onClick(bean: SettingItem?) {
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
                    SETTING_SOUND -> AndroidSystem.openVoiceSetting(requireContext())
                    SETTING_KEYBOARD -> AndroidSystem.openInputSetting(requireContext())

                }
            }
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
