package com.soya.launcher.ui.fragment

import android.os.Bundle
import android.view.ViewGroup
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.FocusHighlightHelper
import androidx.leanback.widget.ItemBridgeAdapter
import com.open.system.ASystemProperties
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.currentActivity
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.startKtxActivity
import com.shudong.lib_base.ext.yes
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.PACKAGE_NAME_KEYSTONE_CORRECTION_P50
import com.soya.launcher.R
import com.soya.launcher.adapter.SettingAdapter
import com.soya.launcher.bean.Projector
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.databinding.FragmentChooseGradientBinding
import com.soya.launcher.ext.isRK3326
import com.soya.launcher.ext.openApp
import com.soya.launcher.p50.AUTO_CORRECTION
import com.soya.launcher.p50.setFunction
import com.soya.launcher.product.base.product
import com.soya.launcher.ui.activity.GradientActivity
import com.soya.launcher.utils.AndroidSystem

class ChooseGradientFragment :
    BaseWallPaperFragment<FragmentChooseGradientBinding, BaseViewModel>() {
    private val dataList: MutableList<SettingItem?> = mutableListOf()
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
                requireContext(),
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
        dataList.clear()
        product.addCalibrationItem(isEnalbe)?.let { dataList.addAll(it) }
        arrayObjectAdapter.addAll(0, dataList)
    }

    private fun newProjectorCallback(): SettingAdapter.Callback {
        return object : SettingAdapter.Callback {
            override fun onSelect(selected: Boolean, bean: SettingItem?) {}
            override fun onClick(bean: SettingItem?) {
                when (bean?.type) {
                    Projector.TYPE_KEYSTONE_CORRECTION -> {
                        product.openManualAutoKeystoneCorrection()
                    }

                    Projector.TYPE_KEYSTONE_CORRECTION_MODE -> {
                        var isEnalbe =
                            ASystemProperties.getInt("persist.vendor.gsensor.enable", 0) == 1
                        setCurMode(!isEnalbe)
                    }

                    Projector.TYPE_AUTO_CALIBRATION -> {
                        setFunction(AUTO_CORRECTION)
                        //PACKAGE_NAME_KEYSTONE_CORRECTION_P50.openApp()
                    }
                    Projector.TYPE_MANUAL_CALIBRATION -> {
                        startKtxActivity<GradientActivity>()
                    }
                }
            }
        }
    }

    private fun setCurMode(isEnalbe: Boolean) {
        product.initCalibrationText(isEnalbe, dataList) {
            itemBridgeAdapter!!.notifyDataSetChanged()
        }
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
