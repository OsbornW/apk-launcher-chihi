package com.shudong.lib_base.base.viewmodel

import com.shudong.lib_base.base.BaseViewModelDB
import org.koin.dsl.module
import retrofit2.Retrofit

/**
 *
 * @ProjectName:  Ispruz_android
 * @Desc:
 * @Author:  zengyue
 * @Date:  2022/11/2 17:45
 * @PACKAGE_NAME:  com.thumbsupec.ispruz.home.di
 */

private val baseModule = module {
    single { get<Retrofit>().create(BaseApi::class.java) }
    single { BaseRepository(get()) }
    single { BaseRepositoryDB() }
    single { BaseViewModelDB() }
}


val baseModules = listOf(baseModule)