package com.vodovoz.app.common.product.rating

import com.squareup.moshi.JsonClass
import com.vodovoz.app.common.account.data.AccountManager
import com.vodovoz.app.data.MainRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RatingProductManager @Inject constructor(
    private val repository: MainRepository,
    private val accountManager: AccountManager
) {

    private val ratings = ConcurrentHashMap<Long, Float>()
    private val ratingsStateListener = MutableSharedFlow<Map<Long, Float>>(replay = 1)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun observeRatings() = ratingsStateListener.asSharedFlow().filter { it.isNotEmpty() }

    private val showRatingSnackbarListener = MutableSharedFlow<String>()
    fun observeRatingSnackbar() = showRatingSnackbarListener.asSharedFlow()

    suspend fun rate(id: Long, oldRating: Float, rating: Float) {
        /*updateRates(id, rating)

        runCatching {
            action(id, rating)
        }.onFailure {
            updateRates(id, oldRating)
        }*/
    }

    private suspend fun action(productId: Long, rating: Float) {
        val response = repository.rateProduct(productId, rating)
        showRatingSnackbarListener.emit(response.message ?: "Вы успешно проголосовали")
        updateRates(productId, rating)
    }

    private suspend fun updateRates(id: Long, rating: Float) {
        ratings[id] = rating
        ratingsStateListener.emit(ratings)
    }

    fun dontCommentProduct(id: Long) {
        val userId = accountManager.fetchAccountId() ?: return
        runCatching {
            scope.launch {
                repository.dontCommentProduct(productId = id, userId = userId)
            }
        }
    }

}

@JsonClass(generateAdapter = true)
data class RatingResponse(
    val status: String?,
    val message: String?,
    val data: Float?
)