package com.soya.launcher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.soya.launcher.R
import com.soya.launcher.bean.Ads

class BannerAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    private val dataList: List<Ads>
) : RecyclerView.Adapter<BannerAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(inflater.inflate(R.layout.holder_banner, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
