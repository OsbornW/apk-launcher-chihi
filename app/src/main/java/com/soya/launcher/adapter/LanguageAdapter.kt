package com.soya.launcher.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.Presenter
import com.soya.launcher.R
import com.soya.launcher.bean.Language

class LanguageAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    private val dataList: List<Language>
) : Presenter() {
    private var callback: Callback? = null
    private var select = 0

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return Holder(inflater.inflate(R.layout.holder_language, parent, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val holder = viewHolder as Holder
        holder.bind(item as Language)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val holder = viewHolder as Holder
        holder.unbind()
    }

    fun setSelect(select: Int) {
        this.select = select
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    inner class Holder(view: View) : ViewHolder(view) {
        private val mTitleView: TextView = view.findViewById(R.id.title)
        private val mDescView: TextView = view.findViewById(R.id.desc)
        private val mCheckView: ImageView = view.findViewById(R.id.check)

        fun bind(bean: Language) {
            val isSelect = dataList.indexOf(bean) == select
            mTitleView.text = bean.name
            mDescView.text = bean.desc
            mCheckView.visibility = if (isSelect) View.VISIBLE else View.GONE
            mDescView.visibility = if (isSelect) View.GONE else View.VISIBLE

            view.setOnClickListener {
                select = dataList.indexOf(bean)
                if (callback != null) callback!!.onClick(bean)
            }
        }

        fun unbind() {
        }
    }

    fun interface Callback {
        fun onClick(bean: Language?)
    }
}
