package com.soya.launcher.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.FocusHighlightHelper
import androidx.leanback.widget.ItemBridgeAdapter
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.startKtxActivity
import com.shudong.lib_base.ext.yes
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.adapter.SettingAdapter
import com.soya.launcher.bean.Projector
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.databinding.FragmentProjectorBinding
import com.soya.launcher.ext.isH6
import com.soya.launcher.ext.isRK3326
import com.soya.launcher.net.viewmodel.HomeViewModel
import com.soya.launcher.product.base.product
import com.soya.launcher.ui.activity.ChooseGradientActivity
import com.soya.launcher.ui.activity.GradientActivity
import com.soya.launcher.ui.activity.InstallModeActivity
import com.soya.launcher.ui.activity.ScaleScreenActivity
import com.soya.launcher.utils.AndroidSystem

class ProjectorFragment : BaseWallPaperFragment<FragmentProjectorBinding,HomeViewModel>() {


    override fun initView() {
        mBind.layout.title.text = getString(R.string.pojector)
        setContent()

        mBind.content.apply { post { requestFocus() } }
    }



    private fun setContent() {
        val arrayObjectAdapter = ArrayObjectAdapter(
            SettingAdapter(
                activity,
                layoutInflater,
                newProjectorCallback(),
                R.layout.holder_setting_4
            )
        )
        val itemBridgeAdapter = ItemBridgeAdapter(arrayObjectAdapter)
        FocusHighlightHelper.setupBrowseItemFocusHighlight(
            itemBridgeAdapter,
            FocusHighlight.ZOOM_FACTOR_LARGE,
            false
        )
        mBind.content.adapter = itemBridgeAdapter
        mBind.content.setNumColumns(product.projectorColumns())
        arrayObjectAdapter.addAll(0, product.addProjectorItem())
    }

    private fun newProjectorCallback(): SettingAdapter.Callback {
        return object : SettingAdapter.Callback {
            override fun onSelect(selected: Boolean, bean: SettingItem) {
            }

            override fun onClick(bean: SettingItem) {
                mViewModel.clickProjectorItem(bean)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): ProjectorFragment {
            val args = Bundle()

            val fragment = ProjectorFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
