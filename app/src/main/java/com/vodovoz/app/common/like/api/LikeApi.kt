package com.vodovoz.app.common.like.api

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface LikeApi {

    @GET("/newmobile/osnova/izbrannoe/adddel.php")
    suspend fun like(
        @Query("iblock") blockId: Long? = null,
        @Query("action") action: String? = null,
        @Query("id") productIdList: String? = null,
        @Query("userid") userId: Long? = null,
    ): ResponseBody

    @GET("/newmobile/osnova/izbrannoe/adddel.php")
    suspend fun dislike(
        @Query("iblock") blockId: Long? = null,
        @Query("action") action: String? = null,
        @Query("id") productIdList: String? = null,
        @Query("userid") userId: Long? = null,
    ): ResponseBody
}