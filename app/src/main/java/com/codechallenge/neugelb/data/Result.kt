package com.codechallenge.neugelb.data

data class Result(
    val poster_path: String?,
    val adult: Boolean?,
    val overview: String?,
    val release_date: String?,
    val genre_ids: IntArray?,
    val id: Int?,
    val original_title: String?,
    val original_language: String?,
    val title: String?,
    val backdrop_path: String?,
    val popularity: Float?,
    val vote_count: Int?,
    val video: Boolean?,
    val vote_average: Float?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Result) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id ?: 0
    }
}