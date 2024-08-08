package com.soya.launcher.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shudong.lib_base.global.AppCacheBase
import com.soya.launcher.App
import com.soya.launcher.R
import com.soya.launcher.bean.Movice
import com.soya.launcher.cache.AppCache
import com.soya.launcher.h27002.getDrawableByName
import com.soya.launcher.utils.FileUtils
import com.soya.launcher.utils.GlideUtils
import com.soya.launcher.view.MyFrameLayout
import java.io.File

class MainContentAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    dataList: MutableList<Movice>,
    callback: Callback?
) : RecyclerView.Adapter<MainContentAdapter.Holder?>() {
    private val dataList: MutableList<Movice>

    private val callback: Callback?
    private var layoutId: Int = 0

    init {
        this.dataList = dataList
        this.callback = callback
    }

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

    fun replace(list: List<Movice>?) {
        dataList.clear()
        dataList.addAll(list!!)
        notifyDataSetChanged()
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        private val mCardView: MyFrameLayout
        private val mIV: ImageView
        private val tvName: TextView?
        private val tvLoadding: TextView?

        init {
            view.nextFocusUpId = R.id.header
            mIV = view.findViewById(R.id.image)
            mCardView = view as MyFrameLayout
            tvName = view.findViewById(R.id.tv_name)
            tvLoadding = view.findViewById(R.id.tv_loadding)
        }

        fun bind(item: Movice, position: Int) {
            val root = itemView.rootView
            when (item.picType) {
                Movice.PIC_ASSETS -> {
                    Log.d("zy1996", "bind: 当前要显示的本地图片是------" + item.imageUrl)
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
                   /* if (!item.isLocal && !TextUtils.isEmpty(item.url) && App.MOVIE_IMAGE.containsKey(
                            item.url
                        )
                    ) {
                        Log.e("zengyue", "bind: Local=====进来了1==$position")
                        val obj = App.MOVIE_IMAGE[item.url]
                        if (obj != null) image = obj
                        Log.e("zengyue", "bind: Local=====进来了2==$image")
                    }
*/
                    //String path = FilePathMangaer.getMoviePath(context) + "/" + item.getPlaceHolderList().get(position).path;
                    //Drawable drawable = H27002ExtKt.getDrawableFromPath(context,path);
                    Log.e("zengyue", "bind: 当前要加载的路径是$image")

                    /*if(item.getImageName()==null||((String)item.getImageName()).isEmpty()){
                        GlideExtKt.bindImageView( mIV, TextUtils.isEmpty((CharSequence) image) ? R.drawable.transparent : image,null);
                    }else {
                        GlideExtKt.bindImageView( mIV, TextUtils.isEmpty((CharSequence) image) ? R.drawable.transparent : image,H27002ExtKt.getDrawableByName(context,(String) item.getImageName()));

                    }*/

                    if(!image.toString().contains("http")){
                        mIV.setImageDrawable(context.getDrawableByName(image.toString()))
                    }else{
                        val cacheFile = AppCache.homeData.dataList.get(image as String)
                        "当前的文件路径是"
                        if (cacheFile != null) {
                            // 使用缓存的 Drawable
                            Log.e("zengyue", "bind: 走的缓存Movice===")
                            //mIV.setImageDrawable(cachedDrawable);
                            GlideUtils.bind(context, mIV, cacheFile)
                        } else {
                            // 轮询直到有缓存 Drawable
                            Log.e("zengyue", "bind: 走的网络Movice===")
                            startPollingForCache(mIV, image)
                        }
                    }


                    if (tvLoadding != null) {
                        if (item.id.isEmpty()) {
                            tvLoadding.visibility = View.GONE
                        }
                    }
                }

                else -> GlideUtils.bind(context, mIV, R.drawable.transparent)
            }
            itemView.setOnClickListener(View.OnClickListener {
                if (TextUtils.isEmpty(item.url)) return@OnClickListener
                callback?.onClick(item)
            })

            root.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                mCardView.isSelected = hasFocus
                val animation = AnimationUtils.loadAnimation(
                    context, if (hasFocus) R.anim.zoom_in_middle else R.anim.zoom_out_middle
                )
                root.startAnimation(animation)
                animation.fillAfter = true
            }

            mCardView.setCallback { selected -> callback?.onFouces(selected, item) }
        }

        fun unbind() {
        }
    }

    interface Callback {
        fun onClick(bean: Movice)
        fun onFouces(hasFocus: Boolean, bean: Movice)
    }


    private fun startPollingForCache( mIV:ImageView, image: String) {
        val handler = Handler(Looper.getMainLooper())
        val pollingInterval = 500L // 轮询间隔（毫秒）
        val maxAttempts = 20 // 最大轮询次数
        var attempts = 0

        val runnable = object : Runnable {
            override fun run() {
                val cacheFile = AppCache.homeData.dataList.get(image)?.let { File(it) }
                if (cacheFile != null && cacheFile.exists()) {
                    // 使用缓存的 Drawable
                    Log.e("zengyue", "Polling: 走的缓存Movice===")
                    GlideUtils.bind(context, mIV, cacheFile)
                } else if (attempts < maxAttempts) {
                    attempts++
                    handler.postDelayed(this, pollingInterval)
                } else {
                    Log.e("zengyue", "Polling: 达到最大轮询次数，缓存仍然不可用")
                    // 处理没有缓存的情况，可能需要使用默认图像或错误处理
                }
            }
        }
        handler.post(runnable)
    }

}
