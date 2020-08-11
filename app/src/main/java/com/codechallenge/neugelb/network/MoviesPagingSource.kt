package com.codechallenge.neugelb.network

import androidx.paging.PagingSource
import com.codechallenge.neugelb.data.Result
import javax.inject.Inject

class MoviesPagingSource @Inject constructor(
    private val repository: Repository,
    private val searchQuery: String?
) : PagingSource<Int, Result>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Result> {
        try {
            val pageNumber = params.key ?: 1

            val response =
                if (searchQuery.isNullOrEmpty()) {
                    repository.loadNextMovies(pageNumber)
                } else {
                    repository.searchNextMovies(pageNumber, searchQuery)
                }

            return LoadResult.Page(
                data = response.results,
                prevKey = response.page - 1,
                nextKey = response.page + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}