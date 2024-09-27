package com.soya.launcher.pop

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
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
import com.soya.launcher.net.viewmodel.KeyBoardViewModel
import org.koin.android.ext.android.inject

/**
 *
 * 自定义键盘
 */
class CusKeyBoardFragment : DialogFragment() {

    private var _binding: DialogCusKeyboardBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: KeyBoardViewModel by inject()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCusKeyboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    lateinit var keyBoardDtos: MutableList<KeyBoardDto>
    var isUpper = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        keyBoardDtos = mViewModel.addLowerCase(false)
        binding.rvNumber.apply {
            grid(3).setup {
                addType<KeyBoardDto>(R.layout.item_cus_keyboard)
                onBind {
                    val item = getModel<KeyBoardDto>()
                    itemView.clickNoRepeat {
                        when (item.keyboardType) {
                            KEYBOARD_TYPE_NUM -> {
                                // 字符输入
                                inputChangeAction?.invoke(item.keyboardChar!!)
                            }
                        }
                    }
                }
            }.models = mViewModel.addNumChar()

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
                                inputChangeAction?.invoke(item.keyboardChar!!)
                            }

                            KEYBOARD_TYPE_SWITCH_Special_Characters -> {
                                //切换到特殊字符
                                keyBoardDtos = mViewModel.addHalfWidth()
                                models = keyBoardDtos
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
                                charDeleteAction?.invoke()
                            }
                            KEYBOARD_TYPE_OK->{
                                //完成
                                dismiss()
                                inputCompleteAction?.invoke()
                            }
                            KEYBOARD_TYPE_SPACE->{
                                // 空格
                                inputSpaceAction?.invoke()
                            }
                            KEYBOARD_TYPE_LEFT_ARROW->{
                                // 左箭头
                                leftArrowAction?.invoke()
                            }
                            KEYBOARD_TYPE_RIGHT_ARROW->{
                                // 右箭头
                                rightArrowAction?.invoke()
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
            }.models = keyBoardDtos
        }
        setKeyBoardLitsener()
    }

    private fun setKeyBoardLitsener() {
        dialog?.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP) {
                (event.unicodeChar == 0).yes {
                    // 返回 0 表示该按键没有可显示的字符
                    when (keyCode) {
                        KeyEvent.KEYCODE_DEL -> {
                            charDeleteAction?.invoke()
                        }

                        else -> {}
                    }
                }.otherwise {
                    //表示有字符的（需要单独处理一下空格，因为空格默认执行了enter键的功能）
                    when (keyCode) {
                        KeyEvent.KEYCODE_SPACE -> {
                            inputSpaceAction?.invoke()
                        }

                        else -> inputChangeAction?.invoke(event.displayLabel.toString())
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

    private var inputChangeAction: ((text: String) -> Unit)? = null  //字符输入回调
    private var charDeleteAction: (() -> Unit)? = null //删除
    private var inputCompleteAction: (() -> Unit)? = null //完成
    private var inputSpaceAction: (() -> Unit)? = null //空格
    private var leftArrowAction: (() -> Unit)? = null //左箭头
    private var rightArrowAction: (() -> Unit)? = null //右箭头

    // 链式调用的 Builder 模式
    class Builder(private val fragmentManager: FragmentManager) {
        private val fragment = CusKeyBoardFragment()

        /**
         * 输入字符后回调
         */
        fun inputChange(action: (text: String) -> Unit): Builder {
            fragment.inputChangeAction = action
            return this
        }

        /**
         * 删除
         */
        fun charDelete(action: () -> Unit): Builder {
            fragment.charDeleteAction = action
            return this
        }

        /**
         * 完成
         */
        fun inputComplete(action: () -> Unit): Builder {
            fragment.inputCompleteAction = action
            return this
        }

        /**
         * 空格
         */
        fun inputSpace(action: () -> Unit): Builder {
            fragment.inputSpaceAction = action
            return this
        }

        /**
         * 左箭头
         */
        fun leftArrow(action: () -> Unit): Builder {
            fragment.leftArrowAction = action
            return this
        }

        /**
         * 右箭头
         */
        fun rightArrow(action: () -> Unit): Builder {
            fragment.rightArrowAction = action
            return this
        }


        fun show() {
            fragment.show(fragmentManager, "CustomDialogFragment")
        }
    }
}
