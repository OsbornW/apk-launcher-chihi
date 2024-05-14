package com.shudong.lib_res.chat

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.shudong.lib_res.R
import com.shudong.lib_res.chat.GlobalOnItemClickManager.Companion.instance

/**
 * Created by xiaokai on 2017/02/07.
 */
class EmotionFragment : Fragment() {
    private var mContext: Context? = null
    private var emotionGrid: GridView? = null
    private var startPosition = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* 先确定每页第一个表情的position */if (arguments != null) {
            startPosition = arguments!!.getInt("emotion_start_position", 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.emotion_gird_classic, container, false)
        emotionGrid = view.findViewById<View>(R.id.grid) as GridView
        emotionGrid!!.adapter = MyAdapter()
        emotionGrid!!.onItemClickListener =
            instance!!.getOnItemClickListener(startPosition / 20, mContext)
        return view
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mContext = activity
    }

    internal inner class MyAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return 21
        }

        override fun getItem(position: Int): Any? {
            return null
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            var convertView = convertView
            val holder: ViewHolder
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.emotion_gird_item_classic, parent, false)
                holder = ViewHolder()
                holder.img = convertView.findViewById<View>(R.id.image) as ImageView
                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }
            var path = ""
            path = if (position == 20) {
                "file:///android_asset/ems/delete.webp"
            } else {
                "file:///android_asset/ems/" + (position + startPosition) + ".webp"
            }
            val list = context?.assets?.list("ems")
            list?.forEach {
                Log.i("zy1995", "当前的文件路径是-----$it")
            }

            Glide.with(mContext!!).load(path).into(holder.img!!)
            return convertView
        }

        internal inner class ViewHolder {
            var img: ImageView? = null
        }
    }

    companion object {
        fun newInstance(position: Int): EmotionFragment {
            val fragment = EmotionFragment()
            val bundle = Bundle()
            bundle.putInt("emotion_start_position", position)
            fragment.arguments = bundle
            return fragment
        }
    }
}