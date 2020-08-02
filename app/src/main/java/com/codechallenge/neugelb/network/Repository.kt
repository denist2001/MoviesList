package com.codechallenge.neugelb.network

import com.codechallenge.neugelb.data.Response
import retrofit2.Callback

interface Repository {
    fun loadNextMovies(
        pageNumber: Int,
        callback: Callback<Response>
    )
    fun searchNextMovies(
        pageNumber: Int,
        query: String,
        callback: Callback<Response>
    )
}