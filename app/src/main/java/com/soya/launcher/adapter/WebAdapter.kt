package com.soya.launcher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.leanback.widget.Presenter
import com.soya.launcher.R
import com.soya.launcher.bean.WebItem
import com.soya.launcher.utils.GlideUtils

class WebAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    private var callback: Callback?
) : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return Holder(inflater.inflate(R.layout.holder_web, parent, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val holder = viewHolder as Holder
        holder.bind(item as WebItem)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val holder = viewHolder as Holder
        holder.unbind()
    }

    inner class Holder(view: View) : ViewHolder(view) {
        private val mImageView: ImageView = view.findViewById(R.id.image)

        fun bind(bean: WebItem) {
            GlideUtils.bind( mImageView, bean.icon)
            view.setOnClickListener { if (callback != null) callback!!.onClick(bean) }
        }

        fun unbind() {}
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    fun interface Callback {
        fun onClick(bean: WebItem?)
    }
}
