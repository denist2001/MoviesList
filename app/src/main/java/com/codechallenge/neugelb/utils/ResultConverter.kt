package com.codechallenge.neugelb.utils

import com.codechallenge.neugelb.data.Result
import com.codechallenge.neugelb.ui.main.ShortPresentations
import java.util.*
import javax.inject.Inject

class ResultConverter @Inject constructor(){

    fun transform(results: List<Result>?): List<ShortPresentations> {
        if (results.isNullOrEmpty()) return emptyList()
        val presentations = ArrayList<ShortPresentations>()
        for (result in results) {
            presentations.add(
                ShortPresentations(
                    id = result.id,
                    imageUrl = result.poster_path,
                    rating = result.vote_average,
                    title = result.title,
                    description = result.overview
                )
            )
        }
        return presentations
    }

}
