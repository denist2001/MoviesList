package com.codechallenge.neugelb.utils

import com.codechallenge.neugelb.data.Result
import junit.framework.Assert.assertEquals
import org.junit.Test

class ResultConverterTest {

    @Test
    fun `check if all values correct objects contains this values`() {
        val source = Result(
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
        val subject = ResultConverter()
        val result = subject.transform(arrayListOf(source))
        assertEquals(12345, result[0].id)
        assertEquals("poster_path_source", result[0].imageUrl)
        assertEquals(9.7F, result[0].rating)
        assertEquals("title_source", result[0].title)
        assertEquals("overview_source", result[0].description)
    }
}