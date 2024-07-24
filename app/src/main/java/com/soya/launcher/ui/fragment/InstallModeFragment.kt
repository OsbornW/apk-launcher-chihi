package com.soya.launcher.ui.fragment

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.os.HandlerCompat.postDelayed
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.hjq.shape.view.ShapeRadioButton
import com.shudong.lib_base.base.BaseVMFragment
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.clickNoRepeat
import com.soya.launcher.R
import com.soya.launcher.bean.InstallModeDto
import com.soya.launcher.databinding.FragmentInstallModeBinding
import com.soya.launcher.h6.H6Manager

class InstallModeFragment : BaseVMFragment<FragmentInstallModeBinding, BaseViewModel>() {


    var lastCheckedPosition = -1

    @RequiresApi(Build.VERSION_CODES.N)
    override fun initView() {
        var installMode = H6Manager.getInstance(requireContext())?.screenMirror
        val list = mutableListOf<InstallModeDto>()
        list.add(InstallModeDto( "桌面正投"))
        list.add(InstallModeDto( "桌面背投"))
        list.add(InstallModeDto( "吊装正投"))
        list.add(InstallModeDto( "吊装背投"))
        when (installMode) {
            0 -> list[0].isChecked = true
            1 -> list[1].isChecked = true
            2 -> list[2].isChecked = true
            3 -> list[3].isChecked = true
        }
        lastCheckedPosition = installMode?:0
        mBind.rvApps.let {
            it.linear().setup {
                addType<InstallModeDto>(R.layout.item_install_mode)
                onBind {
                    val dto = _data as InstallModeDto
                    val rbMode = findView<ShapeRadioButton>(R.id.rb_mode)
                    rbMode.isChecked = dto.isChecked
                    itemView.clickNoRepeat {
                        H6Manager.getInstance(requireContext())?.screenMirror = modelPosition
                        dto.isChecked = true
                        (mutable[lastCheckedPosition] as InstallModeDto).isChecked = false
                        notifyItemChanged(lastCheckedPosition)
                        rbMode.isChecked = true
                        lastCheckedPosition = modelPosition
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
            InstallModeFragment().apply {
                arguments = Bundle()
            }
    }
}