package com.codechallenge.neugelb.network

import com.codechallenge.neugelb.data.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface RepositoryService {

    @GET("movie/now_playing")
    suspend fun loadMovies(@QueryMap params: Map<String, String>): Response

    @GET("search/movie")
    suspend fun searchMovies(@QueryMap params: Map<String, String>): Response
}