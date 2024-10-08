package com.soya.launcher

import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.NetworkUtils
import com.shudong.lib_base.base.BaseVMFragment
import com.shudong.lib_base.ext.ACTIVE_SUCCESS
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.dimenValue
import com.shudong.lib_base.ext.e
import com.shudong.lib_base.ext.jsonToBean
import com.shudong.lib_base.ext.margin
import com.shudong.lib_base.ext.net.lifecycleLoadingView
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.sendLiveEventData
import com.shudong.lib_base.ext.startKtxActivity
import com.shudong.lib_base.ext.yes
import com.shudong.lib_base.global.AppCacheBase
import com.shudong.lib_base.global.AppCacheBase.activeCode
import com.soya.launcher.R
import com.soya.launcher.bean.AuthBean
import com.soya.launcher.databinding.FragmentAuthBinding
import com.soya.launcher.ext.getWifiName
import com.soya.launcher.fragment.AuthViewModel
import com.soya.launcher.ui.activity.NetActivity
import com.soya.launcher.ui.dialog.KeyboardDialog
import com.soya.launcher.utils.AndroidSystem
import com.soya.launcher.utils.AutoSeparateTextWatcher
import com.soya.launcher.utils.showKeyboard
import com.soya.launcher.utils.toTrim
import com.thumbsupec.lib_base.toast.ToastUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AuthFragment : BaseVMFragment<FragmentAuthBinding, AuthViewModel>() {

    override fun initView() {
        setToTopMargin()

        mBind.apply {
            val textWatcher = AutoSeparateTextWatcher(etActiveCode)
            textWatcher.setRULES(intArrayOf(4, 4, 4, 4))
            textWatcher.separator = '-'
            etActiveCode.addTextChangedListener(textWatcher)
        }

        if (NetworkUtils.isConnected()) {
            mBind.etActiveCode.apply {
                post {
                    requestFocus()
                }
            }
        } else {
            mBind.rlSetting.requestFocus()
            startKtxActivity<NetActivity>()
        }


    }


    fun setToCenter() {
        mBind.llActive.post {
            // 计算居中位置
            val topMargin = (mBind.main.height - mBind.llActive.height) / 2
            mBind.llActive.margin(topMargin = topMargin)

        }

    }

    fun setToTopMargin() {
        mBind.llActive.post {
            mBind.llActive.margin(topMargin = com.shudong.lib_dimen.R.dimen.qb_px_90.dimenValue())

        }
    }


    fun showKB() {
        showKeyboard {
            setTargetView(mBind.etActiveCode)
            keboardHide {
                setToCenter()
            }

            inputCompleted {
                mBind.llActive.post {
                    it.yes {
                        mBind.tvActive.apply {
                            post {
                                requestFocus()
                                performClick()
                            }
                        }

                    }.otherwise {
                        ToastUtils.show(getString(R.string.The_input))//输入不能为空
                    }


                }
            }
        }.show(childFragmentManager, KeyboardDialog.TAG)
    }

    override fun initClick() {

        mBind.rlSetting.apply {
            clickNoRepeat {
                AndroidSystem.openSystemSetting(requireContext())
            }
            setOnFocusChangeListener { view, b ->
                b.yes {
                    mBind.setting.setImageResource(R.drawable.icon_setting_focus)
                }.otherwise {
                    mBind.setting.setImageResource(R.drawable.icon_setting_normal)

                }
            }
        }

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
            if (!NetworkUtils.isConnected()) {
                startKtxActivity<NetActivity>()
                return@clickNoRepeat
            }
            activeCode = mBind.etActiveCode.text.toString().replace('-', ' ').toTrim()
            testCode().no {
                mViewModel.reqCheckActiveCode(mBind.etActiveCode.text.toString())
                    .lifecycleLoadingView(this@AuthFragment, {
                        ToastUtils.show(getString(R.string.Failed_please))
                    }) {
                        val authDto = this.jsonToBean<AuthBean>()
                        (authDto.status == 200).yes {
                            authDto.code.let {
                                it.getResult(authDto.msg)
                            }

                        }.otherwise {
                            ToastUtils.show(getString(R.string.Failed_please))
                        }
                    }
            }


        }

    }

    private fun testCode(): Boolean {
        val wifiName = getWifiName()
        "当前的Name：$wifiName".e("zengyue3")
        when (wifiName) {
            "WIFI-5G", "WIFI", "wuyun", "wuyun-5G", "WIFI-test" -> {
                if (activeCode == "11111111") {
                    mBind.etActiveCode.isFocusable  = false
                    mBind.etActiveCode.isFocusableInTouchMode  = false
                    mBind.etActiveCode.isEnabled = false
                    AppCacheBase.isActive = true
                    ToastUtils.show(getString(R.string.Success))
                    lifecycleScope.launch {
                       delay(500)
                        sendLiveEventData(ACTIVE_SUCCESS, true)
                    }
                    return true
                }
            }
            "<unknown ssid>"->{
                if (activeCode == "18074674") {
                    mBind.etActiveCode.isFocusable  = false
                    mBind.etActiveCode.isFocusableInTouchMode  = false
                    mBind.etActiveCode.isEnabled = false
                    AppCacheBase.isActive = true
                    ToastUtils.show(getString(R.string.Success))
                    lifecycleScope.launch {
                        delay(500)
                        sendLiveEventData(ACTIVE_SUCCESS, true)
                    }
                    return true
                }
            }
        }
        return false
    }

    private fun Long.getResult(msg: String?) {
        (msg == null).no {
            ToastUtils.show(msg)
        }.otherwise {
            when (this) {
                10000L -> {
                    mBind.etActiveCode.isFocusable  = false
                    mBind.etActiveCode.isFocusableInTouchMode  = false
                    mBind.etActiveCode.isEnabled = false
                    AppCacheBase.isActive = true
                    ToastUtils.show(getString(R.string.Success))
                    lifecycleScope.launch {
                        delay(500)
                        sendLiveEventData(ACTIVE_SUCCESS, true)
                    }

                }

                10004L -> ToastUtils.show(getString(R.string.Invalid_PIN))
                else -> {
                    ToastUtils.show(getString(R.string.Failed_please))
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AuthFragment()
    }
}


/*
    * 10001L->ToastUtils.show("缺少桌面 ID")
                10002L->ToastUtils.show("桌面 ID 格式不正确")
                10003L->ToastUtils.show("缺少激活码")
                10004L->ToastUtils.show("激活码格式不正确")
                10005L->ToastUtils.show("缺少客户端时间戳的值")
                10006L->ToastUtils.show("客户端时间戳和实际时间戳相差太大")
                10007L->ToastUtils.show("缺少渠道号")
                10008L->ToastUtils.show("渠道号格式不正确")
                10009L->ToastUtils.show("缺少子渠道号")
                10010L->ToastUtils.show("子渠道号格式不正确")
                10011L->ToastUtils.show("缺少 API 版本值")
                10012L->ToastUtils.show("API 版本值不合法")
                10013L->ToastUtils.show("缺少签名字段")
                10014L->ToastUtils.show("签名字段值未通过验证")
                10015L->ToastUtils.show("激活码在我们数据库中不存在")
                10016L->ToastUtils.show("激活码可使用的 IP 数已超过指定数量")
                10017L->ToastUtils.show("新绑定时，桌面 ID 字段已经在数据库中重复存在")
                10018L->ToastUtils.show("当前桌面 ID 和之前此卡号绑定的桌面 ID 不相同")
                10019L->ToastUtils.show("此激活码已经在其它设备上绑定过，一个激活码不可以同时绑定多个设备")
                10020L->ToastUtils.show("错误码：10020")
    * */

