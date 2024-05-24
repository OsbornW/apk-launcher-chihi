package com.soya.launcher.ui.activity

import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import com.shudong.lib_base.base.BaseVMActivity
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.ACTIVE_SUCCESS
import com.shudong.lib_base.ext.IS_MAIN_CANBACK
import com.shudong.lib_base.ext.d
import com.shudong.lib_base.ext.obseverLiveEvent
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import com.shudong.lib_base.global.AppCacheBase
import com.soya.launcher.BuildConfig
import com.soya.launcher.R
import com.soya.launcher.config.Config
import com.soya.launcher.databinding.ActivityMainBinding
import com.soya.launcher.enums.IntentAction
import com.soya.launcher.ext.switchFragment
import com.soya.launcher.manager.PreferencesManager
import com.soya.launcher.ui.fragment.MainFragment.Companion.newInstance
import com.soya.launcher.ui.fragment.WelcomeFragment

class MainActivity : BaseVMActivity<ActivityMainBinding,BaseViewModel>() {
    private var canBackPressed = true


    override fun initView() {
        commit()
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

     fun getFragment(): Fragment  = switchFragment()

    override fun onBackPressed() {
        Log.d("zy1996", "onBackPressed: 是否可以返回？"+canBackPressed)
        if (canBackPressed) {
            super.onBackPressed()
        } else {
            sendBroadcast(Intent(IntentAction.ACTION_RESET_SELECT_HOME))
        }
    }
}
