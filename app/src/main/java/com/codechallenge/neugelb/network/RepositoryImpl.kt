package com.codechallenge.neugelb.network

import com.codechallenge.neugelb.BuildConfig
import com.codechallenge.neugelb.data.Response
import retrofit2.Callback
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject constructor() : Repository {

    @Inject
    lateinit var networkService: RepositoryService

    private val apiKey = BuildConfig.API_KEY

    override fun loadNextMovies(
        pageNumber: Int,
        callback: Callback<Response>
    ) {
        val params = HashMap<String, String>()
        params["api_key"] = apiKey
        params["page"] = pageNumber.toString()

        val callResponse = networkService.loadMovies(params)
        callResponse.enqueue(callback)
    }

    override fun searchNextMovies(
        pageNumber: Int,
        query: String,
        callback: Callback<Response>
    ) {
        val params = HashMap<String, String>()
        params["api_key"] = apiKey
        params["query"] = query
        params["page"] = pageNumber.toString()

        val callResponse = networkService.searchMovies(params)
        callResponse.enqueue(callback)
    }
}