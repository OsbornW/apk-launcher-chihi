package com.chihihx.launcher.bean

data class KeyBoardDto(
    var keyboardType:Int,
    var keyboardChar:String?,
    var keyboardIcon:Int?,
){
    companion object{
        const val KEYBOARD_TYPE_LETTER = 0  //字母
        const val KEYBOARD_TYPE_NUM = 1     //数字
        const val KEYBOARD_TYPE_CASE = 2    //大小写切换
        const val KEYBOARD_TYPE_DEL = 3     //删除
        const val KEYBOARD_TYPE_SWITCH_Special_Characters = 4   //切换特殊字符
        const val KEYBOARD_TYPE_SPACE = 5     //空格
        const val KEYBOARD_TYPE_OK = 6    //完成
        const val KEYBOARD_TYPE_LEFT_ARROW = 7    //左箭头
        const val KEYBOARD_TYPE_RIGHT_ARROW = 8    //右箭头
        const val KEYBOARD_TYPE_HALF_WIDTH = 9    //半角
        const val KEYBOARD_TYPE_BACK_LETTER = 11    //返回字母键盘
        const val KEYBOARD_TYPE_HALF_CHAR = 12    //半角特殊符号
    }
}
