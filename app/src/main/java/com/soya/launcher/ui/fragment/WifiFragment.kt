package com.soya.launcher.ui.fragment

import android.content.Context
import android.net.wifi.WifiManager
import androidx.core.view.isVisible
import androidx.leanback.widget.VerticalGridView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.utils.addModels
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setup
import com.shudong.lib_base.ext.appContext
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.bean.NetworkType
import com.soya.launcher.bean.WifiNetwork
import com.soya.launcher.databinding.FragmentWifiBinding
import com.soya.launcher.databinding.ItemWifiBinding
import com.soya.launcher.net.viewmodel.WifiViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class WifiFragment : BaseWallPaperFragment<FragmentWifiBinding,WifiViewModel>() {

    private lateinit var wifiManager: WifiManager
    override fun initView() {
        // 初始化 WifiManager
        wifiManager = appContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) { // 在 STARTED 和 RESUMED 生命周期时执行
                var previousNetworks: MutableList<WifiNetwork>? = null
                var previousSavedNetworks: MutableList<WifiNetwork>? = null
                while (isActive) {
                    val otherWorks = mViewModel.getWifiNetworks(wifiManager)
                    // 如果列表有变化，更新 UI

                    if (otherWorks != previousNetworks) {
                        previousNetworks = otherWorks.toMutableList()
                        updateData(otherWorks.toMutableList(),mBind.rvOtherWiFi)
                    }
                    delay(3000)
                }
            }
        }

        mBind.rvSavedWiFi.setup {
            addType<WifiNetwork>(R.layout.item_wifi)
            onBind {
                val dto = getModel<WifiNetwork>()
                val binding = getBinding<ItemWifiBinding>()
                when(dto.networkType){
                    NetworkType.CONNECTED->{
                        binding.tvConnectedStatus.text = "已连接"
                    }
                    NetworkType.SAVED->{
                        binding.tvConnectedStatus.text = "已保存"
                    }
                    else->{
                        binding.tvConnectedStatus.text = "未知"
                    }
                }
                binding.tvConnectedWiFiName.text = dto.ssid
                itemView.setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        focusedSSID = dto.ssid
                    }
                }

            }
        }.models = arrayListOf()

        mBind.rvOtherWiFi.setup {
            addType<WifiNetwork>(R.layout.item_wifi)
            onBind {
                val dto = getModel<WifiNetwork>()
                val binding = getBinding<ItemWifiBinding>()
                binding.tvConnectedStatus.isVisible = false
                binding.tvConnectedWiFiName.text = dto.ssid
                itemView.setOnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        focusedSSID = dto.ssid
                    }
                }

            }
        }.models = arrayListOf()

        mBind.rvOtherWiFi.apply { post { requestFocus() } }

    }



    fun updateData(newList: MutableList<WifiNetwork>,rvList:VerticalGridView) {

        val oldList = rvList.mutable as MutableList<WifiNetwork>
        if(oldList.size==0){
            rvList.addModels(newList)
        }else{
            updateNewData(oldList,newList,rvList)
        }

    }

    fun updateNewData(
        oldList: MutableList<WifiNetwork>,
        newList: MutableList<WifiNetwork>,
        rvList: VerticalGridView
    ) {
        val sortedNewList = newList.sortedWith(
            compareByDescending<WifiNetwork> { it.signalStrength }
                .thenBy { it.ssid }
        ) // 按信号强度降序、SSID升序

        val updatedIndexes = mutableListOf<Pair<Int, Int>>() // 记录 (旧索引, 新索引)
        val alreadyProcessed = mutableSetOf<String>() // 跟踪已处理的 SSID


        // 1. 更新已存在的项
        sortedNewList.forEachIndexed { newIndex, newItem ->
            val existingIndex = oldList.indexOfFirst { it.ssid == newItem.ssid }
            if (existingIndex != -1) {
                // 如果存在，更新 Bean 数据
                val existingItem = oldList[existingIndex]
                if (existingItem.signalStrength != newItem.signalStrength) {
                    existingItem.signalStrength = newItem.signalStrength
                    rvList.adapter?.notifyItemChanged(existingIndex)
                }
                updatedIndexes.add(Pair(existingIndex, newIndex))
                alreadyProcessed.add(newItem.ssid)
            }
        }

        // 2. 找到新增的项并插入
        sortedNewList.forEachIndexed { newIndex, newItem ->
            if (!alreadyProcessed.contains(newItem.ssid)) {
                oldList.add(newIndex, newItem)
                rvList.adapter?.notifyItemInserted(newIndex)
                alreadyProcessed.add(newItem.ssid)
            }
        }

        // 3. 删除不存在的项
        val toRemoveIndexes = oldList.withIndex()
            .filter { (index, oldItem) -> !sortedNewList.any { it.ssid == oldItem.ssid } }
            .map { it.index }
            .sortedDescending()
        toRemoveIndexes.forEach { index ->
            oldList.removeAt(index)
            rvList.adapter?.notifyItemRemoved(index)
        }

        // 4. 根据新顺序调整已存在项的位置
        updatedIndexes.forEach { (oldIndex, newIndex) ->
            if (newIndex != -1 && newIndex != oldIndex && oldIndex < oldList.size) {
                val item = oldList.removeAt(oldIndex.coerceAtMost(oldList.size - 1))
                oldList.add(newIndex.coerceAtMost(oldList.size), item)
                rvList.adapter?.notifyItemMoved(oldIndex, newIndex)
            }
        }

        // 5. 焦点保持
        val newFocusedPosition = oldList.indexOfFirst { it.ssid == focusedSSID }
        //"当前新的索引：$newFocusedPosition 对应的SSID是：${focusedSSID}".e("chihi_error")

        if (newFocusedPosition != RecyclerView.NO_POSITION) {
            rvList.scrollToPosition(newFocusedPosition)

            // 确保布局完成后再请求焦点
            rvList.post {
                rvList.layoutManager?.findViewByPosition(newFocusedPosition)?.requestFocus()
            }
        }


    }









    private var focusedSSID: String = ""









    companion object {
        @JvmStatic
        fun newInstance() =
            WifiFragment().apply {}
    }
}