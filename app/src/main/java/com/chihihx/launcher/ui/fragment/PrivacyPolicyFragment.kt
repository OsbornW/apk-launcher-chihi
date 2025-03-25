package com.chihihx.launcher.ui.fragment

import com.chihihx.launcher.R
import com.chihihx.launcher.cache.AppCache
import com.chihihx.launcher.databinding.FragmentPrivacypolicyBinding
import com.chihihx.launcher.ext.navigateTo
import com.chihihx.launcher.product.base.product
import com.shudong.lib_base.BaseWebActivity
import com.shudong.lib_base.base.BaseVMFragment
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.startKtxActivity

class PrivacyPolicyFragment : BaseVMFragment<FragmentPrivacypolicyBinding, BaseViewModel>() {
    override fun initView() {
        mBind.apply {
            tvAgree.requestFocus()
            tvPrivacyPolicy.clickNoRepeat {
                //点击隐私政策跳转
                startKtxActivity<BaseWebActivity>()
            }
            tvAgree.clickNoRepeat {
                //点击同意
                product.switchFragment()?.let {
                    AppCache.isPrivacyPolicyAgreed = true
                    requireActivity().supportFragmentManager.navigateTo(
                        R.id.main_browse_fragment,
                        it
                    )
                    //requireActivity().replaceFragment(it, R.id.main_browse_fragment)
                }

            }
            tvDisagree.clickNoRepeat {
                //点击不同意
            }
        }
    }
}