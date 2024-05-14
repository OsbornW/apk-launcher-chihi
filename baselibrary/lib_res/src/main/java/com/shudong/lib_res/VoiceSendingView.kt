package com.shudong.lib_res

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.shudong.lib_res.databinding.ViewVoiceSendBinding

/**
 * 发送语音提示控件
 */
class VoiceSendingView(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
    private var frameAnimation: AnimationDrawable

    var mBind: ViewVoiceSendBinding

    init {
        mBind = ViewVoiceSendBinding.inflate(LayoutInflater.from(context), this, true)
        mBind.ivTip.setBackgroundResource(R.drawable.animation_voice_input)
        frameAnimation = mBind.ivTip.background as AnimationDrawable
    }

    fun showRecording() {
        mBind.ivTip.setBackgroundResource(R.drawable.animation_voice_input)
        frameAnimation = mBind.ivTip.background as AnimationDrawable
        frameAnimation.start()
        mBind.tvTip.text = "手指上划，取消发送"
    }

    fun showCancel() {
        mBind.ivTip.setBackgroundResource(R.drawable.icon_cancle_voice)
        frameAnimation.stop()
        mBind.tvTip.text = "松开手指，取消发送"
    }

    fun release() {
        mBind.ivTip.setBackgroundResource(R.drawable.icon_voice_tooshort)
        frameAnimation.stop()

        mBind.tvTip.text = "说话时间太短"
    }
}