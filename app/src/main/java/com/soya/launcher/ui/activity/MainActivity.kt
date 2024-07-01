package com.soya.launcher.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_AVR_POWER
import android.view.WindowManager
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.shudong.lib_base.base.BaseVMActivity
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.ACTIVE_SUCCESS
import com.shudong.lib_base.ext.IS_MAIN_CANBACK
import com.shudong.lib_base.ext.d
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.obseverLiveEvent
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.replaceFragment
import com.shudong.lib_base.ext.yes
import com.soya.launcher.App
import com.soya.launcher.BuildConfig
import com.soya.launcher.R
import com.soya.launcher.config.Config
import com.soya.launcher.databinding.ActivityMainBinding
import com.soya.launcher.enums.IntentAction
import com.soya.launcher.ext.switchFragment
import com.soya.launcher.fragment.AuthFragment
import com.soya.launcher.manager.PreferencesManager
import com.soya.launcher.rk3326.ReflectUtils
import com.soya.launcher.utils.GlideUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : BaseVMActivity<ActivityMainBinding,BaseViewModel>() {
    private var canBackPressed = true


    private val homeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
                val reason = intent.getStringExtra("reason")
                if (reason == "homekey") {
                    // 处理 Home 键按下的逻辑
                    sendBroadcast(Intent(IntentAction.ACTION_RESET_SELECT_HOME))
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_HOME||keyCode==KEYCODE_AVR_POWER) {
            // 在这里处理 Home 按键按下事件
            // 如果需要特殊处理，需要使用 System UI 相关权限
            // 处理 Home 键按下的逻辑
            sendBroadcast(Intent(IntentAction.ACTION_RESET_SELECT_HOME))
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

   /* override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            // 在这里处理 Home 按键抬起事件
            // 如果需要特殊处理，需要使用 System UI 相关权限
            return true
        }
        return super.onKeyUp(keyCode, event)
    }*/

    override fun initBeforeContent() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    override fun initView() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                // Simulate a long running task
                setBG(mBind.ivBg)
            }

            withContext(Dispatchers.Main) {
                // Simulate a long running task
                registerReceiver(homeReceiver, IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
                commit()
            }
        }

        //val model = ReflectUtils.getProperty("persist.vendor.launcher.platform","")
        //"当前设备型号是====$model".d("zy1998")
    }



     fun setBG(view: ImageView?) {
        var id = if (Config.COMPANY == 0) R.drawable.wallpaper_22 else R.drawable.wallpaper_1
        for (wallpaper in App.WALLPAPERS) {
            if (wallpaper.id == PreferencesManager.getWallpaper()) {
                id = wallpaper.picture
                break
            }
        }
        GlideUtils.bindBlur(this, view, id)
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(homeReceiver)
    }

    override fun initObserver() {
        this.obseverLiveEvent<Boolean>(ACTIVE_SUCCESS){
            commit()
        }

        this.obseverLiveEvent<Boolean>(IS_MAIN_CANBACK){
            Log.d("zy1996", "switchFragment: 收到false了====")
            it.yes {
                canBackPressed = true
            }.otherwise {
                canBackPressed = false
            }
        }
    }


    private fun commit() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_browse_fragment, getFragment())
            .commitAllowingStateLoss()
    }

    fun switchAuthFragment() {
        canBackPressed = true
        replaceFragment(AuthFragment.newInstance(),R.id.main_browse_fragment)

    }


    fun getFragment(): Fragment  = switchFragment()

    override fun onBackPressed() {
        if (canBackPressed) {
            super.onBackPressed()
        } else {
            sendBroadcast(Intent(IntentAction.ACTION_RESET_SELECT_HOME))
        }

    }
}
