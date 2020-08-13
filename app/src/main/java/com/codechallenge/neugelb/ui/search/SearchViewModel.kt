package com.codechallenge.neugelb.ui.search

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.codechallenge.neugelb.data.Result
import com.codechallenge.neugelb.network.MoviesPagingSource
import com.codechallenge.neugelb.network.Repository
import com.codechallenge.neugelb.ui.main.ShortPresentations
import com.codechallenge.neugelb.utils.ResultConverter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchViewModel @ViewModelInject constructor(
    private val repository: Repository,
    private val converter: ResultConverter
) : ViewModel() {
    fun searchingFlow(searchQuery: String): Flow<PagingData<ShortPresentations>>? =
        Pager(
            PagingConfig(20, enablePlaceholders = true),
            pagingSourceFactory = { MoviesPagingSource(repository, searchQuery) }
        ).flow
            .map { pagingData: PagingData<Result> ->
                pagingData.map { result: Result ->
                    converter.transform(result)
                }
            }
            .cachedIn(viewModelScope)
}
