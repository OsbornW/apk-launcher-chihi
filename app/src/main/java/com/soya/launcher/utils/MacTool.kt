package com.soya.launcher.utils

import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.text.TextUtils
import java.io.BufferedReader
import java.io.FileReader
import java.io.InputStreamReader
import java.io.LineNumberReader
import java.io.Reader
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.Enumeration
import java.util.Locale

object MacTool {
    var VersionInfo: String = ""

    /**
     * 获取设备MAC地址信息
     */
    fun getMac(context: Context): String? {
        var strMac: String? = null

        if (Build.VERSION.SDK_INT < 23) {
            VersionInfo = "6.0以下"
            strMac = getLocalMacAddressFromWifiInfo(context)
            return strMac
        } else if (Build.VERSION.SDK_INT < 24 && Build.VERSION.SDK_INT >= 23) {
            VersionInfo = "6.0以上7.0以下"
            strMac = getMacAddress(context)
            return strMac
        } else if (Build.VERSION.SDK_INT >= 24) {
            VersionInfo = "7.0以上"
            if (!TextUtils.isEmpty(macAddress)) {
                VersionInfo = "7.0以上1"
                strMac = macAddress
                return strMac
            } else if (!TextUtils.isEmpty(machineHardwareAddress)) {
                VersionInfo = "7.0以上2"
                strMac = machineHardwareAddress
                return strMac
            } else {
                VersionInfo = "7.0以上3"
                strMac = localMacAddressFromBusybox
                return strMac
            }
        }

        return "00:00:00:00:00:00"
    }

    // 6.0以下方法，Google提供的公有方法，需要权限
    // <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    /**
     * 根据wifi信息获取本地mac
     */
    private fun getLocalMacAddressFromWifiInfo(context: Context): String {
        val wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val winfo = wifi.connectionInfo
        val mac = winfo.macAddress
        return mac
    }

    /**
     * android 6.0及以上、7.0以下 获取mac地址
     */
    private fun getMacAddress(context: Context): String {
        // 如果是6.0以下，直接通过wifimanager获取

        if (Build.VERSION.SDK_INT < 23) {
            val macAddress0 = getMacAddress0(context)
            if (!TextUtils.isEmpty(macAddress0)) {
                return macAddress0
            }
        }
        var str = ""
        var macSerial = ""
        try {
            val pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address")
            val ir = InputStreamReader(pp.inputStream)
            val input = LineNumberReader(ir)
            while (null != str) {
                str = input.readLine()
                if (str != null) {
                    macSerial = str.trim { it <= ' ' } // 去空格
                    break
                }
            }
        } catch (ex: Exception) {
        }
        if (macSerial == null || "" == macSerial) {
            try {
                return loadFileAsString("/sys/class/net/eth0/address").uppercase(Locale.getDefault())
                    .substring(0, 17)
            } catch (e: Exception) {
                e.printStackTrace()
                //Log.e("----->" + "NetInfoManager", "getMacAddress:" + e.toString());
            }
        }
        return macSerial
    }

    private fun getMacAddress0(context: Context): String {
        if (isAccessWifiStateAuthorized(context)) {
            val wifiMgr = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            var wifiInfo: WifiInfo? = null
            try {
                wifiInfo = wifiMgr.connectionInfo
                return wifiInfo.macAddress
            } catch (e: Exception) {
            }
        }
        return ""
    }

    /**
     * Check whether accessing wifi state is permitted
     */
    private fun isAccessWifiStateAuthorized(context: Context): Boolean {
        //Log.e("----->" + "NetInfoManager", "isAccessWifiStateAuthorized:" + "access wifi state is enabled");
        return PackageManager.PERMISSION_GRANTED == context.checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE")
    }

    @Throws(Exception::class)
    private fun loadFileAsString(fileName: String): String {
        val reader = FileReader(fileName)
        val text = loadReaderAsString(reader)
        reader.close()
        return text
    }

    @Throws(Exception::class)
    private fun loadReaderAsString(reader: Reader): String {
        val builder = StringBuilder()
        val buffer = CharArray(4096)
        var readLength = reader.read(buffer)
        while (readLength >= 0) {
            builder.appendRange(buffer, 0, readLength)
            readLength = reader.read(buffer)
        }
        return builder.toString()
    }

