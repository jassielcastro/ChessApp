package com.ajcm.chessapp.ui.extensions

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates


inline fun <VH : RecyclerView.ViewHolder, T> RecyclerView.Adapter<VH>.basicDiffUtil(
    initialValue: List<T>,
    crossinline areItemsTheSame: (T, T) -> Boolean = { old, new -> old == new },
    crossinline areContentsTheSame: (T, T) -> Boolean = { old, new -> old == new },
) = Delegates.observable(initialValue) { _, old, new ->
    DiffUtil.calculateDiff(object : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            areItemsTheSame(old[oldItemPosition], new[newItemPosition])

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            areContentsTheSame(old[oldItemPosition], new[newItemPosition])

        override fun getOldListSize(): Int = old.size

        override fun getNewListSize(): Int = new.size
    }).dispatchUpdatesTo(this@basicDiffUtil)
}
