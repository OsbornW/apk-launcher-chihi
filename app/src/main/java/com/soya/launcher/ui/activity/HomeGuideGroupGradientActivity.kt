package com.soya.launcher.ui.activity

import android.view.KeyEvent
import androidx.fragment.app.Fragment
import com.shudong.lib_base.ext.FOCUS_BACK
import com.shudong.lib_base.ext.sendLiveEventData
import com.soya.launcher.R
import com.soya.launcher.ui.fragment.HomeGuideGroupGradientFragment

class HomeGuideGroupGradientActivity : AbsActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun getContainerId(): Int {
        return R.id.main_browse_fragment
    }

    override fun getFragment(): Fragment {
        return HomeGuideGroupGradientFragment.newInstance()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {

        when (event.keyCode) {

            KeyEvent.KEYCODE_BACK -> {
                if (event.action == KeyEvent.ACTION_UP) {
                    sendLiveEventData(FOCUS_BACK,true)
                }

                return true
            }
        }

        return super.dispatchKeyEvent(event)
    }

}
