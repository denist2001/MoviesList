package com.codechallenge.neugelb.ui.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import coil.ImageLoader
import coil.api.load
import coil.decode.DataSource
import coil.request.LoadRequest
import coil.request.Request
import com.codechallenge.neugelb.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.details_fragment.*

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.details_fragment) {
    private val imagesDomain = "https://image.tmdb.org/t/p/w500"
    lateinit var imageLoader: ImageLoader

    //Poster, Title, Description and Rating
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = requireArguments().getString("title", "")
        val description = requireArguments().getString("description", "")
        var imageUrl = requireArguments().getString("image_url", "")
        val rating = requireArguments().getFloat("rating", 0.0F)

        if (imageUrl.isNotEmpty()) {
            imageUrl = imagesDomain + imageUrl
        }
        imageLoader = ImageLoader(view.context)
        val request = LoadRequest.Builder(view.context)
            .data(imageUrl)
            .target { drawable ->
                poster_iv.load(drawable)
            }
            .listener(object : Request.Listener {

                override fun onError(request: Request, throwable: Throwable) {
                    super.onError(request, throwable)
                    poster_iv.load(R.drawable.ic_round_local_movies_24)
                    progressBar.visibility = View.INVISIBLE
                }

                override fun onSuccess(request: Request, source: DataSource) {
                    super.onSuccess(request, source)
                    progressBar.visibility = View.INVISIBLE
                }
            })
            .build()
        imageLoader.execute(request)

        title_tv.text = title
        description_tv.text = description
        //here I divided to 2 to avoid issue on android 7.0
        //https://issuetracker.google.com/issues/37114040
        ratingBar.rating = rating / 2
    }

    override fun onDetach() {
        super.onDetach()
        imageLoader.shutdown()
    }
}