package com.vodovoz.app.ui.components.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.ui.components.base.FetchState
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy

class HomeViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val fetchStateMLD = MutableLiveData<FetchState<Any>>()
    val fetchStateLD: LiveData<FetchState<Any>> = fetchStateMLD

    var lastFetchState: FetchState<Any>? = null
        set(value) {
            field = value
            fetchStateMLD.value = field
        }

    init { updateData() }

    fun updateData() {
        fetchStateMLD.value = FetchState.Success()
    }

}