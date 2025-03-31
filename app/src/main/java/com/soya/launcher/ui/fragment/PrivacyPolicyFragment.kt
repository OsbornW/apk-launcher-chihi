package com.soya.launcher.ui.fragment
import android.app.ActivityManager
import android.content.Context
import com.soya.launcher.R
import com.soya.launcher.cache.AppCache
import com.soya.launcher.databinding.FragmentPrivacypolicyBinding
import com.soya.launcher.ext.navigateTo
import com.soya.launcher.product.base.product
import com.shudong.lib_base.BaseWebActivity
import com.shudong.lib_base.base.BaseVMFragment
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.appContext
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
                }

            }
            tvDisagree.clickNoRepeat {
                //点击不同意
                forceStopApp(requireContext(), appContext.packageName)
            }
        }
    }
    fun forceStopApp(context: Context, packageName: String) {
        try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager::class.java.getMethod("forceStopPackage", String::class.java)
                .invoke(activityManager, packageName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}