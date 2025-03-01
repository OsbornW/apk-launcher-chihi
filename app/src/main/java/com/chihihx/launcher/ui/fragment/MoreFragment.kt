package com.chihihx.launcher.ui.fragment

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.drake.brv.utils.addModels
import com.drake.brv.utils.setup
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.dimenValue
import com.shudong.lib_base.ext.margin
import com.shudong.lib_base.ext.no
import com.chihihx.launcher.BaseWallPaperFragment
import com.chihihx.launcher.PACKAGE_NAME_713_BOX_INPUT_METHOD
import com.chihihx.launcher.PACKAGE_NAME_713_BOX_MOUSE
import com.chihihx.launcher.PACKAGE_NAME_713_BOX_SOUND
import com.chihihx.launcher.R
import com.chihihx.launcher.bean.MoreConstants
import com.chihihx.launcher.bean.SettingItem
import com.chihihx.launcher.databinding.FragmentMoreBinding
import com.chihihx.launcher.databinding.HolderSettingBinding
import com.chihihx.launcher.ext.openApp
import com.chihihx.launcher.product.base.product

class MoreFragment : BaseWallPaperFragment<FragmentMoreBinding, BaseViewModel>() {



    override fun initObserver() {


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rootView = view
        val isShowTitle = product.isShowPageTitle()
        isShowTitle.no { rootView.margin(topMargin = com.shudong.lib_dimen.R.dimen.qb_px_20.dimenValue()) }

        mBind.layout.title.text = getString(R.string.more)
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
            MoreConstants.TYPE_MOUSE -> PACKAGE_NAME_713_BOX_MOUSE.openApp()
            MoreConstants.TYPE_SOUND -> PACKAGE_NAME_713_BOX_SOUND.openApp()
            MoreConstants.TYPE_INPUT_METHOD -> PACKAGE_NAME_713_BOX_INPUT_METHOD.openApp()

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
