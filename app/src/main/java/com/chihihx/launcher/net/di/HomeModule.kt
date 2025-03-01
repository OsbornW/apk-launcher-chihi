package com.chihihx.launcher.net.di

import com.chihihx.launcher.net.api.AuthApi
import com.chihihx.launcher.net.api.HomeApi
import com.chihihx.launcher.net.api.HomeLocalApi
import com.chihihx.launcher.net.repository.AuthRepository
import com.chihihx.launcher.net.repository.HomeLocalRepository
import com.chihihx.launcher.net.repository.HomeRepository
import com.chihihx.launcher.net.viewmodel.HomeViewModel
import com.chihihx.launcher.net.viewmodel.KeyBoardViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
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
    single { get<Retrofit>(named("main")).create(HomeApi::class.java) }
    single { get<Retrofit>(named("alternate")).create(HomeLocalApi::class.java) }
    single { get<Retrofit>(named("activeCode")).create(AuthApi::class.java) }
    single { HomeRepository(get()) }
    single { HomeLocalRepository(get()) }
    single { AuthRepository(get()) }

}

val homeViewModel = module {
    viewModel { HomeViewModel() }
}

val keyboardViewModel = module {
    viewModel { KeyBoardViewModel() }
}

val homeModules = listOf(homeNetModule, homeViewModel,keyboardViewModel)