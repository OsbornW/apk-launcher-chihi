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
import com.soya.launcher.h27002.getDrawableByName
import com.soya.launcher.utils.GlideUtils
import com.soya.launcher.view.MyFrameLayout

class StoreAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    private val dataList: MutableList<AppItem>,
    private val callback: Callback?
) : RecyclerView.Adapter<StoreAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(inflater.inflate(R.layout.holder_app_store, parent, false))
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
        private val mRootView = view as MyFrameLayout
        private val mIV: ImageView = view.findViewById(R.id.image)
        private val mTitleView: TextView = view.findViewById(R.id.title)
        private val mMesView: TextView = view.findViewById(R.id.mes)

        init {
            view.nextFocusUpId = R.id.header
        }

        fun bind(bean: AppItem) {
            val root = itemView.rootView
            if (TextUtils.isEmpty(bean.localIcon)) {
                GlideUtils.bind(context, mIV, bean.appIcon)
            } else {
                mIV.setImageDrawable(context.getDrawableByName(bean.localIcon))
            }
            mTitleView.text = bean.appName
            if (!TextUtils.isEmpty(bean.appSize)) mMesView.text =
                String.format("%.01f★ | %s", bean.score, bean.appSize)
            else mMesView.text = ""

            mRootView.setCallback { selected -> callback?.onSelect(selected, bean) }

            itemView.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                mRootView.isSelected = hasFocus
                val animation = AnimationUtils.loadAnimation(
                    context, if (hasFocus) R.anim.zoom_in_middle else R.anim.zoom_out_middle
                )
                root.startAnimation(animation)
                animation.fillAfter = true
            }

            itemView.setOnClickListener { callback?.onClick(bean) }
        }

        fun unbind() {
        }
    }

    interface Callback {
        fun onSelect(selected: Boolean, bean: AppItem?)
        fun onClick(bean: AppItem?)
    }
}
