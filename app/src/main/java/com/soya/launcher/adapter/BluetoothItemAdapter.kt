package com.soya.launcher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.open.system.SystemUtils
import com.soya.launcher.R
import com.soya.launcher.bean.BluetoothItem

class BluetoothItemAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    val dataList: MutableList<BluetoothItem>,
    private val callback: Callback?
) : RecyclerView.Adapter<BluetoothItemAdapter.Holder>() {
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.itemAnimator!!.changeDuration = 0
        recyclerView.itemAnimator!!.removeDuration = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(inflater.inflate(R.layout.holder_bluetooth, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun getBluetoothList(): List<BluetoothItem> {
        return dataList
    }

    fun replace(results: List<BluetoothItem>?) {
        dataList.clear()
        dataList.addAll(results!!)
        notifyDataSetChanged()
    }

    fun add(position: Int, list: List<BluetoothItem>) {
        dataList.addAll(position, list)
        notifyItemRangeInserted(position, list.size)
    }

    fun add(list: List<BluetoothItem>) {
        val size = dataList.size
        dataList.addAll(list)
        notifyItemRangeInserted(size, list.size)
    }

    fun remove(list: List<BluetoothItem>) {
        for (item in list) {
            val index = dataList.indexOf(item)
            if (index != -1) {
                dataList.removeAt(index)
                notifyItemRemoved(index)
            }
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mTitleView: TextView = itemView.findViewById(R.id.title)
        private val mDescView: TextView = itemView.findViewById(R.id.desc)
        private val mCheckView: ImageView = itemView.findViewById(R.id.check)

        fun bind(bean: BluetoothItem) {
            val device = bean.device
            mTitleView.text = device.name
            mDescView.visibility = View.GONE
            mCheckView.visibility =
                if (SystemUtils.isConnected(bean.device)) View.VISIBLE else View.GONE

            itemView.setOnClickListener { callback?.onClick(bean) }
        }
    }

    interface Callback {
        fun onClick(bean: BluetoothItem?)
    }
}
