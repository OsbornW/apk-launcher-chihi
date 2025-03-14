package com.chihihx.launcher.net.viewmodel

import com.shudong.lib_base.base.BaseViewModel
import com.shudong.lib_base.ext.stringValue
import com.shudong.lib_base.ext.yes
import com.chihihx.launcher.R
import com.chihihx.launcher.bean.KeyBoardDto
import com.chihihx.launcher.bean.KeyBoardDto.Companion.KEYBOARD_TYPE_BACK_LETTER
import com.chihihx.launcher.bean.KeyBoardDto.Companion.KEYBOARD_TYPE_CASE
import com.chihihx.launcher.bean.KeyBoardDto.Companion.KEYBOARD_TYPE_DEL
import com.chihihx.launcher.bean.KeyBoardDto.Companion.KEYBOARD_TYPE_HALF_CHAR
import com.chihihx.launcher.bean.KeyBoardDto.Companion.KEYBOARD_TYPE_LEFT_ARROW
import com.chihihx.launcher.bean.KeyBoardDto.Companion.KEYBOARD_TYPE_LETTER
import com.chihihx.launcher.bean.KeyBoardDto.Companion.KEYBOARD_TYPE_NUM
import com.chihihx.launcher.bean.KeyBoardDto.Companion.KEYBOARD_TYPE_OK
import com.chihihx.launcher.bean.KeyBoardDto.Companion.KEYBOARD_TYPE_RIGHT_ARROW
import com.chihihx.launcher.bean.KeyBoardDto.Companion.KEYBOARD_TYPE_SPACE
import com.chihihx.launcher.bean.KeyBoardDto.Companion.KEYBOARD_TYPE_SWITCH_Special_Characters
import java.util.Locale

class KeyBoardViewModel : BaseViewModel() {
    fun addNumChar() = run {
        val numList = mutableListOf<KeyBoardDto>()
        val list = arrayListOf("1", "2", "3", "4", "5", "6", "7", "8", "9", ".com", "0", "@")
        list.forEach {
            numList.add(KeyBoardDto(KEYBOARD_TYPE_NUM, it, null))
        }
        numList
    }

    /**
     * 添加小写字母
     */
    fun addLowerCase(isUpper: Boolean): MutableList<KeyBoardDto> {
        val keyBoardDtos = mutableListOf<KeyBoardDto>()
        arrayListOf("q", "w", "e", "r", "t", "y", "u").apply {
            forEach {
                keyBoardDtos.add(KeyBoardDto(KEYBOARD_TYPE_LETTER, it, null))
            }
        }
        keyBoardDtos.add(
            KeyBoardDto(
                KEYBOARD_TYPE_CASE,
                null,
                R.drawable.baseline_lower_cast_100
            )
        )

        keyBoardDtos.add(
            KeyBoardDto(
                KEYBOARD_TYPE_DEL,
                null,
                R.drawable.baseline_backspace_100,
            )
        )


        arrayListOf("a", "s", "d", "f", "g", "h", "j").apply {
            forEach {
                keyBoardDtos.add(KeyBoardDto(KEYBOARD_TYPE_LETTER, it, null))
            }
        }

        keyBoardDtos.add(KeyBoardDto(KEYBOARD_TYPE_OK, R.string.finish.stringValue(), null))


        arrayListOf("z", "x", "c", "v", "b", "n", "m").apply {
            forEach {
                keyBoardDtos.add(KeyBoardDto(KEYBOARD_TYPE_LETTER, it, null))
            }
        }
        keyBoardDtos.add(KeyBoardDto(KEYBOARD_TYPE_SWITCH_Special_Characters, "123_!", null))



        arrayListOf("i", "o", "p", "k", "l").apply {
            forEach {
                keyBoardDtos.add(KeyBoardDto(KEYBOARD_TYPE_LETTER, it, null))
            }
        }
        keyBoardDtos.add(
            KeyBoardDto(
                KEYBOARD_TYPE_LEFT_ARROW,
                null,
                R.drawable.keyboard_left_arrow,
            )
        )
        keyBoardDtos.add(
            KeyBoardDto(
                KEYBOARD_TYPE_RIGHT_ARROW,
                null,
                R.drawable.keyboard_right_arrow,
            )
        )
        keyBoardDtos.add(
            KeyBoardDto(
                KEYBOARD_TYPE_SPACE,
                null,
                R.drawable.keyboard_space,
            )
        )

        isUpper.yes {
            keyBoardDtos.forEach {
                if (it.keyboardType == KEYBOARD_TYPE_LETTER) {
                    it.keyboardChar =
                        it.keyboardChar?.uppercase(Locale.getDefault())
                }
                if(it.keyboardType == KEYBOARD_TYPE_CASE)it.keyboardIcon = R.drawable.baseline_up_case_100
            }
        }

        return keyBoardDtos
    }


