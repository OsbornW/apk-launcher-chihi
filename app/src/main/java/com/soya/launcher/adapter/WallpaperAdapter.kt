package com.soya.launcher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.leanback.widget.Presenter
import com.soya.launcher.R
import com.soya.launcher.bean.Wallpaper
import com.soya.launcher.manager.PreferencesManager
import com.soya.launcher.view.MyFrameLayout

class WallpaperAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    private val callback: Callback?
) : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return Holder(inflater.inflate(R.layout.holder_wallpaper, parent, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val holder = viewHolder as Holder
        holder.bind(item as Wallpaper)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val holder = viewHolder as Holder
        holder.unbind()
    }

    inner class Holder(view: View) : ViewHolder(view) {
        private val mRootView = view as MyFrameLayout
        private val mIV: ImageView = view.findViewById(R.id.image)
        private val mCheckView: View = view.findViewById(R.id.check)

        fun bind(bean: Wallpaper) {
            mRootView.setCallback { selected -> callback?.onSelect(selected, bean) }

            mRootView.setOnClickListener { callback?.onClick(bean) }
            mIV.setImageResource(bean.picture)
            mCheckView.visibility =
                if (bean.id == PreferencesManager.getWallpaper()) View.VISIBLE else View.GONE
        }

        fun unbind() {}
    }

    interface Callback {
        fun onSelect(select: Boolean, bean: Wallpaper?)
        fun onClick(bean: Wallpaper?)
    }
}
