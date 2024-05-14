package com.shudong.lib_base.pop

import android.content.Context
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.core.CenterPopupView
import com.shudong.lib_base.R
import com.shudong.lib_base.databinding.DialogCenterCommonBinding
import com.shudong.lib_base.ext.appendHeight
import com.shudong.lib_base.ext.changeBG
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.colorValue
import com.shudong.lib_base.ext.dimenValue
import com.shudong.lib_base.ext.no
import com.shudong.lib_base.ext.yes
import com.thumbsupec.lib_base.ext.*
import kotlinx.coroutines.Job

class CenterCommonDialog private constructor(context: Context, val setup: (CenterCommonDialog.() -> Unit)? = null) :
    CenterPopupView(context) {
    private val mBind: DialogCenterCommonBinding

    init {
        addInnerContent()
        mBind = DialogCenterCommonBinding.bind(popupImplView)
    }

    override fun getImplLayoutId(): Int {
        return R.layout.dialog_center_common
    }

     companion object{
         var dialog:CenterCommonDialog?=null
         @JvmStatic
         fun newInstance(context: Context,  setup: (CenterCommonDialog.() -> Unit)? = null):CenterCommonDialog?{
             dialog?.dismiss()
             dialog = null
             dialog = CenterCommonDialog(context,setup)
             return dialog
         }


         @JvmStatic
         fun dissDialog(){
             dialog?.dismiss()
             dialog = null
         }
     }

    fun titleTv() = mBind.tvTitle
     fun setTitleTxt(title:String){
         mBind.tvTitle.text = title
     }


    override fun initPopupContent() {
        super.initPopupContent()
        setup?.invoke(this)
        mBind.apply {
            tvContent.isVisible = !isShowTitleOnly
            isShowTitleOnly.no {
                llDialog.post {
                    llDialog.appendHeight(com.shudong.lib_dimen.R.dimen.qb_px_150.dimenValue())
                }
            }
            isSingleBtn.yes { tvCancle.isVisible = false }

                title.observe(this@CenterCommonDialog){
                    mBind.tvTitle.text = it
                }

            content.observe(this@CenterCommonDialog){
                mBind.tvContent.text = it
            }


            tvConfirm.changeBG(confirmSolidColor)
            tvContent.setTextColor(contentTextColor.colorValue())
            cancleText.isNotEmpty().yes { tvCancle.text = cancleText }
            confirmText.isNotEmpty().yes { tvConfirm.text = confirmText }



            tvCancle.clickNoRepeat {
                clickCancle?.invoke()
                dissDialog()
            }
            tvConfirm.clickNoRepeat {
                clickConfirm?.invoke()
                dissDialog()
            }
        }

    }



    // 是否只显示标题
    var isShowTitleOnly = true
    // 标题
    var title = MutableLiveData<String>()
    // 内容
    var content = MutableLiveData<String>()
    // 取消按钮文本
    var cancleText = ""
    // 确认按钮文本
    var confirmText = ""
     var isSingleBtn = false
     var confirmSolidColor = com.shudong.lib_res.R.color.color_585CE5
     var contentTextColor = com.shudong.lib_res.R.color.color_888D9D

    private var clickCancle: (() -> Unit)? = null
    private var clickConfirm: (() -> Unit)? = null

    private var countDownTick: ((time:Int) -> Unit)? = null
    private var countDownFinish: (() -> Unit)? = null

    /**
     * 取消事件回调
     */
    fun clickCancle(listener: (() -> Unit)?) {
        clickCancle = listener
    }

    /**
     * 确认事件回调
     */
    fun clickConfirm(listener: (() -> Unit)?) {
        clickConfirm = listener
    }



}