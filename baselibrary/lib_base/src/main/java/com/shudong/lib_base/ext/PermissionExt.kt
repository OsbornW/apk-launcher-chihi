package com.shudong.lib_base.ext

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionMediator
import com.permissionx.guolindev.PermissionX
import com.shudong.lib_base.currentActivity

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/11/14 09:05
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.ext
 */


/**
 *
 * 请求位置权限
 */
fun <T> requestLocationPermission(activityOrFragment: T, isGranted: () -> Unit) {

    var permission: PermissionMediator? = null
    if (activityOrFragment is FragmentActivity) {
        permission = PermissionX.init(activityOrFragment)
    } else if (activityOrFragment is Fragment) {
        permission = PermissionX.init(activityOrFragment)
    }

    isGPSOpen(appContext).yes {
        permission
            ?.permissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
            ?.onExplainRequestReason { scope, deniedList -> }
            ?.request { allGranted, grantedList, deniedList ->
                if (allGranted) {

                    isGranted.invoke()

                } else {
                    //showPermissionRequest()
                    //  无权限
                }
            }
    }.otherwise {
        // GPS  没打开

    }


}


/**
 *
 * GPS 开关是否打开的
 */
fun isGPSOpen(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
    val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
    val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    return gps && network
}


/**
 *
 * 请求读写权限
 */
fun <T> requestWRPermission(activityOrFragment: T, isGranted: () -> Unit) {

    var permission: PermissionMediator? = null
    if (activityOrFragment is FragmentActivity) {
        permission = PermissionX.init(activityOrFragment)
    } else if (activityOrFragment is Fragment) {
        permission = PermissionX.init(activityOrFragment)
    }

    permission
        ?.permissions(
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU).yes {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
                Manifest.permission.READ_MEDIA_IMAGES
            }.otherwise {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            }

        )
        ?.onExplainRequestReason { scope, deniedList ->
        }
        ?.request { allGranted, grantedList, deniedList ->
            if (allGranted) {
                isGranted.invoke()
            } else {
                //showPermissionRequest(com.thumbsupec.lib_res.R.string.permission_txt_06.stringValue())
            }
        }
}

/**
 *
 * 请求相机权限
 */
fun <T> requestCameraPermission(activityOrFragment: T, isGranted: () -> Unit) {

    var permission: PermissionMediator? = null
    if (activityOrFragment is FragmentActivity) {
        permission = PermissionX.init(activityOrFragment)
    } else if (activityOrFragment is Fragment) {
        permission = PermissionX.init(activityOrFragment)
    }

    permission
        ?.permissions(
            Manifest.permission.CAMERA,
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU).yes {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
                Manifest.permission.READ_MEDIA_IMAGES
            }.otherwise {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            }
        )
        ?.onExplainRequestReason { scope, deniedList ->
        }
        ?.request { allGranted, grantedList, deniedList ->
            if (allGranted) {
                isGranted.invoke()
            } else {
                //showPermissionRequest(com.thumbsupec.lib_res.R.string.permission_txt_07.stringValue())
            }
        }
}

/**
 *
 * 是否有蓝牙权限
 */
fun isHasBlePerssion() = run {
    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S).yes {
        (PermissionX.isGranted(
            appContext,
            Manifest.permission.BLUETOOTH_CONNECT
        ) &&
                PermissionX.isGranted(
                    appContext,
                    Manifest.permission.BLUETOOTH_SCAN
                )).yes {
            true
        }.otherwise {
            false
        }
    }.otherwise {
        (PermissionX.isGranted(
            appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) &&
                PermissionX.isGranted(
                    appContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )).yes {
            true
        }.otherwise {
            false
        }
    }
}


/**
 *
 * 请求通知权限(适配Android 13)
 */
fun <T> requestNotificationPermission(activityOrFragment: T, isGranted: () -> Unit) {

    var permission: PermissionMediator? = null
    if (activityOrFragment is FragmentActivity) {
        permission = PermissionX.init(activityOrFragment)
    } else if (activityOrFragment is Fragment) {
        permission = PermissionX.init(activityOrFragment)
    }

    permission
        ?.permissions(
            PermissionX.permission.POST_NOTIFICATIONS
        )
        ?.onExplainRequestReason { scope, deniedList ->
        }
        ?.request { allGranted, grantedList, deniedList ->
            if (allGranted) {
                isGranted.invoke()
            } else {
                //showPermissionRequest(com.thumbsupec.lib_res.R.string.permission_txt_08.stringValue())
            }
        }
}


fun <T> requestFloatWindowPermission(activityOrFragment: T, isGranted: () -> Unit) {

    var permission: PermissionMediator? = null
    if (activityOrFragment is FragmentActivity) {
        permission = PermissionX.init(activityOrFragment)
    } else if (activityOrFragment is Fragment) {
        permission = PermissionX.init(activityOrFragment)
    }

    permission
        ?.permissions(
            Manifest.permission.SYSTEM_ALERT_WINDOW
        )
        ?.onExplainRequestReason { scope, deniedList ->
        }
        ?.request { allGranted, grantedList, deniedList ->
            if (allGranted) {
                isGranted.invoke()
            } else {
                showErrorToast("确实悬浮窗权限")
                //showPermissionRequest(com.thumbsupec.lib_res.R.string.permission_txt_08.stringValue())
            }
        }
}


fun <T> requestAudioPermission(activityOrFragment: T, isGranted: () -> Unit) {


    var permission: PermissionMediator? = null
    if (activityOrFragment is FragmentActivity) {
        permission = PermissionX.init(activityOrFragment)
    } else if (activityOrFragment is Fragment) {
        permission = PermissionX.init(activityOrFragment)
    }


    PermissionX.isGranted(
        appContext,
        Manifest.permission.RECORD_AUDIO
    ).yes {
        isGranted.invoke()
    }.otherwise {
        permission
            ?.permissions(
                Manifest.permission.RECORD_AUDIO
            )
            ?.onExplainRequestReason { scope, deniedList ->
            }
            ?.request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                   // isGranted.invoke()
                } else {
                    showErrorToast("缺少录音权限")
                    //showPermissionRequest(com.thumbsupec.lib_res.R.string.permission_txt_08.stringValue())
                }
            }
    }


}