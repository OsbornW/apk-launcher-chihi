package com.soya.launcher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.soya.launcher.R
import com.soya.launcher.bean.SimpleTimeZone
import java.util.TimeZone

class TimeZoneAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    private val dataList: MutableList<SimpleTimeZone>
) : RecyclerView.Adapter<TimeZoneAdapter.Holder>() {
    private var select: TimeZone? = null
    private var callback: Callback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(inflater.inflate(R.layout.holder_time_zone, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setSelect(select: TimeZone?) {
        this.select = select
        notifyDataSetChanged()
    }

    fun replace(list: List<SimpleTimeZone>?) {
        dataList.clear()
        dataList.addAll(list!!)
        notifyDataSetChanged()
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        private val mTitleView: TextView = view.findViewById(R.id.title)
        private val mDescView: TextView = view.findViewById(R.id.desc)
        private val mCheckView: ImageView = view.findViewById(R.id.check)

        fun bind(bean: SimpleTimeZone) {
            val isSelect = select != null && bean.zone.id == select!!.id
            if (isSelect) {
            }
            mTitleView.text = bean.name
            mDescView.text = bean.desc
            mCheckView.visibility = if (isSelect) View.VISIBLE else View.GONE
            mDescView.visibility = if (isSelect) View.GONE else View.VISIBLE

            itemView.setOnClickListener {
                select = bean.zone
                if (callback != null) callback!!.onClick(bean)
            }
        }
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    interface Callback {
        fun onClick(bean: SimpleTimeZone?)
    }
}
