package com.chihihx.launcher.ext

import android.app.Activity
import android.content.Context
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbManager
import com.chihihx.launcher.utils.SDCardUtil

fun Activity.isUDisk() = run{
    var isExistUDisk = false
    val deviceHashMap =
        (this.getSystemService(Context.USB_SERVICE) as UsbManager).deviceList
    if (deviceHashMap.size > 0) {

        deviceHashMap.values.forEach {
            if (it.interfaceCount > 0) {
                val usbInterface = it.getInterface(0)
                when (usbInterface.interfaceClass) {
                    UsbConstants.USB_CLASS_MASS_STORAGE -> {
                        isExistUDisk = true
                        isExistUDisk

                    }

                    else -> {
                        isExistUDisk = false
                        isExistUDisk
                    }
                }
            }
        }
        isExistUDisk

    } else {
        isExistUDisk = false
        isExistUDisk

    }
}

fun Activity.isSDCard() = run{
    val pair = SDCardUtil.getStora(this)

    var isContainSD = false
    isContainSD = pair.first

    isContainSD


}