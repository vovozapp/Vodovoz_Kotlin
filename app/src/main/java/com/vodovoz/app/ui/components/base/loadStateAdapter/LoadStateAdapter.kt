package com.vodovoz.app.ui.components.base.loadStateAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.vodovoz.app.databinding.ViewHolderStateBinding
import io.reactivex.rxjava3.subjects.PublishSubject

class LoadStateAdapter(
    private val updateSubject: PublishSubject<Boolean>
) : LoadStateAdapter<StateViewHolder>() {

    companion object {
        const val ERROR = 1
        const val PROGRESS = 0
    }

    override fun getStateViewType(loadState: LoadState) = when (loadState) {
        is LoadState.NotLoading -> error("Not supported")
        LoadState.Loading -> PROGRESS
        is LoadState.Error -> ERROR
    }

    override fun onBindViewHolder(
        holder: StateViewHolder,
        loadState: LoadState
    ) = holder.onBind(loadState)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ) = when (loadState) {
        is LoadState.NotLoading -> error("Not supported")
        else -> StateViewHolder(
            binding = ViewHolderStateBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            updateSubject = updateSubject
        )
    }

}