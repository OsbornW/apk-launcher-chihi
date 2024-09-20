package com.soya.launcher.enums

interface Types {
    companion object {
        const val TYPE_UNKNOW: Int = -1     //未知类型
        const val TYPE_APP_STORE: Int = 4   // APP Store
        const val TYPE_MY_APPS: Int = 5     //本地应用
        const val TYPE_PROJECTOR: Int = 6   //投影仪
        const val TYPE_MOVICE: Int = 10     // 视频电影
        const val TYPE_GOOGLE_PLAY: Int = 12    //google play(注册码)
        const val TYPE_MEDIA_CENTER: Int = 13   // mediacenter(注册码)
    }
}
