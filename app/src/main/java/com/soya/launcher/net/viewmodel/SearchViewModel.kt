package com.soya.launcher.net.viewmodel

import com.shudong.lib_base.base.BaseViewModel
import com.soya.launcher.bean.SearchDto
import com.soya.launcher.net.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.inject

class SearchViewModel:BaseViewModel() {

    private val repository: HomeRepository by inject()

    fun reqSearchAppList(
        keyWords: String,
        appColumnId: String = "",
        pageSize: Int = 50
    ): Flow<String> = repository.reqSearchAppList(
        hashMapOf("userId" to 62, "keyword" to keyWords, "appColumnId" to appColumnId),
        hashMapOf("pageNo" to 1, "pageSize" to pageSize),
    )

}