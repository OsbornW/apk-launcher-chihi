package com.soya.launcher.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.soya.launcher.R
import com.soya.launcher.bean.Weather
import com.soya.launcher.manager.FilePathMangaer
import com.soya.launcher.utils.AndroidSystem.getImageForAssets

class WeatherAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    private val dataList: MutableList<Weather>
) : RecyclerView.Adapter<WeatherAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(inflater.inflate(R.layout.holder_weather, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun replace(list: List<Weather>?) {
        dataList.clear()
        dataList.addAll(list!!)
        notifyDataSetChanged()
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mDateView: TextView = itemView.findViewById(R.id.date)
        private val mIconView: ImageView = itemView.findViewById(R.id.icon)
        private val mRangeView: TextView = itemView.findViewById(R.id.range)

        fun bind(bean: Weather) {
            mDateView.text = bean.dateDes
            mIconView.setImageBitmap(
                getImageForAssets(
                    context,
                    FilePathMangaer.getWeatherIcon(bean.icon)
                )
            )
            if (!TextUtils.isEmpty(bean.datetime)) mRangeView.text =
                String.format("%.0f℉  %.0f℉", bean.tempmin, bean.tempmax)
        }
    }
}
