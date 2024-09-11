package com.soya.launcher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.soya.launcher.R
import com.soya.launcher.bean.DateItem

class DateListAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    private val dataList: List<DateItem>,
    private val callback: Callback?
) : RecyclerView.Adapter<DateListAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(inflater.inflate(R.layout.holder_date_list, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        private val mTitleView: TextView = view.findViewById(R.id.title)
        private val mDescView: TextView = view.findViewById(R.id.desc)
        private val mSwitch: Switch = view.findViewById(R.id.switch_item)

        fun bind(bean: DateItem) {
            val index = dataList.indexOf(bean)
            mTitleView.text = bean.title
            mDescView.text = bean.description
            mSwitch.isChecked = bean.isSwitch
            mSwitch.visibility = if (bean.isUseSwitch) View.VISIBLE else View.GONE
            mDescView.visibility = if (bean.isUseSwitch) View.GONE else View.VISIBLE
            mTitleView.isEnabled = !dataList[0].isSwitch || (index != 1 && index != 2)

            itemView.setOnClickListener { callback?.onClick(bean) }
        }

        fun unbind() {}
    }

    interface Callback {
        fun onClick(bean: DateItem?)
    }
}
