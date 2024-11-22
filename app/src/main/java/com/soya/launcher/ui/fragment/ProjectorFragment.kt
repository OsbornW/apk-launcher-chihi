package com.soya.launcher.ui.fragment

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.drake.brv.utils.addModels
import com.drake.brv.utils.setup
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.dimenValue
import com.shudong.lib_base.ext.margin
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.padding
import com.shudong.lib_base.ext.widthAndHeight
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.bean.Projector
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.databinding.FragmentProjectorBinding
import com.soya.launcher.databinding.HolderSetting4Binding
import com.soya.launcher.net.viewmodel.HomeViewModel
import com.soya.launcher.product.base.product

class ProjectorFragment : BaseWallPaperFragment<FragmentProjectorBinding,HomeViewModel>() {


    override fun initView() {
        mBind.layout.title.text = getString(R.string.pojector)
        setContent()

        mBind.content.apply { post { requestFocus() } }
        val isShowTitle = product.isShowPageTitle()
        isShowTitle.no { mBind.root.padding(topPadding = com.shudong.lib_dimen.R.dimen.qb_px_25.dimenValue()) }
    }



    private fun setContent() {

        mBind.content.setup {
            addType<SettingItem>(R.layout.holder_setting_4)
            onBind {
                val binding = getBinding<HolderSetting4Binding>()
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

        mBind.content.setNumColumns(product.projectorColumns())
        mBind.content.addModels(product.addProjectorItem())
    }

    private fun clickItem(bean: SettingItem) {

            mViewModel.clickProjectorItem(bean){
                val type = it.first
                val text = it.second
                when(type){
                    Projector.TYPE_AUTO_RESPONSE->{
                        val tvName = mBind.content.getChildAt(0).findViewById<TextView>(R.id.title)
                        tvName.text = text.toString()
                    }
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
