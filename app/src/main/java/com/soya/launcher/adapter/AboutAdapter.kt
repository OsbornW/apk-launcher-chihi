package com.soya.launcher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.Presenter
import com.soya.launcher.R
import com.soya.launcher.bean.AboutItem
import com.soya.launcher.view.MyFrameLayout

class AboutAdapter(private val context: Context, private val inflater: LayoutInflater) :
    Presenter() {
    private var callback: Callback? = null

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return Holder(inflater.inflate(R.layout.holder_about, parent, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val holder = viewHolder as Holder
        holder.bind(item as AboutItem)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val holder = viewHolder as Holder
        holder.unbind()
    }

    inner class Holder(view: View) : ViewHolder(view) {
        private val mRootView = view as MyFrameLayout
        private val mIconView: ImageView = view.findViewById(R.id.icon)
        private val mTitleView: TextView = view.findViewById(R.id.title)
        private val mDescView: TextView = view.findViewById(R.id.desc)

        fun bind(bean: AboutItem) {
            mIconView.setImageResource(bean.icon)
            mTitleView.text = bean.title
            mDescView.text = bean.description

            view.setOnClickListener { if (callback != null) callback!!.onClick(bean) }

            mRootView.setCallback { }
        }

        fun unbind() {
        }
    }

    fun setCallback(callback: Callback?): AboutAdapter {
        this.callback = callback
        return this
    }

    interface Callback {
        fun onClick(bean: AboutItem?)
    }
}
