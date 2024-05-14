package com.thumbsupec.lib_base.toast

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:   Activity生命周期监控
 * @Author:  zengyue
 * @Date:  2022/10/29
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.toast
 */
internal class ActivityStack : Application.ActivityLifecycleCallbacks {
    /** 前台 Activity 对象  */
    var foregroundActivity: Activity? = null
        private set

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {
        foregroundActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        if (foregroundActivity !== activity) {
            return
        }
        foregroundActivity = null
    }

    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}

    companion object {
        /**
         * 注册
         */
        fun register(application: Application): ActivityStack {
            val lifecycle = ActivityStack()
            application.registerActivityLifecycleCallbacks(lifecycle)
            return lifecycle
        }
    }
}