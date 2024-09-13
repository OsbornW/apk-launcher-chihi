package com.soya.launcher.ext

import android.widget.ImageView
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.google.gson.Gson
import com.shudong.lib_base.ext.appContext
import com.shudong.lib_base.ext.otherwise
import com.shudong.lib_base.ext.stringValue
import com.shudong.lib_base.ext.yes
import com.soya.launcher.App
import com.soya.launcher.R
import com.soya.launcher.bean.Data
import com.soya.launcher.bean.HomeInfoDto
import com.soya.launcher.bean.Movice
import com.soya.launcher.bean.TypeItem
import com.soya.launcher.enums.Types
import java.io.InputStreamReader
import java.util.UUID

fun convertH27002Json():MutableList<TypeItem> = run {
    val dto = Gson().fromJson(
        InputStreamReader(
            appContext.assets.open("h27002.json")
        ),
        HomeInfoDto::class.java
    )

    val menus: MutableList<TypeItem> = mutableListOf()

    dto.movies?.let {
        it.forEach { movie ->
            val movies = mutableListOf<Data>()
            movie?.datas?.forEach { data->
                data?.picType = Movice.PIC_NETWORD
                data?.packageNames = movie.packageNames
                data?.appName = movie.name
                if (data != null) {
                    movies.add(data)
                }
            }

            val item = TypeItem(
                movie?.name,
                movie?.icon,
                UUID.randomUUID().leastSignificantBits,
                Types.TYPE_MOVICE,
                TypeItem.TYPE_ICON_IMAGE_URL,
                movie?.type ?: 0
            )
            item.iconName = movie?.iconName
            item.data = movie?.packageNames
            App.MOVIE_MAP.put(item.id, movies)
            menus.add(item)
        }
    }
    menus
}

fun autoResponseText() = run{
    val isResponseOpen = SYSTEM_PROPERTY_AUTO_RESPONSE.systemPropertyValueBoolean()
    isResponseOpen.yes {
        //当前自动响应是打开的
        R.string.auto_response_on.stringValue()
    }.otherwise {
        //当前自动响应是关闭的
         R.string.auto_response_off.stringValue()
    }
}

fun setAutoResponseProperty() = run{
    val isResponseOpen = SYSTEM_PROPERTY_AUTO_RESPONSE.systemPropertyValueBoolean()
    isResponseOpen.yes {
        //进行关闭
        SYSTEM_PROPERTY_AUTO_RESPONSE.setSystemPropertyValueBoolean(false)
        R.string.auto_response_off.stringValue()
    }.otherwise {
        //进行打开
        SYSTEM_PROPERTY_AUTO_RESPONSE.setSystemPropertyValueBoolean(true)
        R.string.auto_response_on.stringValue()
    }
}
