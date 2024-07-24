package com.soya.launcher.ui.activity

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import com.jeremyliao.liveeventbus.LiveEventBus
import com.shudong.lib_base.ext.sendLiveEventData
import com.shudong.lib_base.ext.startKtxActivity
import com.soya.launcher.R
import com.soya.launcher.ui.fragment.ChooseGradientFragment.Companion.newInstance

class ChooseGradientActivity : AbsActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 启动 GradientActivity
        startKtxActivity<GradientActivity>()

        // 如果需要结束当前 Activity，调用 finish()
        finish()
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun getContainerId(): Int {
        return R.id.main_browse_fragment
    }

    override fun getFragment(): Fragment {
        return newInstance()
    }


}
