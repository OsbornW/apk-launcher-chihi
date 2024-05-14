package com.thumbsupec.lib_base.toast

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import com.shudong.lib_base.toast.config.IToast
import com.shudong.lib_base.toast.config.IToastStrategy
import com.shudong.lib_base.toast.config.IToastStyle
import java.lang.ref.WeakReference
import java.lang.reflect.InvocationTargetException

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:   Toast默认处理器
 * @Author:  zengyue
 * @Date:  2022/10/29
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.toast
 */
class ToastStrategy : IToastStrategy {
    /** 应用上下文  */
    private var mApplication: Application? = null

    /** Activity 栈管理  */
    private var mActivityStack: ActivityStack? = null

    /** Toast 对象  */
    private var mToastReference: WeakReference<IToast?>? = null

    /** Toast 样式  */
    private var mToastStyle: IToastStyle<*>? = null

    /** 最新的文本  */
    @Volatile
    private var mLatestText: CharSequence? = null
    override fun registerStrategy(application: Application) {
        mApplication = application
        mActivityStack = ActivityStack.register(application)
    }

    override fun bindStyle(style: IToastStyle<*>?) {
        mToastStyle = style
    }

    override fun createToast(application: Application): IToast {
        val foregroundActivity = mActivityStack!!.foregroundActivity
        val toast: IToast
        toast = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            Settings.canDrawOverlays(application)
        ) {
            // 如果有悬浮窗权限，就开启全局的 Toast
            WindowToast(application)
        } else if (foregroundActivity != null) {
            // 如果没有悬浮窗权限，就开启一个依附于 Activity 的 Toast
            ActivityToast(foregroundActivity)
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            // 处理 Android 7.1 上 Toast 在主线程被阻塞后会导致报错的问题
            SafeToast(application)
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
            !areNotificationsEnabled(application)
        ) {
            // 处理 Toast 关闭通知栏权限之后无法弹出的问题
            // 通过查看和对比 NotificationManagerService 的源码
            // 发现这个问题已经在 Android 10 版本上面修复了
            // 但是 Toast 只能在前台显示，没有通知栏权限后台 Toast 仍然无法显示
            // 并且 Android 10 刚好禁止了 Hook 通知服务
            // 已经有通知栏权限，不需要 Hook 系统通知服务也能正常显示系统 Toast
            NotificationToast(application)
        } else {
            SystemToast(application)
        }

        // targetSdkVersion >= 30 的情况下在后台显示自定义样式的 Toast 会被系统屏蔽，并且日志会输出以下警告：
        // Blocking custom toast from package com.xxx.xxx due to package not in the foreground
        // targetSdkVersion < 30 的情况下 new Toast，并且不设置视图显示，系统会抛出以下异常：
        // java.lang.RuntimeException: This Toast was not created with Toast.makeText()
        if (toast is CustomToast || Build.VERSION.SDK_INT < Build.VERSION_CODES.R || application.applicationInfo.targetSdkVersion < Build.VERSION_CODES.R) {
            toast.view = mToastStyle!!.createView(application)
            toast.setGravity(mToastStyle!!.gravity, mToastStyle!!.xOffset, mToastStyle!!.yOffset)
            toast.setMargin(mToastStyle!!.horizontalMargin, mToastStyle!!.verticalMargin)
        }
        return toast
    }

    override fun showToast(text: CharSequence, delayMillis: Long) {
        mLatestText = text
        HANDLER.removeCallbacks(mShowRunnable)
        // 延迟一段时间之后再执行，因为在没有通知栏权限的情况下，Toast 只能显示当前 Activity
        // 如果当前 Activity 在 showToast 之后立马进行 finish 了，那么这个时候 Toast 可能会显示不出来
        // 因为 Toast 会显示在销毁 Activity 界面上，而不会显示在新跳转的 Activity 上面
        HANDLER.postDelayed(mShowRunnable, delayMillis + DELAY_TIMEOUT)
    }

    override fun cancelToast() {
        HANDLER.removeCallbacks(mCancelRunnable)
        HANDLER.post(mCancelRunnable)
    }

    /**
     * 显示任务
     */
    private val mShowRunnable = Runnable {
        var toast: IToast? = null
        if (mToastReference != null) {
            toast = mToastReference!!.get()
        }
        toast?.cancel()
        toast = createToast(mApplication!!)
        // 为什么用 WeakReference，而不用 SoftReference ？
        // https://github.com/getActivity/ToastUtils/issues/79
        mToastReference = WeakReference(toast)
        toast.duration = getToastDuration(mLatestText)
        toast.setText(mLatestText)
        toast.show()
    }

    /**
     * 取消任务
     */
    private val mCancelRunnable = Runnable {
        var toast: IToast? = null
        if (mToastReference != null) {
            toast = mToastReference!!.get()
        }
        if (toast == null) {
            return@Runnable
        }
        toast.cancel()
    }

    /**
     * 获取 Toast 显示时长
     * 字符长度大于 20 就弹Long toast
     */
    protected fun getToastDuration(text: CharSequence?): Int {
        return if (text!!.length > 20) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    }

    /**
     * 是否有通知栏权限
     */
    @SuppressLint("PrivateApi")
    protected fun areNotificationsEnabled(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getSystemService(NotificationManager::class.java)
                .areNotificationsEnabled()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 参考 Support 库中的方法： NotificationManagerCompat.from(context).areNotificationsEnabled()
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            return try {
                val method = appOps.javaClass.getMethod(
                    "checkOpNoThrow",
                    Integer.TYPE, Integer.TYPE, String::class.java
                )
                val field = appOps.javaClass.getDeclaredField("OP_POST_NOTIFICATION")
                val value = field[Int::class.java] as Int
                method.invoke(
                    appOps, value, context.applicationInfo.uid,
                    context.packageName
                ) as Int == AppOpsManager.MODE_ALLOWED
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
                true
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
                true
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
                true
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
                true
            } catch (e: RuntimeException) {
                e.printStackTrace()
                true
            }
        }
        return true
    }

    companion object {
        /** Handler 对象  */
        private val HANDLER = Handler(Looper.getMainLooper())

        /** 延迟时间  */
        private const val DELAY_TIMEOUT = 200
    }
}