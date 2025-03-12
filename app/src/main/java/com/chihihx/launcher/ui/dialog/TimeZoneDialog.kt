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
            val list: MutableList<SimpleTimeZone> = ArrayList()
            val aDefault = TimeZone.getDefault()
            withContext(Dispatchers.IO){

                // 创建一个代表性时区的预定义列表
                val representativeZones = arrayOf(
                    "UTC",                  // 协调世界时
                    "America/New_York",     // 美东时间
                    "America/Los_Angeles",  // 美西时间
                    "Europe/London",        // 伦敦时间
                    "Europe/Paris",         // 巴黎时间
                    "Asia/Tokyo",          // 东京时间
                    "Asia/Shanghai",       // 上海时间
                    "Australia/Sydney",    // 悉尼时间
                    "Pacific/Auckland",    // 奥克兰时间
                    "Asia/Dubai",          // 迪拜时间
                    "Asia/Kolkata",        // 印度时间
                    "Europe/Moscow",       // 莫斯科时间
                    "America/Chicago",     // 芝加哥时间
                    "America/Sao_Paulo",   // 圣保罗时间
                    "Asia/Singapore",      // 新加坡时间
                    "Africa/Johannesburg", // 约翰内斯堡时间
                    "America/Mexico_City", // 墨西哥城时间
                    "Asia/Seoul",          // 首尔时间
                    "Europe/Berlin",       // 柏林时间
                    "Pacific/Honolulu"     // 夏威夷时间
                )


                for (i in representativeZones.indices) {
                    val id = representativeZones[i]
                    val zone = TimeZone.getTimeZone(id)
                    list.add(SimpleTimeZone(zone, id, zone.displayName))
                }

                // 排序集合
                Collections.sort(list, Comparator.comparingInt { o: SimpleTimeZone -> o.zone.rawOffset })

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
                       "开始执行"
                       scrollToPosition(select)
                   },0)
                }

                "当前选中的时区是：${aDefault.displayName}::$select"

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
