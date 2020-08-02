package com.codechallenge.neugelb.utils

import androidx.recyclerview.widget.DiffUtil
import com.codechallenge.neugelb.ui.main.ShortPresentations

class PresentationsDiffCallback(
    private val newPresentations: List<ShortPresentations>,
    private val oldPresentations: List<ShortPresentations>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldPresentations[oldItemPosition].id == newPresentations[newItemPosition].id
    }

    override fun getOldListSize(): Int {
        return oldPresentations.size
    }

    override fun getNewListSize(): Int {
        return newPresentations.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldPresentations[oldItemPosition] == newPresentations[newItemPosition]
    }
}