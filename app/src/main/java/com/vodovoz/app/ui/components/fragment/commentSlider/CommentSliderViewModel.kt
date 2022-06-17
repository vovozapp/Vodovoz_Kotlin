package com.vodovoz.app.ui.components.fragment.commentSlider

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.ui.components.base.BaseHiddenFragment
import com.vodovoz.app.ui.mapper.CommentMapper.mapToUI
import com.vodovoz.app.ui.model.CommentUI
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy

class CommentSliderViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val sliderCommentListMLD = MutableLiveData<List<CommentUI>>()
    private val stateMLD = MutableLiveData<BaseHiddenFragment.State>()

    val sliderCommentListLD: LiveData<List<CommentUI>> = sliderCommentListMLD
    val sateLD: LiveData<BaseHiddenFragment.State> = stateMLD

    private val compositeDisposable = CompositeDisposable()

    init {
        dataRepository.commentSubject
            .subscribeBy { response ->
                when(response) {
                    is ResponseEntity.Success -> {
                        stateMLD.value = BaseHiddenFragment.State.SHOW
                        response.data?.let { noNullData ->
                            sliderCommentListMLD.value = noNullData.mapToUI()
                        }
                    }
                    is ResponseEntity.Error -> {
                        stateMLD.value = BaseHiddenFragment.State.HIDE
                    }
                }
            }
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}