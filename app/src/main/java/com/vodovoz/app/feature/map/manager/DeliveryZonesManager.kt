package com.vodovoz.app.feature.map.manager

import com.vodovoz.app.common.content.ErrorState
import com.vodovoz.app.common.content.toErrorState
import com.vodovoz.app.data.MainRepository
import com.vodovoz.app.data.model.common.ResponseEntity
import com.vodovoz.app.data.parser.response.map.DeliveryZonesBundleResponseJsonParser.parseDeliveryZonesBundleResponse
import com.vodovoz.app.mapper.DeliveryZonesBundleMapper.mapToUI
import com.vodovoz.app.ui.model.custom.DeliveryZonesBundleUI
import com.vodovoz.app.util.extensions.debugLog
import com.vodovoz.app.util.polygoncreator.Polygon
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeliveryZonesManager @Inject constructor(
    private val repository: MainRepository
) {

    companion object {
        const val CENTER_COLOR = "#16c60c"
        const val MO_COLOR = "#dfdddd"
    }

    private val deliveryZonesStateListener = MutableStateFlow<DeliveryZonesState?>(null)
    fun observeDeliveryZonesState() = deliveryZonesStateListener.asStateFlow()

    suspend fun fetchDeliveryZonesBundle() {
        flow { emit(repository.fetchDeliveryZonesResponse()) }
            .catch {
                debugLog { "fetch delivery zones error ${it.localizedMessage}" }
            }
            .flowOn(Dispatchers.IO)
            .onEach {
                val response = it.parseDeliveryZonesBundleResponse()
                if (response is ResponseEntity.Success) {
                    val data = response.data.mapToUI()
                    val centerPoints = data.deliveryZoneUIList.filter { it.color == CENTER_COLOR }[0].pointList
                    val moPoints = data.deliveryZoneUIList.filter { it.color == MO_COLOR }[0].pointList
                    deliveryZonesStateListener.value = DeliveryZonesState(
                        deliveryZonesBundleUI = data,
                        centerPoints = centerPoints,
                        centerPolygon = buildPolygon(centerPoints),
                        moPoints = moPoints,
                        moPolygon = buildPolygon(moPoints)
                    )
                } else {
                    deliveryZonesStateListener.value = null
                }
            }
            .flowOn(Dispatchers.Default)
            .collect()
    }

    fun containsInCenterPolygon(point: Point) : Boolean{
        val centerPolygon = deliveryZonesStateListener.value?.centerPolygon ?: return false
        val mappedPoint = com.vodovoz.app.util.polygoncreator.Point(point.latitude, point.longitude)
        return centerPolygon.contains(mappedPoint)
    }

    fun containsInMoPolygon(point: Point) : Boolean{
        val moPolygon = deliveryZonesStateListener.value?.moPolygon ?: return false
        val mappedPoint = com.vodovoz.app.util.polygoncreator.Point(point.latitude, point.longitude)
        return moPolygon.contains(mappedPoint)
    }

    private fun buildPolygon(centerPoints: List<Point>) : Polygon? {
        if (centerPoints.isEmpty()) return null

        return Polygon.Builder()
            .apply {
                centerPoints.forEach {
                    addVertex(com.vodovoz.app.util.polygoncreator.Point(it.latitude, it.longitude))
                }
            }
            .build()
    }

    data class DeliveryZonesState(
        val deliveryZonesBundleUI: DeliveryZonesBundleUI? = null,
        val centerPoints: List<Point> = emptyList(),
        val centerPolygon: Polygon? = null,
        val moPoints: List<Point> = emptyList(),
        val moPolygon: Polygon? = null,
    )

}