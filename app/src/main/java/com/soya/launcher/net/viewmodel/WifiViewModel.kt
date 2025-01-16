package com.soya.launcher.net.viewmodel

import android.annotation.SuppressLint
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.utils.bindingAdapter
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import com.soya.launcher.R
import com.soya.launcher.bean.NetworkType
import com.soya.launcher.bean.WifiNetwork
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WifiViewModel : BaseViewModel() {

    /**
     * 获取附近所有可用的WiFi网络
     */
    @SuppressLint("MissingPermission")
    fun getWifiNetworks(wifiManager: WifiManager): List<WifiNetwork> {
        val isSuccess = wifiManager.startScan()
        var networks = mutableListOf<WifiNetwork>()
        isSuccess.yes {
            val connectedNetwork = wifiManager.connectionInfo
            val configuredNetworks = wifiManager.configuredNetworks
            val scanResults = wifiManager.scanResults

            // 获取当前连接的网络ID
            val isConnected = connectedNetwork.ipAddress != 0
            //"当前网络是否是连接的？${isConnected}".e("chihi_error")


            // 已连接的网络
            "当前连接的网络信息是：${connectedNetwork.ssid}===${connectedNetwork.ipAddress}".e("chihi_error")
            if (connectedNetwork != null && connectedNetwork.ssid != "<unknown ssid>") {
                networks.add(
                    WifiNetwork(
                        ssid = connectedNetwork.ssid.replace("\"", ""),
                        signalStrength = WifiManager.calculateSignalLevel(connectedNetwork.rssi, 5),
                        requiresPassword = false,
                        networkType = isConnected.yes { NetworkType.CONNECTED }
                            .otherwise { NetworkType.CONNECTING } // 标记为已连接
                    )
                )
            }

            // 已保存的网络（从已配置网络列表中提取）
            configuredNetworks?.forEach { config ->
                if (networks.none { it.ssid == config.SSID.replace("\"", "") }) {
                    networks.add(
                        WifiNetwork(
                            ssid = config.SSID.replace("\"", ""),
                            signalStrength = -1, // 信号强度未知
                            requiresPassword = true, // 假设保存的网络大多数需要密码
                            networkType = NetworkType.SAVED // 标记为已保存
                        )
                    )
                }
            }

            // 扫描到的可用网络
            scanResults?.forEach { result ->
                if (result.SSID.isNotEmpty() && networks.none { it.ssid == result.SSID }) {
                    networks.add(
                        WifiNetwork(
                            ssid = result.SSID,
                            signalStrength = WifiManager.calculateSignalLevel(result.level, 5),
                            requiresPassword = isNetworkSecured(result),
                            networkType = NetworkType.OTHER // 标记为其他网络
                        )
                    )
                }
            }

            // 按信号强度排序
            networks = networks.sortedByDescending { it.signalStrength }.toMutableList()

            /*networks.forEach {
                 "当前的WiFi数据是：$it".e("chihi_error")
            }*/
        }
        return networks
    }

    fun updateNewData(
        oldList: MutableList<WifiNetwork>,
        newList: MutableList<WifiNetwork>,
        rvList: RecyclerView,
        focusedSSID: String
    ) {

        try {
            var sortedNewList = newList
            if (rvList.id != R.id.rvSavedWiFi) {
                sortedNewList = newList.sortedWith(
                    compareByDescending<WifiNetwork> { it.signalStrength }
                        .thenBy { it.ssid }
                ).toMutableList()   // 按信号强度降序、SSID升序
            }

            val updatedIndexes = mutableListOf<Pair<Int, Int>>() // 记录 (旧索引, 新索引)
            val alreadyProcessed = mutableSetOf<String>() // 跟踪已处理的 SSID

            // 1. 更新已存在的项
            sortedNewList.forEachIndexed { newIndex, newItem ->
                val existingIndex = oldList.indexOfFirst { it.ssid == newItem.ssid }
                if (existingIndex != -1) {
                    // 如果存在，更新 Bean 数据
                    val existingItem = oldList[existingIndex]
                    if (existingItem.signalStrength != newItem.signalStrength || existingItem.networkType != newItem.networkType) {
                        existingItem.signalStrength = newItem.signalStrength
                        existingItem.networkType = newItem.networkType
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
                val removeItem = oldList.removeAt(index)
                if (removeItem.ssid != focusedSSID) rvList.bindingAdapter.notifyItemRemoved(index)


            }

            if (rvList.id == R.id.rvSavedWiFi) {
                // 2. 重新计算 newIndex 和 oldIndex，避免越界
                val adjustedIndexes = mutableListOf<Pair<Int, Int>>() // 用来存储调整后的索引

                updatedIndexes.forEachIndexed { i, (oldIndex, newIndex) ->
                    var adjustedOldIndex = oldIndex
                    var adjustedNewIndex = newIndex

                    // 调整 newIndex：遍历之前的操作，确保当前操作的索引是正确的
                    for (j in 0 until i) {
                        // 如果当前的 oldIndex 或 newIndex 小于之前的操作的 newIndex，更新它们
                        val (prevOldIndex, prevNewIndex) = updatedIndexes[j]

                        // 如果当前 oldIndex 已经被前面的操作移动过，更新当前的索引
                        if (prevNewIndex <= adjustedOldIndex) {
                            adjustedOldIndex++
                        }

                        // 如果当前 newIndex 已经被前面的操作移动过，更新当前的索引
                        if (prevNewIndex <= adjustedNewIndex) {
                            adjustedNewIndex++
                        }
                    }

                    // 确保新的 oldIndex 和 newIndex 在有效范围内
                    adjustedOldIndex = adjustedOldIndex.coerceIn(0, oldList.size - 1)
                    adjustedNewIndex = adjustedNewIndex.coerceIn(0, oldList.size - 1)

                    adjustedIndexes.add(Pair(adjustedOldIndex, adjustedNewIndex))
                }


                adjustedIndexes.forEach { (oldIndex, newIndex) ->
                    if (newIndex != -1 && newIndex != oldIndex && oldIndex < oldList.size) {
                        //if(oldList[oldIndex].networkType==NetworkType.CONNECTED&&oldIndex!=0)oldList.moveItem(rvList,oldIndex,0)
                        oldList.moveItem(rvList, oldIndex, newIndex)
                    }
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
        } catch (e: Exception) {
            "捕捉到了大异常了1".e("chihi_error")
        }


    }

    fun updateSavedData(
        oldList: MutableList<WifiNetwork>,
        newList: MutableList<WifiNetwork>,
        rvList: RecyclerView,
        focusedSSID: String
    ) {

        try {
            var sortedNewList = newList
            if (rvList.id != R.id.rvSavedWiFi) {
                sortedNewList = newList.sortedWith(
                    compareByDescending<WifiNetwork> { it.signalStrength }
                        .thenBy { it.ssid }
                ).toMutableList()   // 按信号强度降序、SSID升序
            }

            val updatedIndexes = mutableListOf<Pair<Int, Int>>() // 记录 (旧索引, 新索引)
            val alreadyProcessed = mutableSetOf<String>() // 跟踪已处理的 SSID

            // 1. 更新已存在的项
            sortedNewList.forEachIndexed { newIndex, newItem ->
                val existingIndex = oldList.indexOfFirst { it.ssid == newItem.ssid }
                if (existingIndex != -1) {
                    // 如果存在，更新 Bean 数据
                    val existingItem = oldList[existingIndex]
                    if (existingItem.signalStrength != newItem.signalStrength || existingItem.networkType != newItem.networkType) {
                        existingItem.signalStrength = newItem.signalStrength
                        existingItem.networkType = newItem.networkType

                        rvList.adapter?.notifyItemChanged(existingIndex)


                    }
                    updatedIndexes.add(Pair(existingIndex, newIndex))
                    alreadyProcessed.add(newItem.ssid)
                }
            }


            // 2. 找到新增的项并插入
            sortedNewList.forEachIndexed { newIndex, newItem ->
                if (!alreadyProcessed.contains(newItem.ssid)) {

                    "开始插入数据".e("chihi_error")
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

                val removeItem = oldList.removeAt(index)
                //if (removeItem.ssid != focusedSSID) rvList.bindingAdapter.notifyItemRemoved(index)
                 rvList.bindingAdapter.notifyItemRemoved(index)


            }

            if (rvList.id == R.id.rvSavedWiFi) {
                // 2. 重新计算 newIndex 和 oldIndex，避免越界
                val adjustedIndexes = mutableListOf<Pair<Int, Int>>() // 用来存储调整后的索引

                updatedIndexes.forEachIndexed { i, (oldIndex, newIndex) ->
                    var adjustedOldIndex = oldIndex
                    var adjustedNewIndex = newIndex

                    // 调整 newIndex：遍历之前的操作，确保当前操作的索引是正确的
                    for (j in 0 until i) {
                        // 如果当前的 oldIndex 或 newIndex 小于之前的操作的 newIndex，更新它们
                        val (prevOldIndex, prevNewIndex) = updatedIndexes[j]

                        // 如果当前 oldIndex 已经被前面的操作移动过，更新当前的索引
                        if (prevNewIndex <= adjustedOldIndex) {
                            adjustedOldIndex++
                        }

                        // 如果当前 newIndex 已经被前面的操作移动过，更新当前的索引
                        if (prevNewIndex <= adjustedNewIndex) {
                            adjustedNewIndex++
                        }
                    }

                    // 确保新的 oldIndex 和 newIndex 在有效范围内
                    adjustedOldIndex = adjustedOldIndex.coerceIn(0, oldList.size - 1)
                    adjustedNewIndex = adjustedNewIndex.coerceIn(0, oldList.size - 1)

                    adjustedIndexes.add(Pair(adjustedOldIndex, adjustedNewIndex))
                }


                adjustedIndexes.forEach { (oldIndex, newIndex) ->
                    if (newIndex != -1 && newIndex != oldIndex && oldIndex < oldList.size) {

                        if(oldList[oldIndex].networkType==NetworkType.CONNECTED&&oldIndex!=0)oldList.moveItem(rvList,oldIndex,0)

                        "开始调整顺序，移动 ${oldIndex}  到 ${newIndex}".e("chihi_error")
                        //oldList.moveItem(rvList,oldIndex,newIndex)


                    }
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
        } catch (e: Exception) {
        }


    }


    // 调整元素位置的函数
    fun MutableList<WifiNetwork>.moveItem(
        rvList: RecyclerView,
        fromPosition: Int,
        toPosition: Int
    ) {
        // 从原位置移除元素
        val item = this.removeAt(fromPosition)
        // 插入到新位置
        this.add(toPosition, item)

        // 通知适配器更新 UI
        rvList.adapter?.notifyItemMoved(fromPosition, toPosition)
    }

    // 延迟发送数据的函数
    fun MutableLiveData<MutableList<WifiNetwork>>.postDelayedData(data: MutableList<WifiNetwork>, delayMillis: Long = 0) {
        viewModelScope.launch {
            delay(delayMillis) // 延迟指定时间
            this@postDelayedData.postValue(data) // 延迟后更新 LiveData
        }
    }

    var previousNetworks: MutableList<WifiNetwork>? = null
    var previousSavedNetworks: MutableList<WifiNetwork>? = null
    val updateSavedNetworksData = MutableLiveData<MutableList<WifiNetwork>>()
    val updateOtherNetworksData = MutableLiveData<MutableList<WifiNetwork>>()
    fun getFilteredNetWorks(wifiManager: WifiManager) {
        val allNetworks = getWifiNetworks(wifiManager)

        // 将已连接和已保存的网络提取到一个新的集合中
        val connectedAndSavedNetworks = allNetworks.filter {
            it.networkType == NetworkType.CONNECTED || it.networkType == NetworkType.SAVED
        }

        // 从原集合中移除已连接和已保存的网络
        val otherWorks = allNetworks.filterNot {
            it.networkType == NetworkType.CONNECTED || it.networkType == NetworkType.SAVED
        }

        // 如果列表有变化，更新 UI

        if (connectedAndSavedNetworks != previousSavedNetworks) {
            previousSavedNetworks = connectedAndSavedNetworks.toMutableList()
            updateSavedNetworksData.postDelayedData(connectedAndSavedNetworks.toMutableList())
        }

        if (otherWorks != previousNetworks) {
            previousNetworks = otherWorks.toMutableList()
            updateOtherNetworksData.postValue(otherWorks.toMutableList())
        }
    }


    // 判断网络是否需要密码
    private fun isNetworkSecured(scanResult: ScanResult): Boolean {
        val capabilities = scanResult.capabilities
        return capabilities.contains("WEP") || capabilities.contains("WPA") || capabilities.contains(
            "WPA2"
        )
    }


}