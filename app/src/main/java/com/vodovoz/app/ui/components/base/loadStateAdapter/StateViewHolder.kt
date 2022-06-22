package com.vodovoz.app.ui.components.base.loadStateAdapter

import android.view.View
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.vodovoz.app.databinding.ViewHolderStateBinding
import io.reactivex.rxjava3.subjects.PublishSubject

class StateViewHolder(
    private val binding: ViewHolderStateBinding,
    private val updateSubject: PublishSubject<Boolean>
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.update.setOnClickListener {
            updateSubject.onNext(true)
        }
    }

    fun onBind(loadState: LoadState) {
        with (binding) {
            when (loadState) {
                is LoadState.Loading -> {
                    errorContainer.visibility = View.INVISIBLE
                    progressContainer.visibility = View.VISIBLE
                }
                is LoadState.Error -> {
                    errorMessage.text = loadState.error.message
                    errorContainer.visibility = View.VISIBLE
                    progressContainer.visibility = View.INVISIBLE
                }
                else -> {}
            }
        }
    }
}