package com.soya.launcher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.soya.launcher.R

class DateSelectAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    private val dataList: MutableList<Int>,
    private val callback: Callback?
) : RecyclerView.Adapter<DateSelectAdapter.Holder>() {
    private var select: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(inflater.inflate(R.layout.holder_date_select, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun getSelect(): Int? {
        return select
    }

    fun setSelect(select: Int?) {
        this.select = select
        notifyItemRangeChanged(0, dataList.size)
    }

    fun getDataList(): List<Int> {
        return dataList
    }

    fun replace(list: List<Int>?) {
        dataList.clear()
        dataList.addAll(list!!)
        notifyDataSetChanged()
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        private val mTitleView = view as TextView

        fun bind(bean: Int) {
            mTitleView.isSelected = bean == select
            mTitleView.text = bean.toString()

            mTitleView.setOnClickListener {
                select = bean
                notifyItemRangeChanged(0, dataList.size)
                callback?.onClick(bean)
            }
        }
    }

    interface Callback {
        fun onClick(bean: Int?)
    }
}
