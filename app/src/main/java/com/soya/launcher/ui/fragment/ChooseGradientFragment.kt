package com.soya.launcher.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.FocusHighlightHelper
import androidx.leanback.widget.ItemBridgeAdapter
import androidx.leanback.widget.VerticalGridView
import com.open.system.ASystemProperties
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.startKtxActivity
import com.shudong.lib_base.ext.yes
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.adapter.SettingAdapter
import com.soya.launcher.bean.Projector
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.databinding.FragmentChooseGradientBinding
import com.soya.launcher.ext.isRK3326
import com.soya.launcher.ui.activity.GradientActivity
import com.soya.launcher.utils.AndroidSystem

class ChooseGradientFragment : BaseWallPaperFragment<FragmentChooseGradientBinding,BaseViewModel>() {
    private val dataList: MutableList<SettingItem?> = ArrayList()
    private var itemBridgeAdapter: ItemBridgeAdapter? = null


    override fun initView() {
        mBind.layout.title.text = getString(R.string.project_gradient)
        setContent()

        setCurMode(true)
        mBind.content.apply { post { requestFocus() } }

    }




    private fun setContent() {
        dataList.clear()
        val arrayObjectAdapter = ArrayObjectAdapter(
            SettingAdapter(
                activity,
                getLayoutInflater(),
                newProjectorCallback(),
                R.layout.holder_setting_5
            )
        )
        itemBridgeAdapter = ItemBridgeAdapter(arrayObjectAdapter)
        FocusHighlightHelper.setupBrowseItemFocusHighlight(
            itemBridgeAdapter,
            FocusHighlight.ZOOM_FACTOR_LARGE,
            false
        )
        mBind.content.setAdapter(itemBridgeAdapter)
        mBind.content.setNumColumns(2)
        mBind.content.setColumnWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
        val isEnalbe = ASystemProperties.getInt("persist.vendor.gsensor.enable", 0) == 1
        dataList.add(
            SettingItem(
                Projector.TYPE_AUTO_MODE,
                if (isEnalbe) getString(R.string.auto) else getString(R.string.close),
                R.drawable.auto
            )
        )
        dataList.add(
            SettingItem(
                Projector.TYPE_SCREEN,
                if (isEnalbe) getString(R.string.auto_calibration) else getString(R.string.manual),
                R.drawable.baseline_screenshot_monitor_100
            )
        )
        arrayObjectAdapter.addAll(0, dataList)
    }

    private fun newProjectorCallback(): SettingAdapter.Callback {
        return object : SettingAdapter.Callback {
            override fun onSelect(selected: Boolean, bean: SettingItem) {}
            override fun onClick(bean: SettingItem) {
                when (bean.type) {
                    Projector.TYPE_SCREEN -> {
                        val isEnalbe =
                            ASystemProperties.getInt("persist.vendor.gsensor.enable", 0) == 1
                        if (isEnalbe) {
                            isRK3326().yes {
                                AndroidSystem.openActivityName(
                                    requireContext(),
                                    "com.lei.hxkeystone",
                                    "com.lei.hxkeystone.CheckGsensorActivity"
                                )

                            }.otherwise {
                                AndroidSystem.openActivityName(
                                    requireContext(),
                                    "com.hxdevicetest",
                                    "com.hxdevicetest.CheckGsensorActivity"
                                )
                            }

                        } else {
                            isRK3326().yes {
                                AndroidSystem.openActivityName(
                                    requireContext(),
                                    "com.lei.hxkeystone",
                                    "com.lei.hxkeystone.FourPoint"
                                )
                            }.otherwise {
                                startKtxActivity<GradientActivity>()
                            }
                        }
                    }

                    Projector.TYPE_AUTO_MODE -> {
                        var isEnalbe =
                            ASystemProperties.getInt("persist.vendor.gsensor.enable", 0) == 1

                        setCurMode(!isEnalbe)

                    }
                }
            }
        }
    }

    private fun setCurMode(isEnalbe: Boolean) {
        ASystemProperties.set(
            "persist.vendor.gsensor.enable",
            if (isEnalbe) "1" else "0"
        )
        dataList[0]!!.setName(
            if (isEnalbe) getString(R.string.auto) else getString(
                R.string.close
            )
        )
        dataList[1]!!.setName(
            if (isEnalbe) getString(R.string.auto_calibration) else getString(
                R.string.manual
            )
        )
        itemBridgeAdapter!!.notifyDataSetChanged()
    }

    companion object {
        @JvmStatic
        fun newInstance(): ChooseGradientFragment {
            val args = Bundle()
            val fragment = ChooseGradientFragment()
            fragment.setArguments(args)
            return fragment
        }
    }
}
