package com.vodovoz.app.common.like

import com.vodovoz.app.common.like.api.LikeApi
import okhttp3.ResponseBody
import javax.inject.Inject

interface LikeRepository {

    suspend fun like(productIdList: List<Long>, userId: Long): ResponseBody
    suspend fun dislike(productId: Long, userId: Long): ResponseBody
}

class LikeRepositoryImpl @Inject constructor(
    private val api: LikeApi
) : LikeRepository {

    override suspend fun like(
        productIdList: List<Long>,
        userId: Long
    ): ResponseBody {
        return api.like(
            blockId = 12,
            action = "add",
            productIdList = StringBuilder().apply {
                productIdList.forEach { productId ->
                    append(productId).append(",")
                }
            }.toString(),
            userId = userId
        )
    }

    override suspend fun dislike(
        productId: Long,
        userId: Long
    ): ResponseBody {
        return api.dislike(
            blockId = 12,
            action = "del",
            productIdList = productId.toString(),
            userId = userId
        )
    }
}