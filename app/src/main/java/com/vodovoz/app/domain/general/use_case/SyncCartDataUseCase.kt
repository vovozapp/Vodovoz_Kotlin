package com.vodovoz.app.domain.general.use_case

import com.vodovoz.app.domain.general.respository.CartDatabaseRepository
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SyncCartDataUseCase(
    private val cartDatabaseRepository: CartDatabaseRepository,
    private val vodovozServiceRepository: VodovozServiceRepository,
) : UseCase {

    operator fun invoke(version: Long): Flow<Result<Boolean>> = flow {
        val firstCurrentVersion = cartDatabaseRepository.getCart().getOrThrow().first.version

        if (firstCurrentVersion > version) return@flow

        //todo - val cart = vodovozServiceRepository.getCart()
        //todo - val secondCurrentVersion = cartDatabaseRepository.getCart().getOrThrow().first.version
        //todo - if (secondCurrentVersion > version) return@flow
        //todo - cartDatabaseRepository.replaceCartItems(cart.items)

        emit(Result.success(true))
    }.catch { e ->
        emit(Result.failure(e))
    }

}