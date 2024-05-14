package com.soya.launcher.ui.activity

import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.ScreenUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.shudong.lib_base.base.BaseVMActivity
import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.d
import com.shudong.lib_base.ext.dimenValue
import com.shudong.lib_base.ext.margin
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.startKtxActivity
import com.shudong.lib_base.ext.yes
import com.soya.launcher.BuildConfig
import com.soya.launcher.databinding.ActivityAuthBinding
import com.soya.launcher.ui.dialog.KeyboardDialog
import com.soya.launcher.utils.AutoSeparateTextWatcher
import com.soya.launcher.utils.md5
import com.soya.launcher.utils.showKeyboard
import com.soya.launcher.utils.showLoadingView
import com.soya.launcher.utils.showLoadingViewDismiss
import com.soya.launcher.utils.toTrim
import com.thumbsupec.lib_base.toast.ToastUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Response
import org.json.JSONObject


class AuthActivity : BaseVMActivity<ActivityAuthBinding, BaseViewModel>() {


    override fun initView() {
        setToTopMargin()

        mBind.apply {
            val textWatcher = AutoSeparateTextWatcher(etActiveCode)
            textWatcher.setRULES(intArrayOf(4, 4, 4,4))
            textWatcher.separator = '-'
            etActiveCode.addTextChangedListener(textWatcher)
        }

    }

    fun setToCenter(){
        mBind.llActive.post {
            // 计算居中位置
            val topMargin = (mBind.main.height - mBind.llActive.height) / 2
            mBind.llActive.margin(topMargin = topMargin)

        }

    }

    fun setToTopMargin(){
        mBind.llActive.post {
            mBind.llActive.margin(topMargin = com.shudong.lib_dimen.R.dimen.qb_px_90.dimenValue())

        }
    }


    fun showKB(){
        showKeyboard{
            setTargetView(mBind.etActiveCode)
            keboardHide {
                setToCenter()
            }

            inputCompleted {
                mBind.llActive.post {
                   // mBind.etActiveCode.clearFocus()
                    mBind.tvActive.requestFocus()
                    Thread.sleep(500)
                    mBind.tvActive.performClick()
                }
            }
        }.show(supportFragmentManager, KeyboardDialog.TAG)
    }
    override fun initClick() {

        mBind.etActiveCode.clickNoRepeat {
            setToTopMargin()
            showKB()
        }

        mBind.etActiveCode.setOnFocusChangeListener { view, b ->

            b.yes {
               showKB()
            }
        }
        mBind.tvActive.setOnFocusChangeListener { view, b ->
            b.yes {
                setToCenter()
            }.otherwise {
                setToTopMargin()
            }
        }

        mBind.tvActive.clickNoRepeat {
            showLoadingView("激活中...")
            val uniqueID = DeviceUtils.getUniqueDeviceId().subSequence(0,DeviceUtils.getUniqueDeviceId().length-1)
            //激活码
            val activeCode = mBind.etActiveCode.text.toString().replace('-',' ').toTrim()
            val versionValue = 1001
            // 渠道ID
            val chanelId = BuildConfig.CHANNEL

            val childChanel = "S10001"
            // 时间戳
            val time = System.currentTimeMillis()/1000
            // 密码盐
            val pwd = "TKPCpTVZUvrI"
            // 待加密的字符串（(d+e+c+b+a+f+密码盐）
            val toBeEncryptedString = "$chanelId$childChanel$versionValue$activeCode$uniqueID$time$pwd"
            // 对字符串进行MD5加密
            val md5String = toBeEncryptedString.md5()
            Log.d("zy1996", "加密前：$toBeEncryptedString===加密后的MD5是：${md5String}")


            val params = mapOf(
                //唯一ID
                "a" to uniqueID,
                // 激活码
                "b" to activeCode.toLong(),
                // API 版本值
                "c" to versionValue,
                //渠道号
                "d" to chanelId,
                // 子渠道号
                "e" to childChanel,
                // 当前时间戳（秒）
                "f" to time,
                // 签名值  md5(d+e+c+b+a+f+密码盐)
                "s" to md5String,
            )

            val jsonObject = JSONObject(params)

            OkGo.post("https://api.freedestop.com/u/client")
                .tag(this)
                .upJson(jsonObject.toString())
                .execute(object : StringCallback() {
                    override fun onSuccess(s: String?, call: Call?, response: Response?) {
                        //上传成功
                        Thread.sleep(3000)
                        showLoadingViewDismiss()
                        ToastUtils.show("恭喜您激活成功")
                        lifecycleScope.launch {
                            delay(2000)
                            startKtxActivity<MainActivity>()
                            finish()
                        }

                        Log.d("zy1996", "请求成功${s}====${response.toString()}")

                    }

                    override fun onError(call: Call?, response: Response?, e: Exception?) {
                        showLoadingViewDismiss()
                        ToastUtils.show("激活失败")
                        Log.d("zy1996", "请求失败，原因：${e.toString()}====${response.toString()}")

                    }
                })

        }
    }
}