package com.soya.launcher.ui.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.leanback.widget.VerticalGridView
import androidx.lifecycle.lifecycleScope
import com.soya.launcher.R
import com.soya.launcher.adapter.AppListAdapter
import com.soya.launcher.ui.dialog.AppDialog
import com.soya.launcher.utils.AndroidSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppsFragment : AbsFragment() {
    private var mContentGrid: VerticalGridView? = null
    private var mTitleView: TextView? = null

    private var receiver: InnerReceiver? = null

    private var mAppItemAdapter: AppListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            receiver = InnerReceiver()
            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_PACKAGE_ADDED)
            filter.addAction(Intent.ACTION_PACKAGE_REMOVED)
            filter.addAction(Intent.ACTION_PACKAGE_REPLACED)
            filter.addDataScheme("package")
            activity!!.registerReceiver(receiver, filter)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.unregisterReceiver(receiver)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_apps
    }

    override fun init(view: View, inflater: LayoutInflater) {
        super.init(view, inflater)
        mContentGrid = view.findViewById(R.id.content)
        mTitleView = view.findViewById(R.id.title)

        mTitleView?.text = getString(R.string.apps)
        mAppItemAdapter = AppListAdapter(
            requireContext(),
            layoutInflater,
            ArrayList(),
            R.layout.holder_app_2,
            newAppListCallback()
        )
    }

    override fun initBind(view: View, inflater: LayoutInflater) {
        super.initBind(view, inflater)
        mContentGrid!!.adapter = mAppItemAdapter
        fillApps()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestFocus(mContentGrid)
    }

    override fun getWallpaperView(): Int {
        return R.id.wallpaper
    }

    private fun fillApps() {
        lifecycleScope.launch {
            var list:List<ApplicationInfo>
            withContext(Dispatchers.IO){
                 list = AndroidSystem.getUserApps(activity)
            }
            withContext(Dispatchers.Main){
                setContent(list)
            }
        }
        //setContent(AndroidSystem.getUserApps(activity))
    }

    private fun setContent(list: List<ApplicationInfo>) {
        mContentGrid!!.setNumColumns(4)
        mAppItemAdapter!!.replace(list)
        requestFocus(mContentGrid)
    }

    private fun newAppListCallback(): AppListAdapter.Callback {
        return object : AppListAdapter.Callback {
            override fun onSelect(selected: Boolean) {
            }

            override fun onClick(bean: ApplicationInfo) {
                AndroidSystem.openPackageName(activity, bean.packageName)
            }

            override fun onMenuClick(bean: ApplicationInfo) {
                menu(bean)
            }
        }
    }

    private fun menu(bean: ApplicationInfo) {
        val dialog = AppDialog.newInstance(bean)
        dialog.setCallback {
            AndroidSystem.openPackageName(
                activity, bean.packageName
            )
        }
        dialog.show(childFragmentManager, AppDialog.TAG)
    }

    inner class InnerReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REMOVED, Intent.ACTION_PACKAGE_REPLACED -> fillApps()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fillApps()

    }

    companion object {
        fun newInstance(): AppsFragment {
            val args = Bundle()

            val fragment = AppsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
