package com.soya.launcher.ui.fragment

import android.os.Build
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.drake.brv.utils.addModels
import com.drake.brv.utils.linear
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setup
import com.shudong.lib_base.base.BaseVMFragment
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.colorValue
import com.shudong.lib_base.ext.downloadApk
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.jsonToBean
import com.shudong.lib_base.ext.jsonToTypeBean
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import com.soya.launcher.R
import com.soya.launcher.bean.UpdateAppsDTO
import com.soya.launcher.cache.AppCache
import com.soya.launcher.databinding.FragmentUpdateAppsBinding
import com.soya.launcher.ext.bindImageView
import com.soya.launcher.ext.getAppIcon
import com.soya.launcher.ext.getAppVersionName
import com.soya.launcher.ext.isAppInstalled
import com.soya.launcher.ext.loadImageWithGlide
import com.soya.launcher.ext.silentInstallWithMutex
import com.soya.launcher.utils.GlideUtils
import com.soya.launcher.view.FlikerProgressBar
import kotlinx.coroutines.launch
import org.raphets.roundimageview.RoundImageView

class UpdateAppsFragment : BaseVMFragment<FragmentUpdateAppsBinding, BaseViewModel>() {

    override fun initView() {

        mBind.rvApps.let {
            it.linear().setup {
                addType<UpdateAppsDTO>(R.layout.item_update)
                onBind {
                    val dto = _data as UpdateAppsDTO
                    val llRoot = findView<LinearLayout>(R.id.ll_root)
                    val ivAppIcon = findView<RoundImageView>(R.id.iv_app_icon)
                    val tvAppName = findView<TextView>(R.id.tv_app_name)
                    val tvAppDate = findView<TextView>(R.id.tv_app_date)
                    val tvVersionInfo = findView<TextView>(R.id.tv_version_info)
                    val pbUpdate = findView<FlikerProgressBar>(R.id.pb_update)
                    tvAppName.text = dto.appName
                    tvAppDate.text = dto.getFormatDate()
                    //dto.iconUrl = "https://hbimg.b0.upaiyun.com/83b258aa7e9f5669ae5787055c0e2463f9809ec965da1-FhXANu_fw658"
                    dto.iconUrl.isNullOrEmpty().yes {
                        ivAppIcon.setImageDrawable(dto.packageName.getAppIcon())
                    }.otherwise {
                        if(dto.packageName.isAppInstalled()){
                            ivAppIcon.setImageDrawable(dto.packageName.getAppIcon())
                        }else{
                            ivAppIcon.loadImageWithGlide(dto.iconUrl!!){
                                ivAppIcon.setImageDrawable(dto.packageName.getAppIcon())
                            }
                        }

                    }

                    tvVersionInfo.text = "${dto.packageName.getAppVersionName()}--->${dto.version}"
                    //pbUpdate.setProgressText(dto.status)
                    llRoot.setOnFocusChangeListener { view, b ->
                        if (b)
                            pbUpdate.setBorderColor(R.color.color_669966.colorValue())
                        else
                            pbUpdate.setBorderColor(R.color.transparent.colorValue())

                    }

                    llRoot.clickNoRepeat {
                        llRoot.isClickable = false
                        pbUpdate.setTextColor(R.color.color_669966.colorValue())
                        pbUpdate.setProgressText(getString(R.string.In_preparation))
                        dto.url.downloadApk(lifecycleScope, dto.packageName,
                            process = {
                                val progressText = getString(R.string.download, it)
                                pbUpdate.setProgressText(progressText)
                                pbUpdate.setProgress(it.toFloat())
                            },
                            downloadError = {
                                llRoot.isClickable = true
                                pbUpdate.setTextColor(com.shudong.lib_res.R.color.red.colorValue())
                                pbUpdate.setProgressText(getString(R.string.Download_failed))
                                pbUpdate.reset()

                            },
                            downloadComplete = { _, destPath ->
                                startInstallApp(destPath, pbUpdate, llRoot) {
                                    dto.isInstalled = it
                                    val isAllInstalled =
                                        (mBind.rvApps.mutable as MutableList<UpdateAppsDTO>).all { it.isInstalled }
                                    isAllInstalled.yes {
                                        mBind.tvNextTime.isVisible = false
                                        mBind.tvCancle.isVisible = false
                                        mBind.tvOk.isVisible = true
                                        mBind.tvOk.post { mBind.tvOk.requestFocus() }

                                    }
                                }
                            }
                        )
                    }

                }

            }.models = arrayListOf()

        }

        val list = AppCache.updateInfo.jsonToTypeBean<MutableList<UpdateAppsDTO>>()

        mBind.rvApps.addModels(list)


        mBind.rvApps.apply {
            postDelayed({
                requestFocus()
                layoutManager?.findViewByPosition(0)?.requestFocus()
            }, 0)
        }

    }

    override fun initClick() {
        mBind.apply {
            tvNextTime.clickNoRepeat {
                // 点击暂不提示，一周后再提示
                AppCache.updateInteval = "week"
                AppCache.lastTipTime = System.currentTimeMillis()
                requireActivity().finish()
            }
            tvCancle.clickNoRepeat {
                // 点击取消，应用重启或者间隔超过24小时后提示
                AppCache.updateInteval = "day"
                AppCache.lastTipTime = System.currentTimeMillis()
                requireActivity().finish()
            }

            tvOk.clickNoRepeat {
                //点击完成
                requireActivity().finish()
                AppCache.lastTipTime = 0
            }
        }
    }

    private fun startInstallApp(
        destPath: String,
        pbUpdate: FlikerProgressBar,
        llRoot: LinearLayout,
        callback: (Boolean) -> Unit
    ) {
        lifecycleScope.launch {
            try {
                val result = destPath.silentInstallWithMutex(pbUpdate)
                if (result) {
                    pbUpdate.setTextColor(com.shudong.lib_res.R.color.black.colorValue())
                    pbUpdate.setProgressText(getString(R.string.Installation_Complete))
                } else {
                    pbUpdate.setTextColor(com.shudong.lib_res.R.color.red.colorValue())
                    pbUpdate.setProgressText(getString(R.string.Installation_failed))
                    llRoot.isClickable = true
                }
                callback(result)
            } catch (e: Exception) {
                pbUpdate.setTextColor(com.shudong.lib_res.R.color.red.colorValue())
                pbUpdate.setProgressText(getString(R.string.Installation_Errors))
                llRoot.isClickable = true
                callback(false)
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            UpdateAppsFragment().apply {
                arguments = Bundle()
            }
    }
}