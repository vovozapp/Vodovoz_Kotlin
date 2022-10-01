package com.vodovoz.app.data.remote

import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MapKitApi {

    //Получить Cookie Session Id
    @GET("/1.x/")
    fun fetchAddressByGeocodeResponse(
        @Query("apikey") apiKey: String? = null,
        @Query("geocode") geocode: String? = null,
        @Query("format") format: String? = null
    ): Single<Response<ResponseBody>>

    //https://geocode-maps.yandex.ru/1.x/?apikey=346ef353-b4b2-44b3-b597-210d62eeb66b&geocode=37.96188,55.798412&format=json
}