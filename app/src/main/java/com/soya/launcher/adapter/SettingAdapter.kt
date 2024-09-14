package com.soya.launcher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.Presenter
import com.shudong.lib_base.ext.dimenValue
import com.shudong.lib_base.ext.widthAndHeight
import com.soya.launcher.R
import com.soya.launcher.bean.Projector
import com.soya.launcher.bean.SettingItem
import com.soya.launcher.view.MyFrameLayout

class SettingAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    private var callback: Callback?,
    private val layoutId: Int
) : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return Holder(inflater.inflate(layoutId, parent, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val holder = viewHolder as Holder
        holder.bind(item as SettingItem)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val holder = viewHolder as Holder
        holder.unbind()
    }

    inner class Holder(view: View) : ViewHolder(view) {
        private val mRootView = view as MyFrameLayout
        private val mIV: ImageView = view.findViewById(R.id.image)
        private val mTitleView: TextView = view.findViewById(R.id.title)

        fun bind(bean: SettingItem) {
            when (bean.type) {
                Projector.TYPE_AUTO_CALIBRATION, Projector.TYPE_MANUAL_CALIBRATION -> {
                    mIV.widthAndHeight(
                        com.shudong.lib_dimen.R.dimen.qb_px_20.dimenValue(),
                        com.shudong.lib_dimen.R.dimen.qb_px_20.dimenValue()
                    )

                }

                else -> {
                    mIV.widthAndHeight(
                        com.shudong.lib_dimen.R.dimen.qb_px_50.dimenValue(),
                        com.shudong.lib_dimen.R.dimen.qb_px_50.dimenValue()
                    )
                }
            }
            mIV.setImageResource(bean.ico)
            mTitleView.text = bean.name

            mRootView.setCallback { selected ->
                if (callback != null) callback!!.onSelect(
                    selected,
                    bean
                )
            }

            view.setOnClickListener { v: View? ->
                if (callback != null) callback!!.onClick(bean)
            }
        }

        fun unbind() {
        }
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    interface Callback {
        fun onSelect(selected: Boolean, bean: SettingItem?)
        fun onClick(bean: SettingItem?)
    }
}
