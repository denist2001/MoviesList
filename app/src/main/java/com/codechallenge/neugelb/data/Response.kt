package com.codechallenge.neugelb.data

data class Response(
    val page: Int,
    val results: List<Result>,
    val dates: Dates,
    val total_pages: Int,
    val total_results: Int
)