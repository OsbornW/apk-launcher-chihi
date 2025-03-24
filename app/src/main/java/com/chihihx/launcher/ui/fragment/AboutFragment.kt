package com.chihihx.launcher.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.drake.brv.utils.addModels
import com.drake.brv.utils.setup
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.jsonToString
import com.shudong.lib_base.ext.net.lifecycle
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.startKtxActivity
import com.shudong.lib_base.ext.yes
import com.chihihx.launcher.BaseWallPaperFragment
import com.chihihx.launcher.BuildConfig
import com.chihihx.launcher.R
import com.chihihx.launcher.bean.AboutItem
import com.chihihx.launcher.cache.AppCache
import com.chihihx.launcher.config.Config
import com.chihihx.launcher.databinding.FragmentAboutBinding
import com.chihihx.launcher.ext.getMemoryInfo
import com.chihihx.launcher.net.viewmodel.HomeViewModel
import com.chihihx.launcher.product.base.product
import com.chihihx.launcher.ui.activity.UpdateLauncherActivity
import com.chihihx.launcher.ui.dialog.ProgressDialog
import com.chihihx.launcher.ui.dialog.ToastDialog
import com.chihihx.launcher.utils.AndroidSystem
import com.shudong.lib_base.BaseWebActivity
import com.shudong.lib_base.BaseWebActivity.Companion.INTENT_WEB_URL
import com.shudong.lib_base.ext.stringValue
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AboutFragment : BaseWallPaperFragment<FragmentAboutBinding,HomeViewModel>(),
    View.OnClickListener {

    private var updateJob:Job?=null
    override fun initView() {
        mBind.layout.title.text = getString(R.string.about)

        mBind.upgrade.clickNoRepeat (2000){
            val dialog = ProgressDialog.newInstance()
            dialog.show(childFragmentManager, ProgressDialog.TAG)


            updateJob?.cancel()
            updateJob = lifecycleScope.launch {
                mViewModel.reqLauncherVersionInfo()
                    .lifecycle(this@AboutFragment, {
                        dialog.dismiss()
                    }, isShowError = false) {
                        dialog.dismiss()
                        val dto = this
                        dto.downLink.isNullOrEmpty().no {
                            AppCache.updateInfoForLauncher = dto.jsonToString()

                                //非强制更新
                                startKtxActivity<UpdateLauncherActivity>()
                        }.otherwise {
                            val toastDialog = ToastDialog.newInstance(
                                getString(R.string.already_latest_version),
                                ToastDialog.MODE_CONFIRM
                            )
                            toastDialog.show(childFragmentManager, ToastDialog.TAG)
                        }
                    }

            }
        }
    }


    override fun initdata() {
        setContent()
        mBind.content.apply { post { requestFocus() } }

    }



    private fun setContent() {
        val list: MutableList<AboutItem?> = ArrayList()
        //list.add(new AboutItem(0, R.drawable.baseline_storage_100, getString(R.string.storage), getString(R.string.storage_total_mask, AndroidSystem.getTotalInternalMemorySize() / 1024000000.0F)));
        list.add(
            AboutItem(
                0,
                R.drawable.baseline_tv_100,
                getString(R.string.android_tv_os_version),
                Build.VERSION.RELEASE
            )
        )
        list.add(
            AboutItem(
                0,
                R.drawable.baseline_translate_100,
                getString(R.string.language),
                AndroidSystem.getSystemLanguage(
                    requireContext()
                )
            )
        )
        list.add(
            AboutItem(
                0,
                R.drawable.baseline_apps_100,
                getString(R.string.apps),
                AndroidSystem.getUserApps(
                    requireContext()
                ).size.toString()
            )
        )
        list.add(
            AboutItem(
                0,
                R.drawable.baseline_workspaces_100,
                getString(R.string.software_version),
                BuildConfig.VERSION_NAME
            )
        )

        list.add(
            AboutItem(
                1,
                R.drawable.baseline_workspaces_100,
                R.string.privacy_policy.stringValue(),
                ""
            )
        )


        product.isShowMemoryInfo().yes {
            list.add(
                AboutItem(
                    0,
                    R.drawable.icon_memory,
                    getString(R.string.memory),
                    getMemoryInfo()
                )
            )
        }


        if (Config.COMPANY == 0) list.add(
            AboutItem(
                2,
                R.drawable.baseline_settings_backup_restore_100,
                getString(R.string.factory_reset),
                Build.MODEL
            )
        )
        else list.add(
            AboutItem(
                0,
                R.drawable.baseline_token_100,
                getString(R.string.device_id),
                AndroidSystem.getDeviceId(
                    requireContext()
                )
            )
        )

        mBind.content.setup {
            addType<AboutItem>(R.layout.holder_about)
            onBind {
                val dto = getModel<AboutItem>()
                itemView.clickNoRepeat {
                    when (dto.type) {
                        1->{
                            val url = "https://agree.xiaohongshu.com/h5/terms/ZXXY20220509001/-1"
                            //val url = "https://t.cp31.ott.cibntv.net/yep/page/s/vk7h5cfgnp"
                            startKtxActivity<BaseWebActivity>(
                                values = arrayOf(Pair(INTENT_WEB_URL, url))
                            )
                        }
                        2 -> AndroidSystem.restoreFactory(requireContext())
                    }
                }
            }
        }

        mBind.content.setNumColumns(1)
        mBind.content.addModels(list)
        mBind.content.requestFocus()
        mBind.content.setColumnWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onClick(v: View) {
        if (v == mBind.upgrade) {

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): AboutFragment {
            val args = Bundle()

            val fragment = AboutFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
