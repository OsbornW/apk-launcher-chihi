package com.soya.launcher.adapter

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.global.AppCacheBase
import com.shudong.lib_base.global.AppCacheBase.drawableCache
import com.soya.launcher.R
import com.soya.launcher.bean.TypeItem
import com.soya.launcher.cache.AppCache
import com.soya.launcher.ext.bindImageView
import com.soya.launcher.h27002.getDrawableByName
import com.soya.launcher.utils.FileUtils
import com.soya.launcher.utils.GlideUtils
import com.soya.launcher.view.MyCardView
import java.io.File

class MainHeaderAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    private val items: MutableList<TypeItem>,
    private val callback: Callback?
) : RecyclerView.Adapter<MainHeaderAdapter.Holder>() {
    private val selectItem = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(inflater.inflate(R.layout.holder_header, parent, false))
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
            itemView.setOnClickListener { callback?.onClick(item) }

            root.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                mCardView.isSelected = hasFocus
                val animation = AnimationUtils.loadAnimation(
                    context, if (hasFocus) R.anim.zoom_in_max else R.anim.zoom_out_max
                )
                animation.fillAfter = true
                itemView.startAnimation(animation)
            }

            mCardView.setCallback { selected -> callback?.onSelect(selected, item) }

            when (item.iconType) {
                TypeItem.TYPE_ICON_IMAGE_RES -> mIV.setImageResource((item.icon as Int))
                TypeItem.TYPE_ICON_ASSETS -> GlideUtils.bind(
                    context, mIV, FileUtils.readAssets(
                        context, item.icon as String
                    )
                )

                else ->      {

                    if(!item.icon.toString().contains("http")){
                        mIV.setImageDrawable(context.getDrawableByName(item.icon.toString()))
                    }else{
                        for ((key,value) in AppCache.homeData.dataList){
                            "当前的Key：：$key::::$value".e("zengyue2")
                        }
                        val cacheFile = AppCache.homeData.dataList.get(item.icon)?.let { File(it) }
                        if (cacheFile?.exists()==true&&AppCache.isAllDownload) {
                            // 使用缓存的 Drawable
                            "走的缓存${AppCache.homeData.dataList.get(item.icon)}".e("zengyue2")
                            //mIV.setImageDrawable(cachedDrawable);
                            GlideUtils.bind(context, mIV, cacheFile)
                        } else {
                            // 轮询直到有缓存 Drawable
                            "走的轮询缓存".e("zengyue2")
                            if(item.iconName!=null && item.iconName.isNotEmpty()){
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

    interface Callback {
        fun onClick(bean: TypeItem)

        fun onSelect(selected: Boolean, bean: TypeItem)
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

                    GlideUtils.bind(context, mIV, cacheFile)
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
