package com.soya.launcher.fragment

import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.stringValue
import com.shudong.lib_base.ext.yes
import com.soya.launcher.BaseWallPaperFragment
import com.soya.launcher.R
import com.soya.launcher.databinding.FragmentAutoResponseBinding
import com.soya.launcher.ext.SYSTEM_PROPERTY_AUTO_RESPONSE
import com.soya.launcher.ext.setSystemPropertyValueBoolean
import com.soya.launcher.ext.systemPropertyValueBoolean
import com.soya.launcher.ui.fragment.ChooseGradientFragment

class AutoResponseFragment:BaseWallPaperFragment<FragmentAutoResponseBinding,BaseViewModel>() {

    var isResponseOpen = false

    override fun initView() {

         isResponseOpen = SYSTEM_PROPERTY_AUTO_RESPONSE.systemPropertyValueBoolean()
        isResponseOpen.yes {
            //当前自动响应是打开的
            mBind.tvStatus.text = R.string.open_response.stringValue()
        }.otherwise {
            //当前自动响应是关闭的
            mBind.tvStatus.text = R.string.close_response.stringValue()
        }
    }

    override fun initClick() {
        mBind.apply {
            llResponse.clickNoRepeat {
                isResponseOpen.yes {
                    //进行关闭
                    SYSTEM_PROPERTY_AUTO_RESPONSE.setSystemPropertyValueBoolean(false){
                        it.yes { mBind.tvStatus.text = R.string.close_response.stringValue() }
                    }
                }.otherwise {
                    //进行打开
                    SYSTEM_PROPERTY_AUTO_RESPONSE.setSystemPropertyValueBoolean(true){
                        it.yes { mBind.tvStatus.text = R.string.open_response.stringValue() }
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): AutoResponseFragment {
            val fragment = AutoResponseFragment()
            return fragment
        }
    }

}