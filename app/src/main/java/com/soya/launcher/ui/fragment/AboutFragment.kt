package com.soya.launcher.ui.fragment

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ItemBridgeAdapter
import androidx.leanback.widget.VerticalGridView
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.clickNoRepeat
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.BuildConfig
import com.soya.launcher.R
import com.soya.launcher.adapter.AboutAdapter
import com.soya.launcher.bean.AboutItem
import com.soya.launcher.config.Config
import com.soya.launcher.databinding.FragmentAboutBinding
import com.soya.launcher.enums.Atts
import com.soya.launcher.http.HttpRequest
import com.soya.launcher.http.response.VersionResponse
import com.soya.launcher.ui.dialog.ProgressDialog
import com.soya.launcher.ui.dialog.ToastDialog
import com.soya.launcher.utils.AndroidSystem
import com.soya.launcher.utils.PreferencesUtils
import retrofit2.Call

class AboutFragment : BaseWallPaperFragment<FragmentAboutBinding,BaseViewModel>(),
    View.OnClickListener {

    private var uiHandler: Handler? = null

    private var call: Call<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiHandler = Handler()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (call != null) call!!.cancel()
    }


    override fun initView() {
        mBind.layout.title.text = getString(R.string.about)

        mBind.upgrade.clickNoRepeat (2000){
            val dialog = ProgressDialog.newInstance()
            dialog.show(childFragmentManager, ProgressDialog.TAG)
            call =
                HttpRequest.checkVersion { call: Call<*>, status: Int, response: VersionResponse? ->
                    dialog.dismiss()
                    if (!isAdded || call.isCanceled || response == null || response.data == null) {
                        val toastDialog = ToastDialog.newInstance(
                            getString(R.string.already_latest_version),
                            ToastDialog.MODE_CONFIRM
                        )
                        toastDialog.show(childFragmentManager, ToastDialog.TAG)
                        return@checkVersion
                    }
                    val result = response.data
                    if (result.version > BuildConfig.VERSION_CODE && Config.CHANNEL == result.channel) {
                        PreferencesUtils.setProperty(Atts.UPGRADE_VERSION, result.version.toInt())
                        AndroidSystem.jumpUpgrade(requireContext(), result)
                    } else {
                        val toastDialog = ToastDialog.newInstance(
                            getString(R.string.already_latest_version),
                            ToastDialog.MODE_CONFIRM
                        )
                        toastDialog.show(childFragmentManager, ToastDialog.TAG)
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

        val arrayObjectAdapter =
            ArrayObjectAdapter(AboutAdapter(requireContext(), layoutInflater).setCallback (object :AboutAdapter.Callback{
                override fun onClick(bean: AboutItem?) {
                    when (bean?.type) {
                        2 -> AndroidSystem.restoreFactory(requireContext())
                    }
                }

            }))

        val itemBridgeAdapter = ItemBridgeAdapter(arrayObjectAdapter)
        mBind.content.adapter = itemBridgeAdapter
        mBind.content.setNumColumns(1)
        arrayObjectAdapter.addAll(0, list)
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
