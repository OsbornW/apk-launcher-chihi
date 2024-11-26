package com.soya.launcher.net.viewmodel

import android.annotation.SuppressLint
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.yes
import com.soya.launcher.BuildConfig
import com.soya.launcher.bean.NetworkType
import com.soya.launcher.bean.SearchDto
import com.soya.launcher.bean.WifiNetwork
import com.soya.launcher.net.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.inject

class WifiViewModel : BaseViewModel() {

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
                if (networks.none { it.ssid == result.SSID }) {
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

            networks.forEach {
                // "当前的WiFi数据是：$it".e("chihi_error")
            }
        }
        return networks
    }

    // 判断网络是否需要密码
    private fun isNetworkSecured(scanResult: ScanResult): Boolean {
        val capabilities = scanResult.capabilities
        return capabilities.contains("WEP") || capabilities.contains("WPA") || capabilities.contains("WPA2")
    }

}