package com.soya.launcher.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.soya.launcher.R
import com.soya.launcher.bean.AppItem
import com.soya.launcher.utils.GlideUtils
import com.soya.launcher.view.MyFrameLayout


class AppItemAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    private val dataList: MutableList<AppItem>,
    private val layoutId: Int,
    private var callback: Callback?
) : RecyclerView.Adapter<AppItemAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(inflater.inflate(layoutId, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun replace(list: List<AppItem>?) {
        dataList.clear()
        dataList.addAll(list!!)
        notifyDataSetChanged()
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        private val mIV: ImageView
        private val mIVSmall: ImageView
        private val mTitle: TextView
        private val mRootView: MyFrameLayout

        init {
            view.nextFocusUpId = R.id.header
            mIV = view.findViewById(R.id.image)
            mTitle = view.findViewById(R.id.title)
            mIVSmall = view.findViewById(R.id.image_small)
            mRootView = view as MyFrameLayout
        }

        fun bind(bean: AppItem) {
            val root = itemView.rootView
            mIV.visibility = View.GONE
            mIVSmall.visibility = View.VISIBLE
            GlideUtils.bind(
                mIVSmall,
                if (TextUtils.isEmpty(bean.localIcon)) bean.appIcon else bean.localIcon
            )
            mTitle.text = bean.appName

            itemView.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                mRootView.isSelected = hasFocus
                val animation = AnimationUtils.loadAnimation(
                    context, if (hasFocus) R.anim.zoom_in_middle else R.anim.zoom_out_middle
                )
                root.startAnimation(animation)
                animation.fillAfter = true
            }

            mRootView.setCallback { selected -> if (callback != null) callback!!.onSelect(selected) }

            itemView.setOnClickListener { if (callback != null) callback!!.onClick(bean) }
        }

        fun unbind() {
        }
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    interface Callback {
        fun onSelect(selected: Boolean)
        fun onClick(bean: AppItem?)
    }
}
