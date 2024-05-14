package com.shudong.lib_base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.shudong.lib_base.base.BaseActivity
import com.shudong.lib_base.ext.yes

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/10/25 10:22
 * @PACKAGE_NAME:  com.thumbsupec.lib_base.base
 */
class ActivityLifecycleKtxCallback: Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        (activity is BaseActivity<*>).yes {
            addActivity(activity)
        }

    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        removeActivity(activity)
    }
}