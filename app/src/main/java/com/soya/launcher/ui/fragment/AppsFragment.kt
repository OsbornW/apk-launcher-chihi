package com.soya.launcher.ui.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.HandlerCompat.postDelayed
import androidx.leanback.widget.VerticalGridView
import androidx.lifecycle.lifecycleScope
import com.drake.brv.utils.grid
import com.drake.brv.utils.models
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setup
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.no
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.config.Config
import com.soya.launcher.databinding.FragmentAppsBinding
import com.soya.launcher.ui.dialog.AppDialog
import com.soya.launcher.utils.AndroidSystem
import com.soya.launcher.utils.isSysApp
import com.soya.launcher.view.AppLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppsFragment : BaseWallPaperFragment<FragmentAppsBinding,BaseViewModel>() {

    private var receiver: InnerReceiver? = null


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


    override fun initView() {
        mBind.content.grid(4).setup {
            addType<ApplicationInfo>(R.layout.item_apps)
            onBind {

                val bean = _data as ApplicationInfo
                val mIV = findView<ImageView>(R.id.image)
                val mTitle = findView<TextView>(R.id.title)
                val mIVSmall = findView<ImageView>(R.id.image_small)
                val mAppLayout = findView<AppLayout>(R.id.fl_focus)

                val pm = context.packageManager
                val banner: Drawable? = null
                if (banner != null) {
                    mIV.setImageDrawable(banner)
                } else {
                    mIVSmall.setImageDrawable(bean.loadIcon(pm))
                }
                if (Config.COMPANY == 4 && bean.packageName == "com.mediatek.wwtv.tvcenter") {
                    mTitle.text = "HDMI"
                } else {
                    mTitle.text = bean.loadLabel(pm)
                }
                mIV.visibility = if (banner == null) View.GONE else View.VISIBLE
                mIVSmall.visibility = if (banner == null) View.VISIBLE else View.GONE

                mAppLayout.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                    itemView.isSelected = hasFocus
                    val animation = AnimationUtils.loadAnimation(
                        context, if (hasFocus) R.anim.zoom_in_middle else R.anim.zoom_out_middle
                    )
                    itemView.startAnimation(animation)
                    animation.fillAfter = true
                }

                mAppLayout.clickNoRepeat {
                    AndroidSystem.openPackageName(requireContext(), bean.packageName)
                }

                mAppLayout.setListener(AppLayout.EventListener { keyCode, event ->
                    if ( event?.keyCode == KeyEvent.KEYCODE_MENU) {
                        isSysApp(bean.packageName).no {
                            //点击了菜单
                            menu(bean)
                        }
                        return@EventListener false
                    }
                    true
                })


            }
        }.models = arrayListOf()

        mBind.layout.title.text = getString(R.string.apps)
    }



    private fun fillApps() {
        lifecycleScope.launch {
            var list:List<ApplicationInfo>
            withContext(Dispatchers.IO){
                 list = AndroidSystem.getUserApps(requireContext())
            }
            withContext(Dispatchers.Main){
                setContent(list)
            }
        }
    }

    private fun setContent(list: List<ApplicationInfo>) {
       // val filteredList = list.toMutableList().let { product.filterRepeatApps(it) } ?:list
        if(list.size!=mBind.content.mutable.size){
            mBind.content.models = list
            mBind.content.apply {
                postDelayed({
                    requestFocus()
                    layoutManager?.findViewByPosition(0)?.requestFocus()
                },800)
            }
        }

        //requestFocus(mContentGrid)
    }


    private fun menu(bean: ApplicationInfo) {
        val dialog = AppDialog.newInstance(bean)
        dialog.setCallback (object :AppDialog.Callback{
            override fun onOpen() {
                AndroidSystem.openPackageName(
                    requireContext(), bean.packageName
                )
            }

        })
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
