package com.soya.launcher.ui.fragment

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.open.system.SystemUtils
import com.shudong.lib_base.base.BaseViewModel
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.bean.MyRunnable
import com.soya.launcher.databinding.FragmentRemoteControlBinding
import com.soya.launcher.enums.Atts
import com.soya.launcher.enums.IntentAction
import com.soya.launcher.ui.fragment.GuideLanguageFragment.Companion.newInstance
import com.soya.launcher.utils.AndroidSystem
import com.soya.launcher.utils.BluetoothScannerUtils
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class RemoteControlFragment : BaseWallPaperFragment<FragmentRemoteControlBinding,BaseViewModel>(),
    View.OnClickListener {
    private val deviceMap: Map<String, BluetoothDevice> = ConcurrentHashMap()
    private val exec: ExecutorService = Executors.newCachedThreadPool()
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var runnable: MyRunnable? = null
    private var connRunnable: MyRunnable? = null
    private var mCacheDevice: BluetoothDevice? = null
    private var uiHandler: Handler? = null
    private var isConnected = false
    private var isConnecting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity!!.sendBroadcast(Intent(IntentAction.ACTION_NOT_SHOW_REMOTE_DIALOG))
        uiHandler = Handler()
        AndroidSystem.setEnableBluetooth(activity, true)
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        BluetoothScannerUtils.startListening(activity, object : BluetoothScannerUtils.Listener() {
            @SuppressLint("MissingPermission")
            override fun onFound(adapter: BluetoothAdapter, devices: List<BluetoothDevice>) {
                super.onFound(adapter, devices)
                val device = devices[0]
                if (isRemote(device.name) && !SystemUtils.isConnected(device) && !isConnecting) {
                    isConnecting = true
                    mCacheDevice = device
                }
            }
        })
    }



    override fun onDestroy() {
        super.onDestroy()
        activity!!.sendBroadcast(Intent(IntentAction.ACTION_SHOW_REMOTE_DIALOG))
        if (connRunnable != null) connRunnable!!.interrupt()
        if (runnable != null) runnable!!.interrupt()
        BluetoothScannerUtils.removeListener(activity)
        exec.shutdownNow()
    }

    override fun initView() {
        mBind.next.visibility = if (arguments!!.getBoolean(
                Atts.SHOW_BOTTOM,
                true
            )
        ) View.VISIBLE else View.GONE

        mBind.next.setOnClickListener { v: View -> this.onClick(v) }

    }

    override fun initdata() {
        check()
    }





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBind.next.apply { post { requestFocus() } }
    }

    override fun onClick(v: View) {
        if (v == mBind.next) {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.main_browse_fragment, newInstance()).addToBackStack(null).commit()
        }
    }



    private fun check() {
        if (mBluetoothAdapter == null) return
        if (runnable != null) runnable!!.interrupt()
        runnable = object : MyRunnable() {
            override fun run() {
                while (!isInterrupt) {
                    if (!isAdded) return

                    var name: String? = ""
                    for (device in mBluetoothAdapter!!.bondedDevices) {
                        if (!device.bluetoothClass.doesClassMatch(BluetoothClass.PROFILE_HID) || !isRemote(
                                device.name
                            )
                        ) continue
                        isConnected = SystemUtils.isConnected(device)
                        name = device.name
                        break
                    }

                    if (isConnected) isConnecting = false
                    if (mCacheDevice != null) {
                        val profile = BluetoothScannerUtils.getProfileProxy(activity)
                        if (profile != null) {
                            name = mCacheDevice!!.name
                            syncIcon(1, name)
                            val success = AndroidSystem.connect(activity, profile, mCacheDevice!!)
                            mCacheDevice = null
                            isConnecting = false
                            if (!success) {
                                syncIcon(2, name)
                                SystemClock.sleep(3000)
                            } else {
                                SystemClock.sleep(1000)
                                continue
                            }
                        }
                    }
                    syncIcon(if (isConnected) 3 else 0, name)
                    SystemClock.sleep(300)
                }
            }
        }
        exec.execute(runnable)
    }

    private fun isRemote(name: String): Boolean {
        return "BlueX-Remote1" == name
    }

    fun syncIcon(connectedType: Int, name: String?) {
        uiHandler!!.post(Runnable {
            if (!isAdded) return@Runnable
            //mConnectedIconView.setImageResource(connected ? R.drawable.baseline_bluetooth_connected_100 : R.drawable.baseline_bluetooth_searching_100);
            mBind.progressBar.visibility = if (connectedType == 0) View.VISIBLE else View.GONE
            if (connectedType == 1) {
                mBind.searchMessage.text = getString(R.string.pairing_mark, name)
            } else if (connectedType == 2) {
                mBind.searchMessage.text = getString(R.string.pairing_fail, name)
            } else if (connectedType == 3) {
                mBind.searchMessage.text = getString(R.string.pairing_success, name)
            } else {
                mBind.searchMessage.text = ""
            }
            //if (isConnecting) mConnectedIconView.setImageResource(R.drawable.baseline_router_100);
        })
    }

    private fun connectDevice(device: BluetoothDevice) {
        if (isConnecting) return
        isConnecting = true
        if (connRunnable != null) connRunnable!!.interrupt()
        connRunnable = object : MyRunnable() {
            override fun run() {
                try {
                    var count = 0
                    //AndroidSystem.connect(getActivity(), device);
                    while (!isInterrupt) {
                        SystemClock.sleep(300)
                        if (SystemUtils.isConnected(device) || count++ >= 10) {
                            return
                        }
                    }
                } finally {
                    isConnecting = false
                }
            }
        }
        exec.execute(connRunnable)
    }

    companion object {
        fun newInstance(useControl: Boolean): RemoteControlFragment {
            val args = Bundle()
            args.putBoolean(Atts.SHOW_BOTTOM, useControl)
            val fragment = RemoteControlFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
