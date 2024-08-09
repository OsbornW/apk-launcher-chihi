package com.soya.launcher.ui.activity

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.blankj.utilcode.util.NetworkUtils
import com.shudong.lib_base.base.BaseVMActivity
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.replaceFragment
import com.shudong.lib_base.ext.yes
import com.soya.launcher.databinding.ActivityNetBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NetActivity : BaseVMActivity<ActivityNetBinding,BaseViewModel>() {

    override fun initView() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) { // 当生命周期至少为 STARTED 时执行
                while (true) {
                    withContext(Dispatchers.IO) {
                        (NetworkUtils.isConnected() && NetworkUtils.isAvailable()).yes {
                            //"当前网络可用1"
                            withContext(Dispatchers.Main) {
                                finish()
                            }
                        }
                    }
                    delay(1000) // 每秒更新一次
                }
            }
        }
    }

    override fun initClick() {
        mBind.apply {
            tvCancle.clickNoRepeat {
                finish()
            }
            tvOk.clickNoRepeat {
                startActivity(Intent(this@NetActivity, WifiListActivity::class.java))

            }
        }
    }

}