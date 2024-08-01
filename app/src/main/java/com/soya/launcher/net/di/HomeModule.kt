package com.soya.launcher.net.di

import com.soya.launcher.net.api.HomeApi
import com.soya.launcher.net.api.HomeLocalApi
import com.soya.launcher.net.repository.HomeLocalRepository
import com.soya.launcher.net.repository.HomeRepository
import com.soya.launcher.net.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
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


private val homeNetModule = module {
    single { get<Retrofit>().create(HomeApi::class.java) }
    single { get<Retrofit>().create(HomeLocalApi::class.java) }
    single { HomeRepository(get()) }
    single { HomeLocalRepository(get()) }

}

val homeViewModel = module {
    viewModel { HomeViewModel() }
}
val homeModules = listOf(homeNetModule, homeViewModel)