package com.soya.launcher.adapter

import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.yes
import com.soya.launcher.R
import com.soya.launcher.config.Config
import com.soya.launcher.utils.isSysApp
import com.soya.launcher.view.AppLayout
import com.soya.launcher.view.MyFrameLayout

class AppListAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    private val dataList: MutableList<ApplicationInfo>,
    private val layoutId: Int,
    private var callback: Callback?
) : RecyclerView.Adapter<AppListAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(inflater.inflate(layoutId, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun getDataList():MutableList<ApplicationInfo>{
        return dataList
    }
    fun replace(list: List<ApplicationInfo>?) {
        dataList.clear()
        dataList.addAll(list!!)
        notifyDataSetChanged()
    }


    fun refresh(list: MutableList<ApplicationInfo>) {
        val oldPackageNames = dataList.map { it.packageName }.toSet()
        val newPackageNames = list.map { it.packageName }.toSet()

        // 找出新增的 packageName 和缺失的 packageName
        val missingInNew = newPackageNames - oldPackageNames
        val extraInOld = oldPackageNames - newPackageNames

        Log.e("zengyue", "Old package names: $oldPackageNames")
        Log.e("zengyue", "New package names: $newPackageNames")
        Log.e("zengyue", "Missing in new: $missingInNew")
        Log.e("zengyue", "Extra in old: $extraInOld")

        when {
            // 处理新增的情况
            missingInNew.isNotEmpty()-> {
                Log.e("zengyue", "新增了包哦1")
                missingInNew.forEach { missingPackageName ->
                    val newItem = list.find { it.packageName == missingPackageName }
                    if (newItem != null) {
                        Log.e("zengyue", "新增了包哦2: $missingPackageName")
                        dataList.add(newItem)
                        notifyItemInserted(dataList.indexOf(newItem))
                    }
                }
            }
            // 处理移除的情况
            extraInOld.isNotEmpty()-> {
                Log.e("zengyue", "移除了包哦1")
                val positionsToRemove = mutableListOf<Int>()
                extraInOld.forEach { extraPackageName ->
                    dataList.indexOfFirst { it.packageName == extraPackageName }.let { position ->
                        if (position != -1) {
                            Log.e("zengyue", "移除了包哦2: $extraPackageName")
                            positionsToRemove.add(position)
                        }
                    }
                }
                // 从最后一个位置开始移除，避免索引问题
                positionsToRemove.sortedDescending().forEach { position ->
                    dataList.removeAt(position)
                    notifyItemRemoved(position)
                }

                Log.e("zengyue", "Updated package names after removal: ${dataList.map { it.packageName }}")
            }
            else -> {
                Log.e("zengyue", "没有新增移除")
            }
        }
    }




    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        private val mIV: ImageView
        private val mIVSmall: ImageView
        private val mTitle: TextView
        private val mRootView: MyFrameLayout
        private val mAppLayout: AppLayout

        init {
            view.nextFocusUpId = R.id.header
            mIV = view.findViewById(R.id.image)
            mTitle = view.findViewById(R.id.title)
            mIVSmall = view.findViewById(R.id.image_small)
            mAppLayout = view.findViewById(R.id.root)
            mRootView = view as MyFrameLayout
        }

        fun bind(bean: ApplicationInfo) {
            val root = itemView.rootView
            val pm = context.packageManager
            //Drawable banner = bean.activityInfo.loadBanner(pm);
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
            mRootView.setCallback { selected -> if (callback != null) callback!!.onSelect(selected) }
            itemView.setOnClickListener { if (callback != null) callback!!.onClick(bean) }
            itemView.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                mRootView.setSelected(hasFocus)
                val animation = AnimationUtils.loadAnimation(
                    context, if (hasFocus) R.anim.zoom_in_middle else R.anim.zoom_out_middle
                )
                root.startAnimation(animation)
                animation.fillAfter = true
            }
            mAppLayout.setListener(AppLayout.EventListener { keyCode, event ->
                if (callback != null && event.keyCode == KeyEvent.KEYCODE_MENU) {
                    isSysApp(bean.packageName).no {
                        callback!!.onMenuClick(bean)
                    }
                    return@EventListener false
                }
                true
            })
        }

        fun unbind() {}
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    interface Callback {
        fun onSelect(selected: Boolean)
        fun onClick(bean: ApplicationInfo)
        fun onMenuClick(bean: ApplicationInfo)
    }
}
