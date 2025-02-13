package com.soya.launcher.ui.fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.LogUtils.A
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.hjq.shape.view.ShapeRadioButton
import com.shudong.lib_base.base.BaseVMFragment
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.SWITCH_HOME
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.sendLiveEventData
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.bean.InstallModeDto
import com.soya.launcher.cache.AppCache
import com.soya.launcher.databinding.FragmentDesktopSelectBinding
import com.soya.launcher.h6.H6Manager


class DesktopSelectFragment : BaseVMFragment<FragmentDesktopSelectBinding,BaseViewModel>() {


    private var lastCheckedPosition = -1

    override fun initView() {
        var installMode = AppCache.curDesktop
        val list = mutableListOf<InstallModeDto>()
        list.add(InstallModeDto( "桌面1"))
        list.add(InstallModeDto( "桌面2"))

        when (installMode) {
            0 -> list[0].isChecked = true
            1 -> list[1].isChecked = true
            2 -> list[2].isChecked = true
            3 -> list[3].isChecked = true
        }
        lastCheckedPosition = installMode
        mBind.rvApps.let {
            it.linear().setup {
                addType<InstallModeDto>(R.layout.item_install_mode)
                onBind {
                    val dto = _data as InstallModeDto
                    val rbMode = findView<ShapeRadioButton>(R.id.rb_mode)
                    rbMode.text = dto.modeName
                    rbMode.isChecked = dto.isChecked
                    itemView.clickNoRepeat {
                        //H6Manager.getInstance(requireContext())?.screenMirror = modelPosition
                        if(!dto.isChecked){
                            dto.isChecked = true
                            (mutable[lastCheckedPosition] as InstallModeDto).isChecked = false
                            notifyItemChanged(lastCheckedPosition)
                            rbMode.isChecked = true
                            lastCheckedPosition = modelPosition
                            sendLiveEventData(SWITCH_HOME, modelPosition)
                            AppCache.curDesktop = modelPosition
                        }

                    }
                }

            }.models = list

        }



        mBind.rvApps.apply {
            postDelayed({
                requestFocus()
                layoutManager?.findViewByPosition(0)?.requestFocus()
            }, 0)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            DesktopSelectFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}