package com.chihihx.launcher.utils

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothProfile.ServiceListener
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.SystemClock
import com.chihihx.launcher.bean.MyRunnable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object BluetoothScannerUtils {
    private val EXECUTOR_SERVICE: ExecutorService = Executors.newCachedThreadPool()
    private val BLUETOOTH_ADAPTER: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val MAP: MutableMap<Context, Inner> = ConcurrentHashMap()
    private var scanRunnable: MyRunnable? = null
    private var bluetoothProfile: BluetoothProfile? = null

    fun init(context: Context) {
        if (BLUETOOTH_ADAPTER == null) return
        registProfileProxy(context)
    }

    fun startListening(context: Context, listener: Listener) {
        if (BLUETOOTH_ADAPTER == null) return
        val receiver = Receiver()
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        context.registerReceiver(receiver, filter)
        MAP[context] = Inner(context, receiver, listener)
        listener.onStartScan(BLUETOOTH_ADAPTER)
        if (scanRunnable == null) {
            scanRunnable = object : MyRunnable() {
                override fun run() {
                    while (!isInterrupt) {
                        if (MAP.isEmpty()) continue
                        scanNow()
                        SystemClock.sleep((20 * 1000).toLong())
                    }
                }
            }
            EXECUTOR_SERVICE.execute(scanRunnable)
        }
    }

    fun removeListener(context: Context) {
        if (MAP.containsKey(context)) {
            val inner = MAP[context]
            context.unregisterReceiver(inner!!.receiver)
            MAP.remove(context)
        }
    }

    private fun registProfileProxy(context: Context) {
        if (BLUETOOTH_ADAPTER == null) return
        val serviceListener: ServiceListener = object : ServiceListener {
            override fun onServiceDisconnected(profile: Int) {
            }

            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                bluetoothProfile = proxy
            }
        }

        BLUETOOTH_ADAPTER.getProfileProxy(context, serviceListener, 4)
    }

    fun getProfileProxy(context: Context?): BluetoothProfile? {
        return bluetoothProfile
    }

    fun scanNow() {
        if (BLUETOOTH_ADAPTER == null) return
        if (BLUETOOTH_ADAPTER.isDiscovering) {
            BLUETOOTH_ADAPTER.cancelDiscovery()
        }
        BLUETOOTH_ADAPTER.startDiscovery()
    }

    fun scanStop() {
        if (BLUETOOTH_ADAPTER == null) return
        BLUETOOTH_ADAPTER.cancelDiscovery()
    }

    open class Listener {
        fun onStartScan(adapter: BluetoothAdapter?) {}

        open fun onFound(adapter: BluetoothAdapter?, devices: List<BluetoothDevice?>?) {}

        fun onRemove(adapter: BluetoothAdapter?, devices: List<BluetoothDevice?>?) {}
    }

    class Inner(
        private val context: Context,
        internal val receiver: Receiver,
        val listener: Listener
    ) {
        var profile: BluetoothProfile? = null
    }

    class Receiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device =
                        intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    for ((_, value) in MAP) {
                        value.listener.onFound(BLUETOOTH_ADAPTER, listOf(device))
                    }
                }

                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {}
            }
        }
    }
}
