package com.shudong.lib_res.chat

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.blankj.utilcode.util.KeyboardUtils
import com.drake.brv.utils.grid
import com.drake.brv.utils.setup
import com.shudong.lib_res.R
import com.shudong.lib_res.databinding.ViewChatInputBinding
import com.shudong.lib_res.view.clickNoRepeat
import com.shudong.lib_res.view.drawTop
import com.shudong.lib_res.view.otherwise
import com.shudong.lib_res.view.yes
import java.io.IOException

class ChatInputView(context:Context?, attrs: AttributeSet? ) : RelativeLayout(context, attrs) {
    var mBindding: ViewChatInputBinding =
        ViewChatInputBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        initView()
        initOnClick()
    }

    var isShowFuncPanel = false
    @SuppressLint("ClickableViewAccessibility")
    private fun initOnClick() {
        mBindding.apply {
            ivEmoj.clickNoRepeat {
                vpEmojPanel.isVisible = true
                prepareEmoticon()
            }

            ivMoreFunc.clickNoRepeat {
                isShowFuncPanel.yes {
                    rvFunc.isVisible = false
                    isShowFuncPanel = false
                    viewLine.isVisible = false
                    KeyboardUtils.showSoftInput(mBindding.etMsg)
                    etMsg.postDelayed({

                        requestFocus()
                    }, 100)
                }.otherwise {
                    KeyboardUtils.hideSoftInput(mBindding.etMsg)
                    postDelayed({
                        rvFunc.isVisible = true
                        isShowFuncPanel = true
                        viewLine.isVisible = true
                    }, 100)


                }
            }

            ivVoice.clickNoRepeat {
                isInputStatus.yes {
                    etMsg.isVisible = false
                    voicePanel.isVisible = true
                    isInputStatus = false
                    ivVoice.setImageResource(R.drawable.icon_chat_input)
                }.otherwise {
                    etMsg.isVisible = true
                    voicePanel.isVisible = false
                    isInputStatus = true
                    ivVoice.setImageResource(R.drawable.icon_microphone)
                }

            }

            voicePanel.setOnTouchListener { view, motionEvent ->
                when(motionEvent.action){
                    MotionEvent.ACTION_DOWN->{
                        isHoleVoiceBtn = true
                        mTouchY1 = motionEvent.y
                        startVoiceRecord?.invoke(false)
                    }
                    MotionEvent.ACTION_UP->{
                        isHoleVoiceBtn = false
                        mTouchY2 = motionEvent.y
                        (mTouchY1 - mTouchY2 > MIN_CANCEL_DISTANCE).yes {
                            //松开取消发送
                            endVoiceRecord?.invoke(true)
                        }.otherwise {
                            endVoiceRecord?.invoke(false)
                        }
                    }
                    MotionEvent.ACTION_MOVE->{
                        isHoleVoiceBtn = true
                        mTouchY3 = motionEvent.y
                        (mTouchY1 - mTouchY3 > MIN_CANCEL_DISTANCE).yes {
                            startVoiceRecord?.invoke(true)
                        }.otherwise {
                            startVoiceRecord?.invoke(false)
                        }
                    }
                }
                true
            }
        }
    }

    var mTouchY1 = 0f
    var mTouchY2 = 0f
    var mTouchY3 = 0f
    private val MIN_CANCEL_DISTANCE = 100f

    private var isInputStatus = true
    private var isHoleVoiceBtn = false
    private fun initView() {

        mBindding.apply {
            rvFunc.grid(4).setup {
                addType<Pair<String, Int>>(R.layout.item_chat_func)
                onBind {
                    val tvFunc = findView<TextView>(R.id.tv_func)
                    val entity = _data as Pair<String, Int>
                    tvFunc.text = entity.first
                    tvFunc.drawTop(context, entity.second)
                }
                R.id.tv_func.onFastClick {
                    when ((_data as Pair<String, Int>).first) {
                        "定位" -> {
                            clickSendLocation?.invoke()
                        }
                        "语音电话"->{
                            clickVoiceCall?.invoke()
                        }
                        "照片"->{
                            clickOpenAlbum?.invoke()
                        }
                        "拍照"->{
                            clickOpenCamera?.invoke()
                        }

                        else -> {}
                    }
                }

            }.models = arrayListOf(
                Pair("照片", R.drawable.icon_photo),
                Pair("拍照", R.drawable.icon_camera),
                Pair("语音电话", R.drawable.icon_voice),
                Pair("定位", R.drawable.icon_location),
                Pair("名片", R.drawable.icon_name_card),
                Pair("文件", R.drawable.icon_file_upload),
            )
            etMsg.setOnEditorActionListener { textView, actionId, keyEvent ->
                when(actionId){
                    EditorInfo.IME_ACTION_SEND->{
                        //点击了发送
                        sendTxtMsg?.invoke(textView.text.toString())
                        etMsg.setText("")
                    }
                }
                true
            }
        }
    }


    private val emsNumOfEveryFragment = 20 //每页的表情数量
    var activity:AppCompatActivity?=null

    fun bindActivity(act:AppCompatActivity){
        activity = act
    }

    private fun prepareEmoticon() {

        val fragmentNum: Int
        /*获取ems文件夹有多少个表情  减1 是因为有个删除键
                         每页20个表情  总共有length个表情
                         先判断能不能整除  判断是否有不满一页的表情
		 */
        /*获取ems文件夹有多少个表情  减1 是因为有个删除键
                         每页20个表情  总共有length个表情
                         先判断能不能整除  判断是否有不满一页的表情
		 */
        val emsTotalNum: Int = getSizeOfAssetsCertainFolder("ems") - 1 //表情的数量(除去删除按钮)

        if (emsTotalNum % emsNumOfEveryFragment == 0) {
            fragmentNum = emsTotalNum / emsNumOfEveryFragment
        } else {
            fragmentNum = emsTotalNum / emsNumOfEveryFragment + 1
        }

        val mViewPagerAdapter = EmotionAdapter(activity?.supportFragmentManager, fragmentNum)
        mBindding.vpEmojPanel.setAdapter(mViewPagerAdapter)
        mBindding.vpEmojPanel.currentItem = 0

        val globalOnItemClickListener = GlobalOnItemClickManager.instance
        globalOnItemClickListener?.attachToEditText(mBindding.etMsg)

    }

    private fun getSizeOfAssetsCertainFolder(folderName: String): Int {
        var size = 0
        try {

            activity?.let {
                size = it.assets.list(folderName)?.size?:0
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return size
    }

    /*private fun prepareEmoticon() {
        if (mBindding.emojPanel == null) return
        for (i in 0..4) {
            val linearLayout = LinearLayout(context)
            linearLayout.layoutParams =
                LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f)
            for (j in 0..6) {
                try {
                    val am = context.assets
                    val index = 7 * i + j
                    val `is` = am.open(String.format("emoticon/%d.gif", index))
                    val bitmap = BitmapFactory.decodeStream(`is`)
                    val matrix = Matrix()
                    val width = bitmap.width
                    val height = bitmap.height
                    matrix.postScale(3.5f, 3.5f)
                    val resizedBitmap = Bitmap.createBitmap(
                        bitmap, 0, 0,
                        width, height, matrix, true
                    )
                    val image = ImageView(context)
                    image.setImageBitmap(resizedBitmap)
                    image.layoutParams = LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    linearLayout.addView(image)
                    image.setOnClickListener {
                        val content = index.toString()
                        val str = SpannableString(index.toString())
                        val span = ImageSpan(context, resizedBitmap, ImageSpan.ALIGN_CENTER)
                        str.setSpan(span, 0, content.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        mBindding.etMsg.append(str)
                    }
                    `is`.close()
                } catch (e: IOException) {
                }
            }
            mBindding.emojPanel.addView(linearLayout)
        }
        //isEmoticonReady = true
    }*/


    private var clickSendLocation: (() -> Unit)? = null
    fun clickSendLocation(listener: (() -> Unit)?) {
        clickSendLocation = listener
    }

    /**
     *
     * 语音电话
     */
    private var clickVoiceCall: (() -> Unit)? = null
    fun clickVoiceCall(listener: (() -> Unit)?) {
        clickVoiceCall = listener
    }

    private var startVoiceRecord: ((isCancle:Boolean) -> Unit)? = null
    fun startVoiceRecord(listener: ((isCancle:Boolean) -> Unit)?) {
        startVoiceRecord = listener
    }

    private var endVoiceRecord: ((isCancle:Boolean) -> Unit)? = null
    fun endVoiceRecord(listener: ((isCancle:Boolean) -> Unit)?) {
        endVoiceRecord = listener
    }

    private var sendTxtMsg: ((txtMsg:String) -> Unit)? = null
    fun sendTxtMsg(listener: ((txtMsg:String) -> Unit)?) {
        sendTxtMsg = listener
    }

    private var clickOpenAlbum: (() -> Unit)? = null
    fun clickOpenAlbum(listener: (() -> Unit)?) {
        clickOpenAlbum = listener
    }

    private var clickOpenCamera: (() -> Unit)? = null
    fun clickOpenCamera(listener: (() -> Unit)?) {
        clickOpenCamera = listener
    }


}