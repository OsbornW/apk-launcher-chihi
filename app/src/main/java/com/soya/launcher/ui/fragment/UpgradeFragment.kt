package com.soya.launcher.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import com.arialyy.annotations.Download
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.task.DownloadTask
import com.shudong.lib_base.base.BaseViewModel
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.bean.Version
import com.soya.launcher.databinding.FragmentUpgradeBinding
import com.soya.launcher.enums.Atts
import com.soya.launcher.handler.PermissionHandler
import com.soya.launcher.manager.FilePathMangaer
import com.soya.launcher.ui.activity.WifiListActivity
import com.soya.launcher.ui.dialog.ProgressDialog
import com.soya.launcher.utils.AppUtil
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UpgradeFragment : BaseWallPaperFragment<FragmentUpgradeBinding,BaseViewModel>(),
    ActivityResultCallback<ActivityResult>,
    View.OnClickListener {
    private val exec: ExecutorService = Executors.newCachedThreadPool()


    private var version: Version? = null
    private var uiHandler: Handler? = null
    private var resultLauncher: ActivityResultLauncher<Any>? = null
    private var fileName: String? = null
    private var savePath: String? = null
    private var mDialog: ProgressDialog? = null
    private var taskId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        version = activity!!.intent.getSerializableExtra(Atts.BEAN) as Version?
        uiHandler = Handler()
        fileName = String.format("%s.apk", System.currentTimeMillis())
        savePath = FilePathMangaer.getAppUpgradePath(activity) + "/" + fileName
        mDialog = ProgressDialog.newInstance()
        Aria.download(this).register()
        initLauncher()
    }

    override fun onDestroy() {
        super.onDestroy()
        exec.shutdownNow()
        if (taskId != -1L) Aria.download(this).load(taskId).stop()
        Aria.download(this).unRegister()
    }


    override fun initView() {
        mBind.divOper.visibility = View.GONE

        mBind.close.setOnClickListener(this)
        mBind.retry.setOnClickListener(this)
        mBind.install.setOnClickListener(this)
        mBind.wifi.setOnClickListener(this)
    }


    override fun initdata() {
        mBind.layout.title.text = getString(R.string.upgrade)
        mBind.tip.text = version!!.desc
        syncProgress(0f)
        download()
    }



    private fun initLauncher() {
        resultLauncher = PermissionHandler.createPermissionsWithIntent(this, this)
    }

    private fun download() {
        syncProgress(0f)
        showButtom(false, true, true, true)
        val url = version!!.downLink
        taskId = Aria.download(this).load(url).setFilePath(savePath).create()
    }

    private fun check() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!activity!!.packageManager.canRequestPackageInstalls()) {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                resultLauncher!!.launch(intent)
            } else {
                install()
            }
        } else {
            install()
        }
    }

    private fun install() {
        mDialog!!.show(childFragmentManager, ProgressDialog.TAG)
        exec.execute {
            try {
                savePath?.let { AppUtil.adbInstallApk(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun syncProgress(progress: Float) {
        uiHandler!!.post {
            mBind.mes.text = getString(R.string.download_progress, progress)
            mBind.progressBar.progress = progress.toInt()
        }
    }

    private fun syncMes(mes: String) {
        uiHandler!!.post { mBind.mes.text = mes }
    }

    private fun showButtom(div: Boolean, close: Boolean, retry: Boolean, install: Boolean) {
        uiHandler!!.post {
            mBind.divOper.visibility = if (div) View.VISIBLE else View.GONE
            mBind.close.visibility = if (close) View.VISIBLE else View.GONE
            mBind.install.visibility = if (install) View.VISIBLE else View.GONE
            mBind.retry.visibility = if (retry) View.VISIBLE else View.GONE
            mBind.close.visibility = View.GONE
            mBind.divOper.requestFocus()
        }
    }

    override fun onActivityResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            install()
        }
    }

    override fun onClick(v: View) {
        if (v == mBind.close) {
            activity!!.finish()
        } else if (v == mBind.retry) {
            download()
        } else if (v == mBind.install) {
            check()
        } else if (v == mBind.wifi) {
            startActivity(Intent(activity, WifiListActivity::class.java))
        }
    }

    @Download.onTaskRunning
    protected fun running(task: DownloadTask) {
        if (!isAdded) return
        val p = task.percent
        syncProgress(p.toFloat())
    }

    @Download.onTaskComplete
    protected fun taskComplete(task: DownloadTask?) {
        syncProgress(100f)
        syncMes(getString(R.string.upgrade_now))
        showButtom(true, true, false, true)
    }

    @Download.onTaskFail
    protected fun taskFail(task: DownloadTask?) {
        if (!isAdded) return
        syncMes(getString(R.string.download_fail))
        showButtom(true, true, true, false)
    }

    companion object {
        fun newInstance(): UpgradeFragment {
            val args = Bundle()

            val fragment = UpgradeFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
