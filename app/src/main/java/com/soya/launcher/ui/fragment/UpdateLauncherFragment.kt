package com.soya.launcher.ui.fragment

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.shudong.lib_base.base.BaseVMFragment
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.downloadApkNopkName
import com.shudong.lib_base.ext.jsonToBean
import com.shudong.lib_base.ext.loading.showLoadingView
import com.shudong.lib_base.ext.loading.showLoadingViewDismiss
import com.shudong.lib_base.ext.showErrorToast
import com.shudong.lib_base.ext.stringValue
import com.soya.launcher.R
import com.soya.launcher.bean.UpdateDto
import com.soya.launcher.cache.AppCache
import com.soya.launcher.databinding.FragmentUpdateLauncherBinding
import com.soya.launcher.ext.silentInstallWithMutex
import com.soya.launcher.net.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

class UpdateLauncherFragment : BaseVMFragment<FragmentUpdateLauncherBinding, HomeViewModel>() {

    lateinit var updateDto: UpdateDto
    override fun initView() {
        updateDto = AppCache.updateInfoForLauncher.jsonToBean()
    }

    override fun initClick() {
        mBind.apply {
            tvNextTime.clickNoRepeat {
                // 点击下次再说，一周后再提示
                requireActivity().finish()
            }
            tvCancle.clickNoRepeat {
                // 点击更新
                showLoadingView(R.string.app_text_35.stringValue())
                lifecycleScope.launch {
                    updateDto.downLink?.downloadApkNopkName(this, downloadError = {
                        showLoadingViewDismiss()
                        showErrorToast(R.string.app_text_36.stringValue())
                    }, downloadComplete = { str, destPath ->
                        lifecycleScope.launch {
                            showLoadingView(R.string.app_text_37.stringValue())
                            destPath.silentInstallWithMutex()
                        }

                    }) { progress: Int ->
                        showLoadingView(getString(R.string.app_text_38, progress))
                    }

                    //requireActivity().finish()
                }

            }

            tvOk.clickNoRepeat {
                //点击完成
                requireActivity().finish()
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            UpdateLauncherFragment().apply {
                arguments = Bundle()
            }
    }
}