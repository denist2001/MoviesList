package com.codechallenge.neugelb.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.codechallenge.neugelb.data.Result
import com.codechallenge.neugelb.network.MoviesPagingSource
import com.codechallenge.neugelb.network.Repository
import com.codechallenge.neugelb.utils.ResultConverter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MainViewModel @ViewModelInject constructor(
    private val repository: Repository,
    private val converter: ResultConverter
) : ViewModel() {

    var previousSearch = ""
    var loadingFlow: Flow<PagingData<ShortPresentations>>? = Pager(PagingConfig(20)) {
        MoviesPagingSource(repository, "")
    }
        .flow
        .map { pagingData: PagingData<Result> ->
            pagingData.map { result: Result ->
                converter.transform(result)
            }
        }
        .cachedIn(viewModelScope)
    fun searchingFlow(searchQuery: String): Flow<PagingData<ShortPresentations>>? {
        return Pager(PagingConfig(20)) {
            MoviesPagingSource(repository, searchQuery)
        }.flow
            .map { pagingData: PagingData<Result> ->
                pagingData.map { result: Result ->
                    converter.transform(result)
                }
            }
            .cachedIn(viewModelScope)
    }
}