    // -----------------------------------------
    private val macAddress: String?
        /**
         * 根据IP地址获取MAC地址
         */
        get() {
            var strMacAddr: String? = null
            try {
                // 获得IpD地址
                val ip = localInetAddress
                val b = NetworkInterface.getByInetAddress(ip).hardwareAddress
                val buffer = StringBuffer()
                for (i in b.indices) {
                    if (i != 0) {
                        buffer.append(':')
                    }
                    val str = Integer.toHexString(b[i].toInt() and 0xFF)
                    buffer.append(if (str.length == 1) "0$str" else str)
                }
                strMacAddr = buffer.toString().uppercase(Locale.getDefault())
            } catch (e: Exception) {
            }
            return strMacAddr
        }

    private val localInetAddress: InetAddress?
        /**
         * 获取移动设备本地IP
         */
        get() {
            var ip: InetAddress? = null
            try {
                // 列举
                val en_netInterface = NetworkInterface.getNetworkInterfaces()
                while (en_netInterface.hasMoreElements()) { // 是否还有元素
                    val ni = en_netInterface.nextElement() // 得到下一个元素
                    val en_ip = ni.inetAddresses // 得到一个ip地址的列举
                    while (en_ip.hasMoreElements()) {
                        ip = en_ip.nextElement()
                        if (!ip.isLoopbackAddress && ip.hostAddress.indexOf(":") == -1) break
                        else ip = null
                    }

                    if (ip != null) {
                        break
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }
            return ip
        }

    private val localIpAddress: String?
        /**
         * 获取本地IP
         */
        get() {
            try {
                val en = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf = en.nextElement()
                    val enumIpAddr = intf.inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress) {
                            return inetAddress.hostAddress
                        }
                    }
                }
            } catch (ex: SocketException) {
                ex.printStackTrace()
            }
            return null
        }

    // -------------------------------------------------
    /** android 7.0及以上 （2）扫描各个网络接口获取mac地址  */
    private val machineHardwareAddress: String?
        /**
         * 获取设备HardwareAddress地址
         */
        get() {
            var interfaces: Enumeration<NetworkInterface?>? = null
            try {
                interfaces = NetworkInterface.getNetworkInterfaces()
            } catch (e: SocketException) {
                e.printStackTrace()
            }
            var hardWareAddress: String? = null
            var iF: NetworkInterface? = null
            if (interfaces == null) {
                return null
            }
            while (interfaces.hasMoreElements()) {
                iF = interfaces.nextElement()
                try {
                    hardWareAddress = bytesToString(iF!!.hardwareAddress)
                    if (hardWareAddress != null) break
                } catch (e: SocketException) {
                    e.printStackTrace()
                }
            }
            return hardWareAddress
        }

    /*** byte转为String  */
    private fun bytesToString(bytes: ByteArray?): String? {
        if (bytes == null || bytes.size == 0) {
            return null
        }
        val buf = StringBuilder()
        for (b in bytes) {
            buf.append(String.format("%02X:", b))
        }
        if (buf.length > 0) {
            buf.deleteCharAt(buf.length - 1)
        }
        return buf.toString()
    }

    private val localMacAddressFromBusybox: String
        /**
         * android 7.0及以上 （3）通过busybox获取本地存储的mac地址
         * 根据busybox获取本地Mac
         */
        get() {
            var result: String? = ""
            var Mac = ""
            result = callCmd("busybox ifconfig", "HWaddr")
            // 如果返回的result == null，则说明网络不可取
            if (result == null) {
                return "网络异常"
            }
            // 对该行数据进行解析
            // 例如：eth0 Link encap:Ethernet HWaddr 00:16:E8:3E:DF:67
            if (result.length > 0 && result.contains("HWaddr") == true) {
                Mac = result.substring(result.indexOf("HWaddr") + 6, result.length - 1)
                result = Mac
            }
            return result
        }

    private fun callCmd(cmd: String, filter: String): String {
        var result = ""
        var line = ""
        try {
            val proc = Runtime.getRuntime().exec(cmd)
            val `is` = InputStreamReader(proc.inputStream)
            val br = BufferedReader(`is`)

            while ((br.readLine().also { line = it }) != null && line.contains(filter) == false) {
                result += line
            }

            result = line
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}
