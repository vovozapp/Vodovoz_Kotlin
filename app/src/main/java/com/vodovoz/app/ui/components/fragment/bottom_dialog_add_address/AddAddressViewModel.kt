package com.vodovoz.app.ui.components.fragment.bottom_dialog_add_address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vodovoz.app.R
import com.vodovoz.app.data.DataRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.model.common.SortType
import com.vodovoz.app.databinding.BottomFragmentSortSettingsBinding
import com.vodovoz.app.databinding.DialogBottomAddAddressBinding
import com.vodovoz.app.ui.components.base.ViewState
import com.vodovoz.app.ui.components.base.ViewStateBaseBottomFragment
import com.vodovoz.app.ui.components.fragment.paginated_products_catalog.PaginatedProductsCatalogFragment
import com.vodovoz.app.ui.mapper.AddressMapper.mapToUI
import com.vodovoz.app.ui.model.AddressUI
import com.vodovoz.app.util.Keys
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

class AddAddressViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val viewStateMLD = MutableLiveData<ViewState>()
    private val errorMLD = MutableLiveData<String>()
    private val addressUIMLD = MutableLiveData<AddressUI>()

    val viewStateLD: LiveData<ViewState> = viewStateMLD
    val errorLD: LiveData<String> = errorMLD
    val addressLD: LiveData<AddressUI> = addressUIMLD

    private val compositeDisposable = CompositeDisposable()

    lateinit var addressUI: AddressUI

    fun updateArgs(addressUI: AddressUI) {
        this.addressUI = addressUI
        addressUIMLD.value = addressUI
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}