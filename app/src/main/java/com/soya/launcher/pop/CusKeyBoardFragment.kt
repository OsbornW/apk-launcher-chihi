package com.soya.launcher.pop

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.drake.brv.utils.addModels
import com.drake.brv.utils.grid
import com.drake.brv.utils.setup
import com.shudong.lib_base.ext.clickNoRepeat
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import com.soya.launcher.R
import com.soya.launcher.bean.KeyBoardDto
import com.soya.launcher.bean.KeyBoardDto.Companion.KEYBOARD_TYPE_BACK_LETTER
import com.soya.launcher.bean.KeyBoardDto.Companion.KEYBOARD_TYPE_CASE
import com.soya.launcher.bean.KeyBoardDto.Companion.KEYBOARD_TYPE_DEL
import com.soya.launcher.bean.KeyBoardDto.Companion.KEYBOARD_TYPE_HALF_CHAR
import com.soya.launcher.bean.KeyBoardDto.Companion.KEYBOARD_TYPE_LEFT_ARROW
import com.soya.launcher.bean.KeyBoardDto.Companion.KEYBOARD_TYPE_LETTER
import com.soya.launcher.bean.KeyBoardDto.Companion.KEYBOARD_TYPE_NUM
import com.soya.launcher.bean.KeyBoardDto.Companion.KEYBOARD_TYPE_OK
import com.soya.launcher.bean.KeyBoardDto.Companion.KEYBOARD_TYPE_RIGHT_ARROW
import com.soya.launcher.bean.KeyBoardDto.Companion.KEYBOARD_TYPE_SPACE
import com.soya.launcher.bean.KeyBoardDto.Companion.KEYBOARD_TYPE_SWITCH_Special_Characters
import com.soya.launcher.databinding.DialogCusKeyboardBinding
import com.soya.launcher.databinding.ItemCusKeyboardBinding
import com.soya.launcher.ext.deletePreviousChar
import com.soya.launcher.ext.moveCursorLeft
import com.soya.launcher.ext.moveCursorRight
import com.soya.launcher.net.viewmodel.KeyBoardViewModel
import com.soya.launcher.utils.toTrim
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

/**
 *
 * 自定义键盘
 */
class CusKeyBoardFragment : DialogFragment() {

    private var _binding: DialogCusKeyboardBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: KeyBoardViewModel by inject()
    private var editText:EditText?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCusKeyboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    lateinit var keyBoardDtos: MutableList<KeyBoardDto>
    val editTextData = MutableLiveData<EditText>()
    var isUpper = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextData.observe(this){
            editText = it
        }
        binding.rvNumber.apply {
            grid(3).setup {
                addType<KeyBoardDto>(R.layout.item_cus_keyboard)
                onBind {
                    val item = getModel<KeyBoardDto>()
                    itemView.clickNoRepeat {
                        when (item.keyboardType) {
                            KEYBOARD_TYPE_NUM -> {
                                // 字符输入
                                //inputChangeAction?.invoke(item.keyboardChar!!)
                                editText?.append(item.keyboardChar!!)
                            }
                        }
                    }
                }
            }.models

        }





