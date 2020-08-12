package com.codechallenge.neugelb.network

import androidx.paging.PagingSource
import com.codechallenge.neugelb.data.Result
import javax.inject.Inject

class MoviesPagingSource @Inject constructor(
    private val repository: Repository,
    private val searchQuery: String?
) : PagingSource<Int, Result>() {

    private val GITHUB_STARTING_PAGE_INDEX = 1

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
                prevKey = if (pageNumber == GITHUB_STARTING_PAGE_INDEX) null else pageNumber - 1,
                nextKey = if (response.results.isEmpty()) null else pageNumber + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}