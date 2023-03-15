package com.vodovoz.app.common.like

import com.vodovoz.app.data.local.LocalDataSource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class LikeManager @Inject constructor(
    private val repository: LikeRepository,
    private val localDataSource: LocalDataSource
) {

    /*private val likes = ConcurrentHashMap<Long, Boolean>()
    private val likesStateListener = MutableSharedFlow<Map<Long, Boolean>>(replay = 1)

    fun observeLikes() = likesStateListener.asSharedFlow().filter { it.isNotEmpty() }

    suspend fun like(id: Long, isFavorite: Boolean) {
        updateLikes(id, !isFavorite)

        runCatching {
            action(id, isFavorite)
        }.onFailure {
            updateLikes(id, isFavorite)
        }
    }

    private suspend fun action(id: Long, isFavorite: Boolean) {
        return if (isFavorite) {
            repository.dislike(id)
            updateLikes(id, false)
        } else {
            repository.like(id)
            updateLikes(id, true)
        }
    }

    private suspend fun updateLikes(id: Long, state: Boolean) {
        likes[id] = state
        likesStateListener.emit(likes)
    }*/

}