package com.soya.launcher.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shudong.lib_base.ext.e
import com.soya.launcher.R
import com.soya.launcher.bean.Data
import com.soya.launcher.bean.HomeDataList
import com.soya.launcher.bean.Movice
import com.soya.launcher.cache.AppCache
import com.soya.launcher.callback.ContentCallback
import com.soya.launcher.ext.loadImageWithGlide
import com.soya.launcher.h27002.getDrawableByName
import com.soya.launcher.utils.FileUtils
import com.soya.launcher.utils.GlideUtils
import com.soya.launcher.view.MyFrameLayout
import java.io.File

class MainContentAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    private val dataList: MutableList<Data>,
    private val callback: ContentCallback?
) : RecyclerView.Adapter<MainContentAdapter.Holder?>() {

    private var layoutId: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(inflater.inflate(layoutId, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(dataList[position], position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setLayoutId(layoutId: Int) {
        this.layoutId = layoutId
    }

    fun replace(list: List<Data>?) {
        list?.let {
            dataList.clear()
            dataList.addAll(it)
            notifyDataSetChanged()
        }

    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        private val mCardView: MyFrameLayout
        private val mIV: ImageView
        private val tvName: TextView?
        private val tvLoadding: TextView?
        private val flRoot: FrameLayout?

        init {
            view.nextFocusUpId = R.id.header
            mIV = view.findViewById(R.id.image)
            mCardView = view as MyFrameLayout
            tvName = view.findViewById(R.id.tv_name)
            tvLoadding = view.findViewById(R.id.tv_loadding)
            flRoot = view.findViewById(R.id.fl_root)
        }

        fun bind(item: Data, position: Int) {
            val root = itemView.rootView
            when (item.picType) {
                Movice.PIC_ASSETS -> {

                    GlideUtils.bind(
                        context, mIV, FileUtils.readAssets(
                            context, item.imageUrl as String
                        )
                    )
                }

                Movice.PIC_NETWORD -> {
                    var image = item.imageUrl
                    if (tvName != null) {
                        tvName.text = item.id
                    }



                    if (!image.toString().contains("http")) {
                        mIV.setImageDrawable(context.getDrawableByName(image.toString()))
                    } else {
                        val cacheFile = AppCache.homeData.dataList.get(image)?.let { File(it) }
                        if (cacheFile?.exists() == true && AppCache.isAllDownload) {

                            // 使用缓存的 Drawable
                            //mIV.loadFileRadius1(cacheFile)
                            mIV.loadImageWithGlide(cacheFile) {
                                // 删除本地缓存文件
                                if (cacheFile.exists()) {
                                    cacheFile.delete()
                                }

                                val cacheList = AppCache.homeData.dataList
                                // 从 map 中删除对应的条目
                                cacheList.remove(item.imageUrl)
                                AppCache.homeData = HomeDataList(cacheList)

                                if (!item.imageName.isNullOrEmpty()) {

                                    val drawable = context.getDrawableByName(item.imageName)
                                    mIV.setImageDrawable(drawable)
                                }
                            }
                        } else {
                            // 轮询直到有缓存 Drawable

                            if (!item.imageName.isNullOrEmpty()) {

                                val drawable = context.getDrawableByName(item.imageName)
                                mIV.setImageDrawable(drawable)
                            }

                            if (image != null) {
                                startPollingForCache(mIV, image)
                            }
                        }
                    }


                    if (tvLoadding != null) {
                        if (item.id?.isNotEmpty() == false) {
                            tvLoadding.visibility = View.GONE
                        }
                    }
                }

                else -> GlideUtils.bind(context, mIV, R.drawable.transparent)
            }
            flRoot?.setOnClickListener(View.OnClickListener {
                "我点击了".e("zengyue1")
                if (TextUtils.isEmpty(item.url)) return@OnClickListener
                callback?.onClick?.invoke(item)
            })

            flRoot?.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                mCardView.isSelected = hasFocus
                val animation = AnimationUtils.loadAnimation(
                    context, if (hasFocus) R.anim.zoom_in_middle else R.anim.zoom_out_middle
                )
                flRoot?.startAnimation(animation)
                animation.fillAfter = true
            }

            mCardView.setCallback { selected -> callback?.onFocus?.invoke(selected, item) }
        }

        fun unbind() {
        }
    }




    private fun startPollingForCache(mIV: ImageView, image: String) {
        val handler = Handler(Looper.getMainLooper())
        val pollingInterval = 500L // 轮询间隔（毫秒）
        val maxAttempts = 20 // 最大轮询次数
        var attempts = 0

        val runnable = object : Runnable {
            override fun run() {
                val cacheFile = AppCache.homeData.dataList.get(image)?.let { File(it) }
                if (cacheFile != null && cacheFile.exists() && AppCache.isAllDownload) {
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
