package com.vodovoz.app.feature.all.orders.detail.traceorder

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vodovoz.app.common.content.Event
import com.vodovoz.app.common.content.PagingContractViewModel
import com.vodovoz.app.common.content.State
import com.vodovoz.app.common.content.itemadapter.Item
import com.vodovoz.app.data.MainRepository
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TraceOrderViewModel @Inject constructor(
    private val savedState: SavedStateHandle,
    private val repository: MainRepository,
    private val application: Application,
) : PagingContractViewModel<TraceOrderViewModel.TraceOrderState, TraceOrderViewModel.TraceOrderEvents>(
    TraceOrderState()
) {

    private val driverId = savedState.get<String>("driverId")
    private val driverName = savedState.get<String>("driverName")
    private val orderId = savedState.get<String>("orderId")

    private val firebaseDatabase = FirebaseDatabase.getInstance().reference

    init {
        viewModelScope.launch(Dispatchers.Default) {
            generateBitmap(
                "https://vodovoz.ru/bitrix/templates/vodovoz/images/karta/auto.png",
                true
            )
            generateBitmap(
                "https://vodovoz.ru/bitrix/templates/vodovoz/images/karta/home.png",
                false
            )
        }
    }

    private fun generateBitmap(url: String, auto: Boolean) {
        val bitmap = Glide
            .with(application)
            .asBitmap()
            .load(url)
            .submit()
            .get()

        val scaledBitmap = Bitmap.createScaledBitmap(
            bitmap,
            80,
            80,
            false
        )

        uiStateListener.value = if (auto) {
            state.copy(
                data = state.data.copy(
                    autoBitmap = scaledBitmap
                )
            )
        } else {
            state.copy(
                data = state.data.copy(
                    homeBitmap = scaledBitmap
                )
            )
        }
    }

    fun fetchDriverData() {
        viewModelScope.launch(Dispatchers.IO) {
            if (driverId == null) return@launch
            runCatching {
                firebaseDatabase.child(driverId).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (!snapshot.exists()) return

                        val nameBuilder = StringBuilder()
                        val carBuilder = StringBuilder()
                        val lastName = snapshot.child("LastName").value.toString()
                        val firstName = snapshot.child("FirstName").value.toString()
                        val carName = snapshot.child("Auto").value.toString()
                        val carNumber = snapshot.child("CarNumber").value.toString()

                        nameBuilder
                            .apply {
                                if (lastName.isNotEmpty()) {
                                    append("$lastName ")
                                }
                                if (firstName.isNotEmpty()) {
                                    append(firstName)
                                }
                            }

                        carBuilder
                            .apply {
                                if (carName.isNotEmpty()) {
                                    append("$carName ")
                                }
                                if (carNumber.isNotEmpty()) {
                                    append(carNumber)
                                }
                            }

                        val driverLatitude =
                            snapshot.child("Position_vodila").child("Latitude").value.toString()
                                .replace(",", ".")
                        val driverLongitude =
                            snapshot.child("Position_vodila").child("Longitude").value.toString()
                                .replace(",", ".")

                        val point =
                            if (driverLatitude.isNotEmpty() && driverLongitude.isNotEmpty()) {
                                Point(driverLatitude.toDouble(), driverLongitude.toDouble())
                            } else {
                                null
                            }

                        uiStateListener.value = state.copy(
                            data = state.data.copy(
                                name = nameBuilder.toString(),
                                car = carBuilder.toString(),
                                driverPoint = point
                            )
                        )

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }
        }
    }

    data class TraceOrderState(
        val item: Item? = null,
        val name: String? = null,
        val car: String? = null,
        val driverPoint: Point? = null,
        val autoBitmap: Bitmap? = null,
        val homeBitmap: Bitmap? = null,
    ) : State

    sealed class TraceOrderEvents : Event
}