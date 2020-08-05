package com.codechallenge.neugelb.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.codechallenge.neugelb.BuildConfig
import com.codechallenge.neugelb.R
import javax.inject.Inject

class MainAdapter @Inject constructor() : ListAdapter<ShortPresentations, MainAdapter.MainViewHolder>(DiffCallback()) {

    private lateinit var loadNextMovies: () -> Unit
    private val imagesDomain = BuildConfig.SMALL_IMAGES_DOMAIN

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.presentation_item, parent, false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.itemView.setOnClickListener { clickedView ->
            val presentation = currentList[position] //presentationItems[position]
            val action = MainFragmentDirections.actionShowSelectedMovie(
                title = presentation.title,
                imageUrl = presentation.imageUrl,
                description = presentation.description,
                rating = presentation.rating?.let { it } ?: 0.0F
            )
            clickedView.findNavController().navigate(action)
        }
        val presentation = currentList[position] //presentationItems[position]
        holder.title.text = presentation.title
        holder.description.text = presentation.description
        //here I divided to 2 to avoid issue on android 7.0
        //https://issuetracker.google.com/issues/37114040
        holder.ratingBar.rating = presentation.rating?.let { it / 2 } ?: 0.0F
        holder.posterView.load(imagesDomain + presentation.imageUrl) {
            scale(Scale.FIT)
            placeholder(R.drawable.ic_baseline_local_movies_24)
            transformations(RoundedCornersTransformation(8F))
        }
        if (position == currentList.size - 10) {
            loadNextMovies()
        }
    }

    fun cleanPresentationsList() {
        currentList.clear()
        notifyDataSetChanged()
    }

    fun getNextPresentations(loadNextValues: ()->Unit) {
        this.loadNextMovies = loadNextValues
    }

    class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val posterView: ImageView = itemView.findViewById(R.id.poster_iv)
        val title: TextView = itemView.findViewById(R.id.title_tv)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val description: TextView = itemView.findViewById(R.id.description_tv)
    }
}

class DiffCallback : DiffUtil.ItemCallback<ShortPresentations>() {
    override fun areItemsTheSame(
        oldItem: ShortPresentations,
        newItem: ShortPresentations
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: ShortPresentations,
        newItem: ShortPresentations
    ): Boolean {
        return oldItem == newItem
    }
}