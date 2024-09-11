package com.soya.launcher.ui.fragment

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.VerticalGridView
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.appContext
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.adapter.BluetoothItemAdapter
import com.soya.launcher.bean.BluetoothItem
import com.soya.launcher.databinding.FragmentBluetoothBinding
import com.soya.launcher.utils.BluetoothScannerUtils

class BluetoothFragment : BaseWallPaperFragment<FragmentBluetoothBinding,BaseViewModel>(),
    BluetoothItemAdapter.Callback {
    private var mAdapter: BluetoothItemAdapter? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BluetoothScannerUtils.startListening(activity, object : BluetoothScannerUtils.Listener() {
            override fun onFound(adapter: BluetoothAdapter, devices: List<BluetoothDevice>) {
                super.onFound(adapter, devices)

                val deviceSet = adapter.bondedDevices
                val map: MutableMap<String, BluetoothDevice> = HashMap()
                for (device in deviceSet) {
                    if (!TextUtils.isEmpty(device.name)) map[device.name] = device
                }
                for (device in devices) {
                    if (!TextUtils.isEmpty(device.name)) map[device.name] = device
                }

                val strings: MutableMap<String, BluetoothItem> = HashMap()
                for (item in mAdapter!!.dataList) strings[item.device.name] = item
                val items: MutableList<BluetoothItem> = ArrayList()

                for ((key, value) in map) {
                    if (!strings.containsKey(key)) items.add(BluetoothItem(value))
                    else strings[key]!!.device = value
                }
                mAdapter!!.add(items)
                mAdapter!!.notifyItemChanged(0, mAdapter!!.itemCount)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        BluetoothScannerUtils.removeListener(activity)
    }


    override fun initView() {
        mAdapter = BluetoothItemAdapter(requireContext(), LayoutInflater.from(appContext), ArrayList(), this)
        mBind.content.adapter = mAdapter
        mBind.content.setColumnWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
    }


    companion object {
        fun newInstance(): BluetoothFragment {
            val args = Bundle()

            val fragment = BluetoothFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onClick(bean: BluetoothItem?) {

    }
}
