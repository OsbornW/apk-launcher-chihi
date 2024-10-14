package com.soya.launcher.bean

class Projector(val type: Int, val icon: Int) {
    companion object {
        const val TYPE_SCREEN_ZOOM: Int = 0 //屏幕切边/屏幕缩放
        const val TYPE_HDMI: Int = 1    // HDMI
        const val TYPE_KEYSTONE_CORRECTION: Int = 2  //梯形校正
        const val TYPE_PROJECTOR_MODE: Int = 3  //投影模式/安装模式
        const val TYPE_KEYSTONE_CORRECTION_MODE: Int = 4   //开启自动梯形校正

        /*
        * P50_H713
        * */
        const val TYPE_AUTO_RESPONSE: Int = 5   //自动响应
        const val TYPE_AUTO_FOCUS: Int = 6   //自动聚焦
        const val TYPE_AUTO_ENTRY: Int = 7   //自动入幕
        const val TYPE_AUTO_CALIBRATION: Int = 8   //自动校准
        const val TYPE_MANUAL_CALIBRATION: Int = 9   //手动校准
        const val TYPE_IMAGE_MODE: Int = 10   //图像模式
    }
}
