package com.soya.launcher.ui.fragment

import android.os.Bundle
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.leanback.widget.ItemBridgeAdapter
import com.drake.brv.utils.addModels
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.setup
import com.open.system.ASystemProperties
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.dimenValue
import com.shudong.lib_base.ext.startKtxActivity
import com.shudong.lib_base.ext.widthAndHeight
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.bean.Projector
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.databinding.FragmentChooseGradientBinding
import com.soya.launcher.databinding.HolderSetting5Binding
import com.soya.launcher.p50.AUTO_CORRECTION
import com.soya.launcher.p50.setFunction
import com.soya.launcher.product.base.product
import com.soya.launcher.ui.activity.GradientActivity

class ChooseGradientFragment :
    BaseWallPaperFragment<FragmentChooseGradientBinding, BaseViewModel>() {
    private val dataList: MutableList<SettingItem?> = mutableListOf()


    override fun initView() {
        mBind.layout.title.text = getString(R.string.project_gradient)
        setContent()

        setCurMode(true)
        mBind.content.apply { post { requestFocus() } }

    }


    private fun setContent() {
        dataList.clear()

        mBind.content.setup {
            addType<SettingItem>(R.layout.holder_setting_5)
            onBind {
                val binding = getBinding<HolderSetting5Binding>()
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


        mBind.content.setNumColumns(2)
        mBind.content.setColumnWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
        val isEnalbe = ASystemProperties.getInt("persist.vendor.gsensor.enable", 0) == 1
        dataList.clear()
        product.addCalibrationItem(isEnalbe)?.let { dataList.addAll(it) }
        mBind.content.addModels(dataList)
    }

    private fun clickItem(bean: SettingItem) {
        when (bean.type) {
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



    private fun setCurMode(isEnalbe: Boolean) {
        product.initCalibrationText(isEnalbe, dataList) {
            dataList.forEachIndexed { index, settingItem ->
                mBind.content.bindingAdapter.notifyItemChanged(index)
            }

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
