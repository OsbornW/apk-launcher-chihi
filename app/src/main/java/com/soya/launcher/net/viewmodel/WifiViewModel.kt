package com.soya.launcher.net.viewmodel

import android.annotation.SuppressLint
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import androidx.leanback.widget.VerticalGridView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.yes
import com.soya.launcher.BuildConfig
import com.soya.launcher.bean.NetworkType
import com.soya.launcher.bean.SearchDto
import com.soya.launcher.bean.WifiNetwork
import com.soya.launcher.net.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.inject

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

            // 已连接的网络
            if (connectedNetwork != null && connectedNetwork.ssid != "<unknown ssid>") {
                networks.add(
                    WifiNetwork(
                        ssid = connectedNetwork.ssid.replace("\"", ""),
                        signalStrength = WifiManager.calculateSignalLevel(connectedNetwork.rssi, 5),
                        requiresPassword = false,
                        networkType = NetworkType.CONNECTED // 标记为已连接
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
                if (result.SSID.isNotEmpty() &&networks.none { it.ssid == result.SSID }) {
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
        focusedSSID:String
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
                //"开始插入数据".e("chihi_error")
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
            //"开始移除数据".e("chihi_error")
            rvList.adapter?.notifyItemRemoved(index)
        }

        // 4. 根据新顺序调整已存在项的位置
        updatedIndexes.forEach { (oldIndex, newIndex) ->
            if (newIndex != -1 && newIndex != oldIndex && oldIndex < oldList.size) {
                //"调整已存在项目的数据".e("chihi_error")
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

    var previousNetworks: MutableList<WifiNetwork>? = null
    var previousSavedNetworks: MutableList<WifiNetwork>? = null
    val updateSavedNetworksData = MutableLiveData<MutableList<WifiNetwork>>()
    val updateOtherNetworksData = MutableLiveData<MutableList<WifiNetwork>>()
    fun getFilteredNetWorks(wifiManager:WifiManager){
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
            updateSavedNetworksData.postValue(connectedAndSavedNetworks.toMutableList())
        }

        if (otherWorks != previousNetworks) {
            previousNetworks = otherWorks.toMutableList()
            updateOtherNetworksData.postValue(otherWorks.toMutableList())
        }
    }



    // 判断网络是否需要密码
    private fun isNetworkSecured(scanResult: ScanResult): Boolean {
        val capabilities = scanResult.capabilities
        return capabilities.contains("WEP") || capabilities.contains("WPA") || capabilities.contains("WPA2")
    }



}