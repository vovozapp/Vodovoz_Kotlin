package com.vodovoz.app.feature.all.orders.detail.traceorder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.feature.all.orders.detail.model.DriverPointsEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TraceOrderViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val repository: MainRepository,
) : PagingContractViewModel<TraceOrderViewModel.TraceOrderState, TraceOrderViewModel.TraceOrderEvents>(
    TraceOrderState()
) {

    private val driverId = savedState.get<String>("driverId")

    private val firebaseDatabase = FirebaseDatabase.getInstance().reference

    private fun fetchDriverData(driverId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                firebaseDatabase.child(driverId).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }
        }
    }

    data class TraceOrderState(
        val item: Item? = null,
    ) : State

    sealed class TraceOrderEvents : Event
}