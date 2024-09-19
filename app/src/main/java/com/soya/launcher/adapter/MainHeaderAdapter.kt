package com.soya.launcher.adapter

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.shudong.lib_base.ext.dimenValue
import com.shudong.lib_base.ext.margin
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import com.soya.launcher.R
import com.soya.launcher.bean.HomeDataList
import com.soya.launcher.bean.TypeItem
import com.soya.launcher.cache.AppCache
import com.soya.launcher.callback.HeaderCallback
import com.soya.launcher.ext.loadImageWithGlide
import com.soya.launcher.h27002.getDrawableByName
import com.soya.launcher.utils.FileUtils
import com.soya.launcher.utils.GlideUtils
import com.soya.launcher.view.MyCardView
import java.io.File

class MainHeaderAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    private val items: MutableList<TypeItem>,
    private val callback: HeaderCallback?
) : RecyclerView.Adapter<MainHeaderAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(inflater.inflate(R.layout.item_home_header, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun replace(list: List<TypeItem>?) {
        items.clear()
        items.addAll(list!!)
        notifyDataSetChanged()
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        private val mIV: ImageView = view.findViewById(R.id.image)
        private val mTitleView: TextView = view.findViewById(R.id.title)
        private val mCardView: MyCardView = view.findViewById(R.id.root)

        fun bind(item: TypeItem, position: Int) {
            val root = itemView.rootView
            mTitleView.setBackgroundResource(R.drawable.light_item)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mTitleView.setTextColor(context.getColorStateList(R.color.text_selector_color_1))
            } else {
                val colorStateList = ResourcesCompat.getColorStateList(
                    context.resources, R.color.text_selector_color_1, null
                )
                mTitleView.setTextColor(colorStateList)
            }
            itemView.setOnClickListener { callback?.onClick?.invoke(item) }

            itemView.setOnFocusChangeListener { view, hasFocus ->
                (position == 0).yes {
                    hasFocus.yes {
                        view.margin(leftMargin = com.shudong.lib_dimen.R.dimen.qb_px_15.dimenValue())
                    }.otherwise {
                        view.margin(leftMargin = com.shudong.lib_dimen.R.dimen.qb_px_0.dimenValue())

                    }
                }
                mCardView.isSelected = hasFocus
                val animation = AnimationUtils.loadAnimation(
                    context, if (hasFocus) R.anim.zoom_in_max else R.anim.zoom_out_max
                )
                animation.fillAfter = true
                itemView.startAnimation(animation)
            }


            mCardView.setCallback { selected -> callback?.onSelect?.invoke(selected, item) }

            when (item.iconType) {
                TypeItem.TYPE_ICON_IMAGE_RES -> mIV.setImageResource((item.icon as Int))
                TypeItem.TYPE_ICON_ASSETS -> GlideUtils.bind(
                     mIV, FileUtils.readAssets(
                        context, item.icon as String
                    )
                )

                else ->      {

                    if(!item.icon.toString().contains("http")){
                        mIV.setImageDrawable(context.getDrawableByName(item.icon.toString()))
                    }else{

                        val cacheFile = AppCache.homeData.dataList.get(item.icon)?.let { File(it) }
                        if (cacheFile?.exists()==true&&AppCache.isAllDownload) {
                            // 使用缓存的 Drawable
                            mIV.loadImageWithGlide(cacheFile){
                                // 删除本地缓存文件
                                if (cacheFile.exists()) {
                                    cacheFile.delete()
                                }

                                val cacheList = AppCache.homeData.dataList
                                // 从 map 中删除对应的条目
                                cacheList.remove(item.icon)
                                AppCache.homeData = HomeDataList(cacheList)

                                if( !item.iconName.isNullOrEmpty()){
                                    mIV.setImageDrawable(context.getDrawableByName(item.iconName.toString()))
                                }
                            }
                        } else {
                            // 轮询直到有缓存 Drawable

                            if(!item.iconName.isNullOrEmpty()){
                                mIV.setImageDrawable(context.getDrawableByName(item.iconName.toString()))
                            }
                            startPollingForCache(mIV,item.icon.toString())
                        }
                    }


                }


            }
            mTitleView.text = item.name
        }

        fun unbind() {
        }
    }


    private fun startPollingForCache( mIV:ImageView, image: String) {
        val handler = Handler(Looper.getMainLooper())
        val pollingInterval = 500L // 轮询间隔（毫秒）
        val maxAttempts = 20 // 最大轮询次数
        var attempts = 0

        val runnable = object : Runnable {
            override fun run() {
                val cacheFile = AppCache.homeData.dataList.get(image)?.let { File(it) }
                if (cacheFile?.exists()==true&&AppCache.isAllDownload) {
                    // 使用缓存的 Drawable

                    GlideUtils.bind( mIV, cacheFile)
                } else if (attempts < maxAttempts) {
                    attempts++
                    handler.postDelayed(this, pollingInterval)
                } else {

                    // 处理没有缓存的情况，可能需要使用默认图像或错误处理
                }
            }
        }
        handler.post(runnable)
    }

}
