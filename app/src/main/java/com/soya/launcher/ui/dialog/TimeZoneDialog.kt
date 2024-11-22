package com.soya.launcher.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.HandlerCompat.postDelayed
import androidx.leanback.widget.VerticalGridView
import androidx.lifecycle.lifecycleScope
import com.drake.brv.utils.addModels
import com.drake.brv.utils.setup
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.e
import com.soya.launcher.R
import com.soya.launcher.bean.SimpleTimeZone
import com.soya.launcher.databinding.DialogTimeZoneBinding
import com.soya.launcher.databinding.HolderTimeZoneBinding
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

                val ids = TimeZone.getAvailableIDs()


                for (i in ids.indices) {
                    val id = ids[i]
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
