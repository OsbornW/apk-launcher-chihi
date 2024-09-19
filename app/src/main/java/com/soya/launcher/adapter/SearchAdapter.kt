package com.soya.launcher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.Presenter
import com.soya.launcher.R
import com.soya.launcher.bean.Movice
import com.soya.launcher.utils.AndroidSystem.getImageFromAssetsFile
import com.soya.launcher.utils.GlideUtils

class SearchAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    private val callback: Callback?
) : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return Holder(inflater.inflate(R.layout.holder_search, parent, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val holder = viewHolder as Holder
        holder.bind(item as Movice)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val holder = viewHolder as Holder
        holder.unbind()
    }

    inner class Holder(view: View) : ViewHolder(view) {
        private val mIV: ImageView = view.findViewById(R.id.image)
        private val mTitleView: TextView = view.findViewById(R.id.title)

        fun bind(bean: Movice) {
            when (bean.picType) {
                Movice.PIC_ASSETS -> GlideUtils.bind(
                     mIV, getImageFromAssetsFile(
                        context, bean.picture
                    )
                )

                Movice.PIC_NETWORD -> GlideUtils.bind( mIV, bean.picture)
                else -> GlideUtils.bind( mIV, R.drawable.transparent)
            }
            mTitleView.text = bean.title
            view.setOnClickListener { callback?.onClick(bean) }
        }

        fun unbind() {
        }
    }

    interface Callback {
        fun onClick(bean: Movice?)
    }
}