    fun addHalfWidth(): MutableList<KeyBoardDto> {
        val keyBoardDtos = mutableListOf<KeyBoardDto>()
        arrayListOf("~", "!", "#", "$", "^", "%", "&", "`").apply {
            forEach {
                keyBoardDtos.add(KeyBoardDto(KEYBOARD_TYPE_HALF_CHAR, it, null))
            }
        }

        keyBoardDtos.add(
            KeyBoardDto(
                KEYBOARD_TYPE_DEL,
                null,
                R.drawable.baseline_backspace_100,
            )
        )



        arrayListOf("?", "(", ")", "_", "+", "=", "*", ";").apply {
            forEach {
                keyBoardDtos.add(KeyBoardDto(KEYBOARD_TYPE_HALF_CHAR, it, null))
            }
        }

        keyBoardDtos.add(KeyBoardDto(KEYBOARD_TYPE_BACK_LETTER, R.string.back.stringValue(), null))


        arrayListOf(":", "'", "\"", "-", "<", ">").apply {
            forEach {
                keyBoardDtos.add(KeyBoardDto(KEYBOARD_TYPE_HALF_CHAR, it, null))
            }
        }



        arrayListOf(",", ".", "[", "]", "/", "\\", "￥", "|").apply {
            forEach {
                keyBoardDtos.add(KeyBoardDto(KEYBOARD_TYPE_HALF_CHAR, it, null))
            }
        }
        keyBoardDtos.add(
            KeyBoardDto(
                KEYBOARD_TYPE_LEFT_ARROW,
                null,
                R.drawable.keyboard_left_arrow,
            )
        )
        keyBoardDtos.add(
            KeyBoardDto(
                KEYBOARD_TYPE_RIGHT_ARROW,
                null,
                R.drawable.keyboard_right_arrow,
            )
        )
        keyBoardDtos.add(KeyBoardDto(KEYBOARD_TYPE_OK, R.string.finish.stringValue(), null))

        return keyBoardDtos
    }


    fun switchLowerUpper(
        adapterList: MutableList<KeyBoardDto>,
        isUpper: Boolean,
        callback: ((index: Int) -> Unit)? = null
    ) {
        val list = mutableListOf<KeyBoardDto>()
        adapterList.forEach {
            val dto = KeyBoardDto(it.keyboardType, it.keyboardChar, it.keyboardIcon)
            list.add(dto)
            if (dto.keyboardType == KEYBOARD_TYPE_LETTER) {
                dto.keyboardChar =
                    if (isUpper) dto.keyboardChar?.uppercase(Locale.getDefault()) else dto.keyboardChar?.lowercase(
                        Locale.getDefault()
                    )
            }
            if (dto.keyboardType == KEYBOARD_TYPE_CASE) {
                if (isUpper) dto.keyboardIcon =
                    R.drawable.baseline_up_case_100 else dto.keyboardIcon =
                    R.drawable.baseline_lower_cast_100
            }
        }

        list.forEachIndexed { parentIndex, dto ->
            adapterList.forEachIndexed { index, keyBoardDto ->
                if (parentIndex == index && dto.keyboardChar != keyBoardDto.keyboardChar) {
                    keyBoardDto.keyboardChar = dto.keyboardChar
                    callback?.invoke(index)
                }
                if (parentIndex == index && dto.keyboardIcon != keyBoardDto.keyboardIcon) {
                    keyBoardDto.keyboardIcon = dto.keyboardIcon
                    callback?.invoke(index)
                }
            }
        }
    }


}