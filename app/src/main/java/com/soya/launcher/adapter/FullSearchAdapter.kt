package com.soya.launcher.adapter

import android.content.Context
import android.content.pm.ApplicationInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.FocusHighlight
import androidx.leanback.widget.FocusHighlightHelper
import androidx.leanback.widget.HorizontalGridView
import androidx.leanback.widget.ItemBridgeAdapter
import androidx.recyclerview.widget.RecyclerView
import com.soya.launcher.R
import com.soya.launcher.bean.AppItem
import com.soya.launcher.bean.DivSearch
import com.soya.launcher.bean.WebItem

class FullSearchAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    private val dataList: MutableList<DivSearch<*>?>,
    private var callback: Callback?
) : RecyclerView.Adapter<FullSearchAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(inflater.inflate(R.layout.holder_full_search, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        dataList[position]?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun sync(bean: DivSearch<*>) {
        val index = dataList.indexOf(bean)
        if (index != -1) notifyItemChanged(index)
    }

    fun replace(list: MutableList<DivSearch<*>?>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun add(index: Int, bean: DivSearch<*>) {
        dataList.add(index, bean)
        notifyDataSetChanged()
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        private val mContentGrid: HorizontalGridView = view.findViewById(R.id.content)
        private val mTitleView: TextView = view.findViewById(R.id.title)
        private val mNoneView: View = view.findViewById(R.id.mask)
        private val mProgressView: View = view.findViewById(R.id.progressBar)

        fun bind(bean: DivSearch<*>) {
            mTitleView.text = bean.title
            when (bean.type) {
                0 -> {
                    mContentGrid.visibility = View.VISIBLE
                    mNoneView.visibility = View.GONE
                    mProgressView.visibility = View.GONE
                    setAppContent(bean)
                }

                1 -> if (bean.state == 0) {
                    mContentGrid.visibility = View.GONE
                    mProgressView.visibility = View.VISIBLE
                    mNoneView.visibility = View.GONE
                } else if (bean.state == 1) {
                    mContentGrid.visibility = View.GONE
                    mProgressView.visibility = View.GONE
                    mNoneView.visibility = View.VISIBLE
                } else {
                    mContentGrid.visibility = View.VISIBLE
                    mProgressView.visibility = View.GONE
                    mNoneView.visibility = View.GONE
                    setAppStore(bean)
                }

                2 -> {
                    mContentGrid.visibility = View.VISIBLE
                    mNoneView.visibility = View.GONE
                    mProgressView.visibility = View.GONE
                    web(bean)
                }
            }
        }

        private fun setAppContent(bean: DivSearch<*>) {
            val adapter = AppListAdapter(
                context,
                inflater,
                (bean.list) as MutableList<ApplicationInfo>,
                R.layout.item_search_apps,
                object : AppListAdapter.Callback {
                    override fun onSelect(selected: Boolean) {
                    }

                    override fun onClick(child: ApplicationInfo) {
                        if (callback != null) callback!!.onClick(bean.type, child)
                    }

                    override fun onMenuClick(bean: ApplicationInfo) {
                    }
                })
            mContentGrid.adapter = adapter
            mContentGrid.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        private fun setAppStore(bean: DivSearch<*>) {
            val adapter = AppItemAdapter(
                context,
                inflater,
                (bean.list) as MutableList<AppItem>,
                R.layout.item_search_apps,
                object : AppItemAdapter.Callback {
                    override fun onSelect(selected: Boolean) {
                    }

                    override fun onClick(child: AppItem?) {
                        if (callback != null) callback!!.onClick(bean.type, child)
                    }
                })
            mContentGrid.adapter = adapter
            mContentGrid.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        private fun web(bean: DivSearch<*>) {
            val arrayObjectAdapter = ArrayObjectAdapter(
                WebAdapter(
                    context,
                    inflater,object :WebAdapter.Callback{
                        override fun onClick(child: WebItem?) {
                            if (callback != null) callback!!.onClick(bean.type, child)
                        }

                    }
                ) )
            val itemBridgeAdapter = ItemBridgeAdapter(arrayObjectAdapter)
            FocusHighlightHelper.setupBrowseItemFocusHighlight(
                itemBridgeAdapter,
                FocusHighlight.ZOOM_FACTOR_MEDIUM,
                false
            )
            mContentGrid.adapter = itemBridgeAdapter
            arrayObjectAdapter.addAll(0, bean.list)
            mContentGrid.setRowHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    interface Callback {
        fun onClick(type: Int, bean: Any?)
    }
}
