package com.chihihx.launcher.product

import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.yes
import com.chihihx.launcher.bean.TypeItem
import com.chihihx.launcher.ext.convertGameJson
import com.chihihx.launcher.ext.isGame

object ProjectorX50 : ProjectorP50() {
    override fun isShowDefaultVideoApp() = false

    override fun addGameItem(): MutableList<TypeItem>? = run {
        isGame().yes {
            convertGameJson()
        }.otherwise { null }
    }

    override fun isGameRes(): Int = run {
        isGame().yes {
            1
        }.otherwise { 0 }
    }

}