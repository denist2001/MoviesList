package com.codechallenge.neugelb.data

import org.junit.Assert.assertNotEquals
import org.junit.Test

class ResultTest {
    private val subject = Result(
        poster_path = "poster_path_source",
        adult = false,
        overview = "overview_source",
        release_date = "release_date_source",
        genre_ids = intArrayOf(18, 20, 24),
        id = 12345,
        original_title = "original_title_source",
        original_language = "original_language_source",
        title = "title_source",
        backdrop_path = "backdrop_path_source",
        popularity = 5.8F,
        vote_count = 1559,
        video = false,
        vote_average = 9.7F
    )

    @Test
    fun `to be able check if results are not equals in a case when genre_ids arrays with different order`() {
        val newValue = Result(
            poster_path = "poster_path_source",
            adult = false,
            overview = "overview_source",
            release_date = "release_date_source",
            genre_ids = intArrayOf(24, 18, 20),
            id = 12345,
            original_title = "original_title_source",
            original_language = "original_language_source",
            title = "title_source",
            backdrop_path = "backdrop_path_source",
            popularity = 5.8F,
            vote_count = 1559,
            video = false,
            vote_average = 9.7F
        )
        assertNotEquals(subject, newValue)
    }
}