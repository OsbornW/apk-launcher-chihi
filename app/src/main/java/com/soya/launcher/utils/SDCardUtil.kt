package com.soya.launcher.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.storage.StorageManager
import android.util.Log
import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException


object SDCardUtil {
    private const val TAG = "SDCardUtil"
    fun getStorageType(pContext: Context): HashMap<*, *> {
        val map = HashMap<String, String>()
        map.clear()
        val buffer = StringBuffer()
        val storageManager = pContext.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        try {
            //获取类型是U盘还是SD卡
            val getVolumes = storageManager.javaClass.getMethod("getVolumes")
            val invokeVolumes = getVolumes.invoke(storageManager) as List<Any>
            val volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo")
            val getPath = volumeInfoClazz.getMethod("getPath")
            val getDisk = volumeInfoClazz.getMethod("getDisk")
            val getBrand = volumeInfoClazz.getMethod("getDescription")
            val diskInfoClazz = Class.forName("android.os.storage.DiskInfo")
            val getDescription = diskInfoClazz.getMethod("getDescription")
            for (i in invokeVolumes.indices) {
                val volumeInfo = invokeVolumes[i]
                val invokePath = getPath.invoke(volumeInfo) as File
                val brand = getBrand.invoke(volumeInfo) as String
                 Log.d("zy1997", "getStorageType: 当前的品牌是====="+brand)
                Log.e(TAG, "invokePath:" + invokePath.path)
                if ("/data" == invokePath.path || "/storage/emulated" == invokePath.path) continue
                val invokeDiskInfo = getDisk.invoke(volumeInfo)
                val description = getDescription.invoke(invokeDiskInfo) as String
                Log.e(TAG, "getDescription:$description")
                // map.put(invokePath.getPath(), description);
                map[brand] = description
            }
            return map
        } catch (e: Exception) {
        } finally {
        }
        return HashMap<Any?, Any?>()
    }



    fun getStoragePath(mContext: Context): String {
        var targetpath = ""

        val mStorageManager = mContext
            .getSystemService(Context.STORAGE_SERVICE) as StorageManager
        var storageVolumeClazz: Class<*>? = null
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume")
            val getVolumeList = mStorageManager.javaClass.getMethod("getVolumeList")
            val getPath = storageVolumeClazz.getMethod("getPath")
            val result = getVolumeList.invoke(mStorageManager)
            val length: Int = java.lang.reflect.Array.getLength(result)
            val getUserLabel = storageVolumeClazz.getMethod("getUserLabel")
            for (i in 0 until length) {
                val storageVolumeElement: Any = java.lang.reflect.Array.get(result, i)
                val userLabel = getUserLabel.invoke(storageVolumeElement) as String
                val path = getPath.invoke(storageVolumeElement) as String
                if (!path.contains("/storage/emulated/0")&&!userLabel.contains("disk")
                    &&!userLabel.contains("U")) {
                    targetpath = userLabel
                }
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return targetpath
    }

    @SuppressLint("PrivateApi", "SoonBlockedPrivateApi")
     fun getStora(context: Context): Pair<Boolean,String> {
        var path = ""
        val mStorageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val volumeInfoClazz: Class<*>
        val diskInfoClaszz: Class<*>
        try {
            volumeInfoClazz = Class.forName("android.os.storage.VolumeInfo")
            diskInfoClaszz = Class.forName("android.os.storage.DiskInfo")
            val StorageManager_getVolumes =
                Class.forName("android.os.storage.StorageManager").getMethod("getVolumes")
            val VolumeInfo_GetDisk = volumeInfoClazz.getMethod("getDisk")
            val VolumeInfo_GetPath = volumeInfoClazz.getMethod("getPath")

            val DiskInfo_IsUsb = diskInfoClaszz.getMethod("isUsb")
            val DiskInfo_IsSd = diskInfoClaszz.getMethod("isSd")
            val List_VolumeInfo = (StorageManager_getVolumes.invoke(mStorageManager) as List<Any>)


            for (i in List_VolumeInfo.indices) {
                val volumeInfo = List_VolumeInfo[i]

                val diskInfo = VolumeInfo_GetDisk.invoke(volumeInfo) ?: continue
                val sd = DiskInfo_IsSd.invoke(diskInfo) as Boolean
                val usb = DiskInfo_IsUsb.invoke(diskInfo) as Boolean
                val file = VolumeInfo_GetPath.invoke(volumeInfo) as File

                if (sd) {
                     return Pair(true,file.name)
                    break
                }

            }
        } catch (e: java.lang.Exception) {
           // YYLog.print(TAG, "[——————— ——————— Exception:" + e.message + "]")
            e.printStackTrace()
        }
        return Pair(false,"")
    }


}
