package com.vodovoz.app.feature.profile.old

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.CategoryDetailEntity
import com.vodovoz.app.data.model.common.OrderEntity
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.UserDataEntity
import com.vodovoz.app.mapper.CategoryDetailMapper.mapToUI
import com.vodovoz.app.mapper.OrderMapper.mapToUI
import com.vodovoz.app.mapper.UserDataMapper.mapToUI
import com.vodovoz.app.ui.base.ViewState
import com.vodovoz.app.ui.model.CategoryDetailUI
import com.vodovoz.app.ui.model.OrderUI
import com.vodovoz.app.ui.model.UserDataUI
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val userDataUIMLD = MutableLiveData<UserDataUI>()
    private val isAlreadyLoginMLD = MutableLiveData<Boolean>()
    private val orderUIListMLD = MutableLiveData<List<OrderUI>>()
    private val viewedProductsSliderMLD = MutableLiveData<List<CategoryDetailUI>>()
    private val bestForYouProductsListMLD = MutableLiveData<CategoryDetailUI>()
    private val errorMLD = MutableLiveData<String>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val isAlreadyLoginLD: LiveData<Boolean> = isAlreadyLoginMLD
    val userDataUILD: LiveData<UserDataUI> = userDataUIMLD
    val orderUIListLD: LiveData<List<OrderUI>> = orderUIListMLD
    val viewedProductsSliderLD: LiveData<List<CategoryDetailUI>> = viewedProductsSliderMLD
    val bestForYouProductsListLD: LiveData<CategoryDetailUI> = bestForYouProductsListMLD
    val errorLD: LiveData<String> = errorMLD

    private val compositeDisposable = CompositeDisposable()

    fun isAlreadyLogin() {
        when(dataRepository.isAlreadyLogin()) {
            true -> {
                isAlreadyLoginMLD.value = true
                updateData()
            }
            else -> isAlreadyLoginMLD.value = false
        }
    }

    fun updateData() {
        Single.zip(
            dataRepository.fetchUserData(userId = dataRepository.fetchUserId()!!),
            dataRepository.fetchOrdersSlider(),
            dataRepository.fetchViewedProductsSlider(),
            dataRepository.fetchPersonalProducts()
        ) { userDataResponse, ordersResponse, viewedProductsResponse, bestForYouProductsResponse ->
            Pair(Pair(userDataResponse, ordersResponse), Pair(viewedProductsResponse, bestForYouProductsResponse))
        }.subscribeOn(Schedulers.io())
            .doOnSubscribe { viewStateMLD.value = ViewState.Loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { response ->
                    if (response.first.second is ResponseEntity.Success) {
                        orderUIListMLD.value = (response.first.second as ResponseEntity.Success<List<OrderEntity>>).data.mapToUI()
                    }

                    if (response.second.first is ResponseEntity.Success) {
                        viewedProductsSliderMLD.value = (response.second.first as ResponseEntity.Success<List<CategoryDetailEntity>>).data.mapToUI()
                    } else {
                        viewedProductsSliderMLD.value = listOf()
                    }

                    if (response.second.second is ResponseEntity.Success) {
                        bestForYouProductsListMLD.value = (response.second.second as ResponseEntity.Success<CategoryDetailEntity>).data.mapToUI()
                    }

                    when(response.first.first) {
                        is ResponseEntity.Hide -> viewStateMLD.value = ViewState.Error("Hide")
                        is ResponseEntity.Error -> viewStateMLD.value = ViewState.Error((response.first.first as ResponseEntity.Error<UserDataEntity>).errorMessage)
                        is ResponseEntity.Success -> {
                            userDataUIMLD.value = (response.first.first as ResponseEntity.Success<UserDataEntity>).data.mapToUI()
                            viewStateMLD.value = ViewState.Success()
                        }
                    }
                },
                onError = { throwable -> viewStateMLD.value = ViewState.Error(throwable.message!!) }
            ).addTo(compositeDisposable)
    }

    fun logout() {
        dataRepository.logout().subscribe()
        isAlreadyLoginMLD.value = false
    }

    fun changeFavoriteStatus(productId: Long, isFavorite: Boolean) {
        when(isFavorite) {
            true -> dataRepository.addToFavorite(productId)
            false -> dataRepository.removeFromFavorite(productId = productId)

        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {},
                onError = { throwable -> errorMLD.value = throwable.message ?: "Неизвестная ошибка" }
            ).addTo(compositeDisposable)
    }

    fun changeCart(productId: Long, quantity: Int) {
        dataRepository
            .changeCart(productId, quantity)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = { errorMLD.value = it.message ?: "Неизвестная ошибка" })
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}