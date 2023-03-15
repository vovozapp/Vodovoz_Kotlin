package com.vodovoz.app.common.like

import com.vodovoz.app.common.account.data.AccountLocal
import com.vodovoz.app.common.like.data.LikeLocal
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class LikeManager @Inject constructor(
    private val repository: LikeRepository,
    private val accountLocal: AccountLocal
) {

    private val likes = ConcurrentHashMap<Long, Boolean>()
    private val likesStateListener = MutableSharedFlow<Map<Long, Boolean>>(replay = 1)

    fun observeLikes() = likesStateListener.asSharedFlow().filter { it.isNotEmpty() }

    suspend fun like(id: Long, isFavorite: Boolean) {
        updateLikes(id, !isFavorite)

        val userId = accountLocal.fetchUserId()

        if (userId != null) {
            runCatching {
                action(id, userId, isFavorite)
            }.onFailure {
                updateLikes(id, isFavorite)
            }
        }
    }

    private suspend fun action(productId: Long, userId: Long, isFavorite: Boolean) {
        return if (isFavorite) {
            repository.dislike(productId, userId)
            updateLikes(productId, false)
        } else {
            repository.like(listOf(productId), userId)
            updateLikes(productId, true)
        }
    }

    private suspend fun updateLikes(id: Long, state: Boolean) {
        likes[id] = state
        likesStateListener.emit(likes)
    }

}