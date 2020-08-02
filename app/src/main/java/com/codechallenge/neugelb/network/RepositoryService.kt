package com.codechallenge.neugelb.network

import com.codechallenge.neugelb.data.Response
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface RepositoryService {

    @GET("movie/now_playing")
    fun loadMovies(@QueryMap params: Map<String, String>): Call<Response>

    @GET("search/movie")
    fun searchMovies(@QueryMap params: Map<String, String>): Call<Response>
}