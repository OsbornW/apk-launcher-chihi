package com.soya.launcher.adapter

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.soya.launcher.R
import com.soya.launcher.bean.WifiItem
import com.soya.launcher.utils.AndroidSystem

class WifiListAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    private val dataList: MutableList<WifiItem>,
    private val useNext: Boolean,
    private val callback: Callback?
) : RecyclerView.Adapter<WifiListAdapter.Holder>() {
    @JvmField
    var connectSSID: String? = null
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.itemAnimator!!.changeDuration = 0
        recyclerView.itemAnimator!!.removeDuration = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(inflater.inflate(R.layout.holder_wifi, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun getDataList(): List<WifiItem> {
        return dataList
    }

    fun replace(results: MutableList<WifiItem>) {
        dataList.clear()
        dataList.addAll(results)
        dataList.forEachIndexed { index, item ->
            val result = item.item
            val isConnect = result.SSID == connectSSID
            if(isConnect||item.isSave){
                dataList.remove(item)
            }
        }

        notifyDataSetChanged()
    }


    fun add(position: Int, list: MutableList<WifiItem>) {

        dataList.addAll(position, list)

        dataList.forEachIndexed { index, item ->
            val result = item.item
            val isConnect = result.SSID == connectSSID
            if(isConnect||item.isSave){
                dataList.remove(item)
            }
        }

        notifyItemRangeInserted(position, list.size)

    }

    fun remove(list: List<WifiItem>) {
        for (item in list) {
            val index = dataList.indexOf(item)
            if (index != -1) {
                dataList.removeAt(index)
                notifyItemRemoved(index)
            }
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mTitleView: TextView
        private val mLockView: ImageView
        private val mSignalView: ImageView
        private val mStatusView: TextView

        init {
            if (useNext) {
                itemView.nextFocusLeftId = R.id.next
                itemView.nextFocusRightId = R.id.next
            }
            mTitleView = itemView.findViewById(R.id.title)
            mLockView = itemView.findViewById(R.id.lock)
            mSignalView = itemView.findViewById(R.id.signal)
            mStatusView = itemView.findViewById(R.id.status)
        }

        fun bind(bean: WifiItem) {
            val result = bean.item
            val usePass = AndroidSystem.isUsePassWifi(result)
            val isConnect = result.SSID == connectSSID
            mStatusView.text = if (bean.isSave) context.getString(R.string.saved) else ""
            if (isConnect) mStatusView.text = context.getString(R.string.connected)
            mTitleView.text = result.SSID
            mLockView.visibility = if (usePass) View.VISIBLE else View.GONE
            when (WifiManager.calculateSignalLevel(bean.item.level, 5)) {
                1, 2 -> mSignalView.setImageResource(R.drawable.baseline_wifi_1_bar_100)
                3 -> mSignalView.setImageResource(R.drawable.baseline_wifi_2_bar_100)
                else -> mSignalView.setImageResource(R.drawable.baseline_wifi_100)
            }
            itemView.setOnClickListener { callback?.onClick(bean) }
        }
    }

    private fun cleanSSID(ssid: String): String {
        return ssid.replaceFirst("^\"".toRegex(), "").replaceFirst("\"$".toRegex(), "")
    }

    interface Callback {
        fun onClick(bean: WifiItem?)
    }
}
