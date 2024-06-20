package com.soya.launcher.ext

import android.app.Activity
import android.content.Context
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbManager

fun Activity.isUDisk() = run{
    var isExistUDisk = false
    val deviceHashMap =
        (this.getSystemService(Context.USB_SERVICE) as UsbManager).deviceList
    if (deviceHashMap.size > 0) {

        deviceHashMap.values.forEach {
            if (it.getInterfaceCount() > 0) {
                val usbInterface = it.getInterface(0)
                when (usbInterface.interfaceClass) {
                    UsbConstants.USB_CLASS_MASS_STORAGE -> {
                        isExistUDisk = true
                        isExistUDisk

                    }

                    else -> {
                        isExistUDisk = true
                        isExistUDisk
                    }
                }
            }
        }
        isExistUDisk


        //mBind.rlUdisk.visibility = View.VISIBLE
    } else {
        isExistUDisk = false
        isExistUDisk

    }
}