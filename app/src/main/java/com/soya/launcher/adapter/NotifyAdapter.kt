package com.soya.launcher.adapter

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.showTipToast
import com.soya.launcher.R
import com.soya.launcher.bean.Notify
import com.soya.launcher.ext.getIndexAfterSorting
import com.soya.launcher.ext.sortByInstallTime
import com.soya.launcher.utils.GlideUtils

class NotifyAdapter(
    private val context: Context,
    private val inflater: LayoutInflater,
    private val dataList: MutableList<Notify>,
    private val layoutId: Int
) : RecyclerView.Adapter<NotifyAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(inflater.inflate(layoutId, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun replace(list: List<Notify>?) {
        dataList.clear()
        dataList.addAll(list!!)
        notifyDataSetChanged()
    }

    fun getDataList():MutableList<Notify>{
        return dataList
    }

    fun refresh(list: MutableList<Notify>) {
        val oldPackageNames = dataList.map { it.type }.toSet()
        val newPackageNames = list.map { it.type }.toSet()

        // 找出新增的 packageName 和缺失的 packageName
        val missingInNew = newPackageNames - oldPackageNames
        val extraInOld = oldPackageNames - newPackageNames

        when {
            // 处理新增的情况
            missingInNew.isNotEmpty()-> {

                missingInNew.forEach { missingPackageName ->
                    val newItem = list.find { it.type == missingPackageName }
                    if (newItem != null) {
                        var tempList = mutableListOf<Notify>()
                        tempList.addAll(dataList)
                        tempList.add(newItem)
                        val sortIndex = tempList.getIndexAfterSorting(newItem)
                        dataList.add(sortIndex,newItem)
                        notifyItemInserted(dataList.indexOf(newItem))
                    }
                }
            }
            // 处理移除的情况
            extraInOld.isNotEmpty()-> {

                val positionsToRemove = mutableListOf<Int>()
                extraInOld.forEach { extraPackageName ->
                    dataList.indexOfFirst { it.type == extraPackageName }.let { position ->
                        if (position != -1) {

                            positionsToRemove.add(position)
                        }
                    }
                }
                // 从最后一个位置开始移除，避免索引问题
                positionsToRemove.sortedDescending().forEach { position ->
                    dataList.removeAt(position)
                    notifyItemRemoved(position)
                }


            }
            else -> {

            }
        }
    }

    /**
     * 获取指定应用在排序后集合中的索引位置
     * 如果找不到对应的索引，返回0
     */
    fun MutableList<Notify>.getIndexAfterSorting(targetAppInfo: Notify): Int {

        // 获取目标应用的信息在排序后集合中的索引位置
        return this.indexOfFirst { it.type == targetAppInfo.type }.takeIf { it >= 0 } ?: 0
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mIV: ImageView = itemView.findViewById(R.id.image)

        fun bind(bean: Notify) {
            //itemView.setFocusable(false);
            //itemView.setFocusableInTouchMode(false);
            //itemView.setEnabled(false);
            GlideUtils.bind(context, mIV, bean.icon)
            itemView.clickNoRepeat {
                when(bean.type){
                    Notify.TYPE_UDisk,Notify.TYPE_TF->{
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.type = "*/*" // 设置文件类型，*/* 表示任意类型
                        context.startActivity(intent)
                    }

                }
            }
        }
    }
}
