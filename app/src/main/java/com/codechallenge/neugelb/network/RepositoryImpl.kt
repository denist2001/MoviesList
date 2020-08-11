package com.codechallenge.neugelb.network

import com.codechallenge.neugelb.BuildConfig
import com.codechallenge.neugelb.data.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject constructor() : Repository {

    @Inject
    lateinit var networkService: RepositoryService

    private val apiKey = BuildConfig.API_KEY

    override suspend fun loadNextMovies(
        pageNumber: Int
    ): Response {
        val params = HashMap<String, String>()
        params["api_key"] = apiKey
        params["page"] = pageNumber.toString()

        return networkService.loadMovies(params)

    }

    override suspend fun searchNextMovies(
        pageNumber: Int,
        query: String
    ): Response {
        val params = HashMap<String, String>()
        params["api_key"] = apiKey
        params["query"] = query
        params["page"] = pageNumber.toString()

        return networkService.searchMovies(params)

    }
}