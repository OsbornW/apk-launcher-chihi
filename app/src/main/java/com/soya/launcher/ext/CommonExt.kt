package com.soya.launcher.ext

import android.content.pm.ApplicationInfo
import com.drake.brv.BindingAdapter
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
import com.soya.launcher.bean.Notify
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
                movie?.name?:"",
                movie?.icon?:"",
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

fun convertGameJson():MutableList<TypeItem> = run {
    val dto = Gson().fromJson(
        InputStreamReader(
            appContext.assets.open("game.json")
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
                movie?.name?:"",
                movie?.icon?:"",
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

fun BindingAdapter.refresh(oldList: MutableList<Notify>, newList: MutableList<Notify>) {
    val oldPackageNames = oldList.map { it.type }.toSet()
    val newPackageNames = newList.map { it.type }.toSet()

    // 找出新增的 packageName 和缺失的 packageName
    val missingInNew = newPackageNames - oldPackageNames
    val extraInOld = oldPackageNames - newPackageNames

    when {
        // 处理新增的情况
        missingInNew.isNotEmpty()-> {

            missingInNew.forEach { missingPackageName ->
                val newItem = newList.find { it.type == missingPackageName }
                if (newItem != null) {
                    var tempList = mutableListOf<Notify>()
                    tempList.addAll(oldList)


                    // 找到第一个type为0的元素的索引
                    val index = tempList.indexOfFirst { it.type == 0 }

                    // 如果找到了type为0的元素，就在其前面插入newItem
                    if (index != -1) {
                        tempList.add(index, newItem)
                    } else {
                        // 如果没有找到，则直接添加到列表末尾
                        tempList.add(newItem)
                    }

                    tempList.add(newItem)
                    val sortIndex = tempList.getIndexAfterSorting(newItem)
                    oldList.add(sortIndex,newItem)
                    notifyItemInserted(oldList.indexOf(newItem))
                }
            }
        }
        // 处理移除的情况
        extraInOld.isNotEmpty()-> {

            val positionsToRemove = mutableListOf<Int>()
            extraInOld.forEach { extraPackageName ->
                oldList.indexOfFirst { it.type == extraPackageName }.let { position ->
                    if (position != -1) {

                        positionsToRemove.add(position)
                    }
                }
            }
            // 从最后一个位置开始移除，避免索引问题
            positionsToRemove.sortedDescending().forEach { position ->
                oldList.removeAt(position)
                notifyItemRemoved(position)
            }


        }
        else -> {

        }
    }
}

/**
 * 获取指定应用在排序后集合中的索引位置
 * 如果找不到对应的索引，返回0
 */
fun MutableList<Notify>.getIndexAfterSorting(targetAppInfo: Notify): Int {

    // 获取目标应用的信息在排序后集合中的索引位置
    return this.indexOfFirst { it.type == targetAppInfo.type }.takeIf { it >= 0 } ?: 0
}


fun BindingAdapter.refreshAppList(oldList: MutableList<ApplicationInfo>,newList: MutableList<ApplicationInfo>) {
    val oldPackageNames = oldList.map { it.packageName }.toSet()
    val newPackageNames = newList.map { it.packageName }.toSet()

    // 找出新增的 packageName 和缺失的 packageName
    val missingInNew = newPackageNames - oldPackageNames
    val extraInOld = oldPackageNames - newPackageNames

    when {
        // 处理新增的情况
        missingInNew.isNotEmpty()-> {

            missingInNew.forEach { missingPackageName ->
                val newItem = newList.find { it.packageName == missingPackageName }
                if (newItem != null) {
                    var tempList = mutableListOf<ApplicationInfo>()
                    tempList.addAll(oldList)
                    tempList.add(newItem)
                    val sortIndex = tempList.getIndexAfterSorting(newItem)
                    oldList.add(sortIndex,newItem)
                    notifyItemInserted(oldList.indexOf(newItem))
                }
            }
        }
        // 处理移除的情况
        extraInOld.isNotEmpty()-> {

            val positionsToRemove = mutableListOf<Int>()
            extraInOld.forEach { extraPackageName ->
                oldList.indexOfFirst { it.packageName == extraPackageName }.let { position ->
                    if (position != -1) {

                        positionsToRemove.add(position)
                    }
                }
            }
            // 从最后一个位置开始移除，避免索引问题
            positionsToRemove.sortedDescending().forEach { position ->
                oldList.removeAt(position)
                notifyItemRemoved(position)
            }


        }
        else -> {

        }
    }
}

