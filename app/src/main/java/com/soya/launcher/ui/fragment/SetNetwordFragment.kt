package com.soya.launcher.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.FocusHighlightHelper
import androidx.leanback.widget.HorizontalGridView
import androidx.leanback.widget.ItemBridgeAdapter
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.appContext
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.adapter.SettingAdapter
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.databinding.FragmentSetNetwordBinding
import com.soya.launcher.utils.AndroidSystem

class SetNetwordFragment : BaseWallPaperFragment<FragmentSetNetwordBinding,BaseViewModel>(), View.OnClickListener {


    override fun initView() {
        setContent()
        mBind.next.setOnClickListener(this)
        AndroidSystem.openWifiSetting(requireContext())
    }


    private fun setContent() {
        val arrayObjectAdapter = ArrayObjectAdapter(
            SettingAdapter(
                requireContext(),
                LayoutInflater.from(appContext),
                newCallback(),
                R.layout.holder_setting_2
            )
        )
        val itemBridgeAdapter = ItemBridgeAdapter(arrayObjectAdapter)
        FocusHighlightHelper.setupBrowseItemFocusHighlight(
            itemBridgeAdapter,
            FocusHighlight.ZOOM_FACTOR_MEDIUM,
            false
        )
        mBind.content.adapter = itemBridgeAdapter
        mBind.content.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT)

        arrayObjectAdapter.addAll(
            0,
            listOf(SettingItem(1, getString(R.string.network), R.drawable.baseline_wifi_100))
        )
        mBind.content.selectedPosition = 0
    }

    fun newCallback(): SettingAdapter.Callback {
        return object : SettingAdapter.Callback {
            override fun onSelect(selected: Boolean, bean: SettingItem?) {
            }

            override fun onClick(bean: SettingItem?) {
                when (bean?.type) {
                    0 -> AndroidSystem.openDateSetting(requireContext())
                    1 -> AndroidSystem.openWifiSetting(requireContext())
                }
            }
        }
    }

    override fun onClick(v: View) {
        if (v == mBind.next) {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.main_browse_fragment, ChooseWallpaperFragment.newInstance()).commit()
        }
    }
    
    companion object {
        fun newInstance(): SetNetwordFragment {
            val args = Bundle()

            val fragment = SetNetwordFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
