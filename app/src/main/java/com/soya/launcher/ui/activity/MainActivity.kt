package com.soya.launcher.ui.activity

import android.content.Intent
import androidx.fragment.app.Fragment
import com.shudong.lib_base.base.BaseVMActivity
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.ACTIVE_SUCCESS
import com.shudong.lib_base.ext.obseverLiveEvent
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import com.shudong.lib_base.global.AppCacheBase
import com.soya.launcher.R
import com.soya.launcher.config.Config
import com.soya.launcher.databinding.ActivityMainBinding
import com.soya.launcher.enums.IntentAction
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
    }

    private fun commit() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_browse_fragment, getFragment())
            .commitAllowingStateLoss()
    }

     fun getFragment(): Fragment {

         (Config.COMPANY == 0).yes {
             AppCacheBase.isActive.yes {
                 /*return if (PreferencesManager.isGuide() == 0 && Config.COMPANY == 0) {
                     canBackPressed = true
                     WelcomeFragment.newInstance()
                 } else {
                     canBackPressed = false
                     newInstance()
                 }*/
                 canBackPressed = false
                 return newInstance()
             }.otherwise {
                 return AuthFragment.newInstance()
             }
         }.otherwise {
             return if (PreferencesManager.isGuide() == 0 && Config.COMPANY == 0) {
                 canBackPressed = true
                 WelcomeFragment.newInstance()
             } else {
                 canBackPressed = false
                 newInstance()
             }
         }


    }

    override fun onBackPressed() {
        if (canBackPressed) super.onBackPressed() else sendBroadcast(Intent(IntentAction.ACTION_RESET_SELECT_HOME))
    }
}
