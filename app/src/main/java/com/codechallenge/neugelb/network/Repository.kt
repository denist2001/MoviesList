package com.codechallenge.neugelb.network

import androidx.paging.PagingData
import com.codechallenge.neugelb.data.Response
import kotlinx.coroutines.flow.Flow

interface Repository {

    suspend fun loadNextMovies(
        pageNumber: Int
    ): Response

    suspend fun searchNextMovies(
        pageNumber: Int,
        query: String
    ): Response
}