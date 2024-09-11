package com.soya.launcher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.soya.launcher.R
import com.soya.launcher.bean.Ads
import com.soya.launcher.utils.GlideUtils

class AdsAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    private val dataList: List<Ads>,
    private val layoutMap: Map<Int, Int>
) : RecyclerView.Adapter<AdsAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(inflater.inflate(layoutMap[viewType]!!, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataList[position].spanSize == 2) TYPE_MAX else TYPE_MIN
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mImagView: ImageView = itemView.findViewById(R.id.image)

        fun bind(bean: Ads) {
            GlideUtils.bind(context, mImagView, bean.picture)
        }
    }

    companion object {
        const val TYPE_MIN: Int = 0
        const val TYPE_MAX: Int = 1
    }
}
