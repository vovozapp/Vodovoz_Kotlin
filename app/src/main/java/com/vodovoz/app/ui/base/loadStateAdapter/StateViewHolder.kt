package com.vodovoz.app.ui.base.loadStateAdapter

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
        binding.btnUpdate.setOnClickListener {
            updateSubject.onNext(true)
        }
    }

    fun onBind(loadState: LoadState) {
        with (binding) {
            when (loadState) {
                is LoadState.Loading -> {
                    llErrorContainer.visibility = View.INVISIBLE
                    pbLoading.visibility = View.VISIBLE
                }
                is LoadState.Error -> {
                    tvErrorMessage.text = loadState.error.message
                    llErrorContainer.visibility = View.VISIBLE
                    pbLoading.visibility = View.INVISIBLE
                }
                else -> {}
            }
        }
    }
}