        binding.rvLetter.apply {

            grid(9).setup {
                itemAnimator = null
                addType<KeyBoardDto>(R.layout.item_cus_keyboard)
                initLayoutManager()
                onBind {
                    val itemBinding = getBinding<ItemCusKeyboardBinding>()
                    val item = getModel<KeyBoardDto>()
                    itemView.clickNoRepeat {
                        when (item.keyboardType) {
                            KEYBOARD_TYPE_LETTER, KEYBOARD_TYPE_HALF_CHAR -> {
                                //字符输入
                                //inputChangeAction?.invoke(item.keyboardChar!!)
                                editText?.append(item.keyboardChar!!)
                            }

                            KEYBOARD_TYPE_SWITCH_Special_Characters -> {
                                //切换到特殊字符
                                keyBoardDtos = mViewModel.addHalfWidth()
                                models = keyBoardDtos
                                binding.rvLetter.apply { post { requestFocus() } }
                            }

                            KEYBOARD_TYPE_CASE -> {
                                //切换大小写
                                isUpper = !isUpper
                                mViewModel.switchLowerUpper(
                                    models as MutableList<KeyBoardDto>,
                                    isUpper
                                ) {
                                    notifyItemChanged(it)
                                }

                            }
                            KEYBOARD_TYPE_DEL->{
                                //删除字符
                                //charDeleteAction?.invoke()
                                editText?.deletePreviousChar()
                            }
                            KEYBOARD_TYPE_OK->{
                                //完成
                                dismiss()
                                inputCompleteAction?.invoke(editText?.text.toString().toTrim().length>1)
                            }
                            KEYBOARD_TYPE_SPACE->{
                                // 空格
                                //inputSpaceAction?.invoke()
                                editText?.append(" ")
                            }
                            KEYBOARD_TYPE_LEFT_ARROW->{
                                // 左箭头
                                //leftArrowAction?.invoke()
                                editText?.moveCursorLeft()
                            }
                            KEYBOARD_TYPE_RIGHT_ARROW->{
                                // 右箭头
                                //rightArrowAction?.invoke()
                                editText?.moveCursorRight()
                            }
                            KEYBOARD_TYPE_BACK_LETTER->{
                                // 返回字母键盘
                                keyBoardDtos = mViewModel.addLowerCase(isUpper)
                                models = keyBoardDtos
                                binding.rvLetter.apply { post { requestFocus() } }
                            }
                        }
                    }
                }
            }.models
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                keyBoardDtos = mViewModel.addLowerCase(false)
            }
            withContext(Dispatchers.Main){
                binding.rvNumber.addModels(mViewModel.addNumChar())
            }
            withContext(Dispatchers.Main){
                binding.rvLetter.addModels(keyBoardDtos )
            }
        }


        setKeyBoardLitsener()
        binding.rvLetter.apply { post { requestFocus() } }
    }

    private var inputCompleteAction: ((isNotEmpty:Boolean) -> Unit)? = null  //
    /**
     *
     */
    fun inputCompleteAction(action: (isNotEmpty:Boolean) -> Unit) {
        inputCompleteAction = action
    }

    private var keboardHide: (() -> Unit)? = null

    /**
     * 键盘隐藏
     */
    fun keboardHide(listener: (() -> Unit)?) {
        keboardHide = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        keboardHide?.invoke()
    }

    private fun setKeyBoardLitsener() {
        dialog?.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP) {
                (event.unicodeChar == 0).yes {
                    // 返回 0 表示该按键没有可显示的字符
                    when (keyCode) {
                        KeyEvent.KEYCODE_DEL -> {
                            //charDeleteAction?.invoke()
                            editText?.deletePreviousChar()
                        }

                        else -> {}
                    }
                }.otherwise {
                    //表示有字符的（需要单独处理一下空格，因为空格默认执行了enter键的功能）
                    when (keyCode) {
                        KeyEvent.KEYCODE_SPACE -> {
                            //inputSpaceAction?.invoke()
                            editText?.append(" ")
                        }

                        else -> {
                            editText?.append(event.displayLabel.toString())
                            //inputChangeAction?.invoke(event.displayLabel.toString())
                        }
                    }
                }
            }
            false
        }
    }

    /**
     * 设置布局管理器
     */
    private fun initLayoutManager() {
        val gridLayoutManager = binding.rvLetter.layoutManager as GridLayoutManager
        gridLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val item = keyBoardDtos[position]
                return when {
                    // "OK" 键跨4列显示
                    item.keyboardType == KEYBOARD_TYPE_OK -> 2
                    item.keyboardType == KEYBOARD_TYPE_SWITCH_Special_Characters -> 2
                    item.keyboardType == KEYBOARD_TYPE_SPACE -> 2
                    // 其他按键均占1列
                    else -> 1
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 背景透明
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
            setDimAmount(0f)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
