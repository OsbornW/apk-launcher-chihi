package com.chihihx.launcher.ui.dialog

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.os.HandlerCompat.postDelayed
import androidx.lifecycle.lifecycleScope
import com.drake.brv.utils.addModels
import com.drake.brv.utils.setup
import com.shudong.lib_base.ext.clickNoRepeat
import com.chihihx.launcher.R
import com.chihihx.launcher.bean.SimpleTimeZone
import com.chihihx.launcher.databinding.DialogTimeZoneBinding
import com.chihihx.launcher.databinding.HolderTimeZoneBinding
import com.chihihx.launcher.ext.getFormattedTimeZone
import com.shudong.lib_base.ext.printLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections
import java.util.TimeZone

class TimeZoneDialog : SingleDialogFragment<DialogTimeZoneBinding>() {

    private var callback: Callback? = null



    private var select: TimeZone? = null
    override fun init(view: View) {

        binding.content.setup {
            addType<SimpleTimeZone>(R.layout.holder_time_zone)
            onBind {
                val dto = getModel<SimpleTimeZone>()
                val binding  = getBinding<HolderTimeZoneBinding>()
                val isSelect = select != null && dto.zone.id == select!!.id
                binding.check.visibility = if (isSelect) View.VISIBLE else View.GONE
                binding.desc.visibility = if (isSelect) View.GONE else View.VISIBLE
                itemView.clickNoRepeat {
                    select = dto.zone
                    if (callback != null) callback!!.onClick(dto)
                }
            }
        }

        blur(binding.root, binding.blur)
    }

    override fun initBind(view: View) {

        lifecycleScope.launch {
           /* withContext(Dispatchers.Main){
                blur(mRootView, mBlur)
            }*/
            var select = 0
            var list: MutableList<SimpleTimeZone> = ArrayList()
            val aDefault = TimeZone.getDefault()
            withContext(Dispatchers.IO){

                list = filterTimeZones().toMutableList()
                for (i in list.indices) {
                    val id = list[i].zone.id
                    if (id == aDefault.id) select = i
                }
            }
            withContext(Dispatchers.Main){
                binding.content.addModels(list)
                binding.content.setColumnWidth(ViewGroup.LayoutParams.WRAP_CONTENT)

                binding.content.apply {
                   postDelayed({
                       if (select in 0 until list.size) {
                           scrollToPosition(select)
                       } else {
                           "Invalid select index: $select, list size: ${list.size}".printLog()
                           scrollToPosition(0) // 默认滚动到第一个
                       }
                   },0)
                }

                "当前选中的时区是：${aDefault.displayName}::$select".printLog()

            }
        }




    }


    override fun getDimAmount(): Float {
        return 0f
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    interface Callback {
        fun onClick(bean: SimpleTimeZone?)
    }

    fun filterTimeZones(targetCount: Int = 90): List<SimpleTimeZone> {
        val ids = TimeZone.getAvailableIDs()
        val uniqueZones = mutableMapOf<Int, MutableList<String>>() // 偏移量 -> 时区 ID 列表

        val requiredZones = listOf("Asia/Shanghai", "Asia/Taipei", "Asia/Hong_Kong", "Asia/Tokyo", "Asia/Seoul")

        // 按偏移量分组
        for (id in ids) {
            if (id.contains("/")) { // 只保留城市格式的 ID
                val zone = TimeZone.getTimeZone(id)
                val offset = zone.rawOffset
                val zoneList = uniqueZones.getOrPut(offset) { mutableListOf() }
                zoneList.add(id)
            }
        }

        // 动态计算每组取的数量
        val offsetCount = uniqueZones.size
        val baseTakeCount = targetCount / offsetCount
        val extraCount = targetCount % offsetCount

        val selectedIds = mutableListOf<String>()
        var index = 0
        for (offsetZones in uniqueZones.values) {
            offsetZones.sort() // 按字母排序
            val takeCount = if (index < extraCount) baseTakeCount + 1 else baseTakeCount
            selectedIds.addAll(offsetZones.take(takeCount.coerceAtMost(offsetZones.size)))
            index++
        }

        // 确保目标时区一定包含
        for (zoneId in requiredZones) {
            if (!selectedIds.contains(zoneId)) {
                selectedIds.add(zoneId)
            }
        }

        // 转换为 SimpleTimeZone 列表
        val list: MutableList<SimpleTimeZone> = ArrayList()
        for (id in selectedIds) {
            val zone = TimeZone.getTimeZone(id)
            list.add(SimpleTimeZone(zone, id, zone.getFormattedTimeZone()))
        }
        list.sortBy { it.zone.rawOffset } // 按偏移量排序

        // 截取或返回最终结果
        val finalList = if (list.size > targetCount) list.take(targetCount) else list
        "Filtered time zones: ${finalList.size}".printLog()
        return finalList
    }


    companion object {
        const val TAG: String = "TimeZoneDialog"
        @JvmStatic
        fun newInstance(): TimeZoneDialog {
            val args = Bundle()

            val fragment = TimeZoneDialog()
            fragment.arguments = args
            return fragment
        }
    }
}
