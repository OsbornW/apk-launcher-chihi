package com.shudong.lib_base

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process.myPid
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import java.util.*

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/10/25 10:20
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.ext
 */

val currentProcessName: String?
    get() {
        val pid = myPid()
        val mActivityManager = appContext.getSystemService(
            Context.ACTIVITY_SERVICE
        ) as ActivityManager
        for (appProcess in mActivityManager.runningAppProcesses) {
            if (appProcess.pid == pid) {
                return appProcess.processName
            }
        }
        return null
    }

/**
 * 获取packageName
 */
fun getPackageName(context: Context): String {
    try {
        val pi = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU).yes {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(0)
            )
        }.otherwise {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(context.packageName, 0)
        }
        return pi.packageName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return ""
}

/**
 * 获取versionName
 */
fun getAppVersion(context: Context): String {
    try {
        val pi = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU).yes {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(0)
            )
        }.otherwise {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(context.packageName, 0)
        }
        return pi.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return ""
}

/**
 * 方法描述：判断某一应用是否正在运行
 *
 * @param context     上下文
 * @param packageName 应用的包名
 * @return true 表示正在运行，false 表示没有运行
 */
fun String.isAppRunning(packageName: String): Boolean {
    val am = appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    @Suppress("DEPRECATION") val list = am.getRunningTasks(100)
    if (list.size <= 0) {
        return false
    }
    for (info in list) {
        if (info.baseActivity!!.packageName == packageName) {
            return true
        }
    }
    return false
}


/**
 *
 * 通过判断手机里的所有进程是否有这个App的进程
 * 从而判断该App是否有打开
 */
 fun isAppRunning(): Boolean {
//通过ActivityManager我们可以获得系统里正在运行的activities
//包括进程(Process)等、应用程序/包、服务(Service)、任务(Task)信息。
    val am = appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val processInfos = am.runningAppProcesses
    val mainProcessName = getPackageName(appContext)

    //获取本App的唯一标识
    val myPid: Int = myPid()
    //利用一个增强for循环取出手机里的所有进程
    for (info in processInfos) {
        //通过比较进程的唯一标识和包名判断进程里是否存在该App
        if (info.pid == myPid && mainProcessName == info.processName) {
            return true
        }
    }
    return false
}


val activityList = LinkedList<Activity>()

//app当前显示的Activity
val currentActivity: Activity? get() = if (activityList.isNullOrEmpty()) null else activityList.last

/**
 * 添加Activity入栈
 * @param activity Activity
 */
fun addActivity(activity: Activity) {
    activityList.add(activity)
}

/**
 * 关闭Activity出栈
 * @param activity Activity
 */
fun finishActivity(activity: Activity) {
    if (!activity.isFinishing) {
        activity.finish()
    }
    activityList.remove(activity)
}

/**
 * 从栈移除activity 不会finish
 * @param activity Activity
 */
fun removeActivity(activity: Activity) {
    activityList.remove(activity)
}

/**
 * 关闭Activity出栈
 * @param cls Class<*>
 */
fun finishActivity(cls: Class<*>) {
    if (activityList.isNullOrEmpty()) return
    val index = activityList.indexOfFirst { it.javaClass == cls }
    if (index == -1) return
    if (!activityList[index].isFinishing) {
        activityList[index].finish()
    }
    activityList.removeAt(index)
}

/**
 * 关闭所有的Activity 全部出栈
 */
fun finishAllActivity() {
    activityList.forEach {
        if (!it.isFinishing) {
            it.finish()
        }
    }
    activityList.clear()
}

fun finishAllActivityExceptMain() {
    val it = activityList.iterator()
    while (it.hasNext()) {
        val next = it.next()
        if (!next.isFinishing) {
            if (!next.localClassName.contains("MainActivity")) {
                next.finish()
                it.remove()
            }
        }
    }
}

fun finishAllActivityExceptLogin() {
    val it = activityList.iterator()
    while (it.hasNext()) {
        val next = it.next()
        if (!next.isFinishing) {
            if (!next.localClassName.contains("EmailLoginActivity")) {
                next.finish()
                it.remove()
            }
        }
    }
}