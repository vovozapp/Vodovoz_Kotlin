package com.vodovoz.app.ui.fragment.all_brands

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.mapper.BrandMapper.mapToUI
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.model.BrandUI
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class AllBrandsViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val brandUIListMLD = MutableLiveData<List<BrandUI>>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val brandUIListLD: LiveData<List<BrandUI>> = brandUIListMLD

    private val compositeDisposable = CompositeDisposable()

    private lateinit var brandIdList: List<Long>

    fun updateArgs(brandIdList: List<Long>) {
        this.brandIdList = brandIdList
        updateData()
    }

    fun updateData() {
        dataRepository.fetchAllBrands(brandIdList = brandIdList)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    when(response) {
                        is ResponseEntity.Success -> {
                            brandUIListMLD.value = response.data.mapToUI()
                            viewStateMLD.value = ViewState.Success()
                        }
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Hide()
                        is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error(response.errorMessage)
                    }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message!!) }
            ).addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}