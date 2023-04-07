package com.vodovoz.app.feature.map.api

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface MapKitFlowApi {

    //Получить Cookie Session Id
    @GET("/1.x/")
    suspend fun fetchAddressByGeocodeResponse(
        @Query("apikey") apiKey: String? = null,
        @Query("geocode") geocode: String? = null,
        @Query("format") format: String? = null
    ): ResponseBody

}