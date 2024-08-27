package com.soya.launcher.ui.activity

import android.view.KeyEvent
import androidx.fragment.app.Fragment
import com.jeremyliao.liveeventbus.LiveEventBus
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.replaceFragment
import com.shudong.lib_base.ext.sendLiveEventData
import com.soya.launcher.BaseWallpaperActivity
import com.soya.launcher.R
import com.soya.launcher.databinding.ActivityMainBinding
import com.soya.launcher.ui.fragment.BluetoothFragment
import com.soya.launcher.ui.fragment.ChooseGradientFragment.Companion.newInstance

class ChooseGradientActivity : BaseWallpaperActivity<ActivityMainBinding,BaseViewModel>() {

    override fun initView() {
        replaceFragment(newInstance(), R.id.main_browse_fragment)
    }



}
