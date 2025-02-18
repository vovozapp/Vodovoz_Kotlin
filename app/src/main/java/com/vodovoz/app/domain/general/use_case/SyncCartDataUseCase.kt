package com.vodovoz.app.domain.general.use_case

import com.vodovoz.app.domain.general.respository.CartManagerRepository
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncCartDataUseCase @Inject constructor(
    private val cartManagerRepository: CartManagerRepository,
    private val vodovozServiceRepository: VodovozServiceRepository,
) : UseCase {

    operator fun invoke(version: Long): Flow<Result<Unit>> = flow {
        //todo - val cart = vodovozServiceRepository.getCart()

        val currentVersion = cartManagerRepository.getCartVersion()

        if (currentVersion > version) return@flow
        //todo - cartDatabaseRepository.replaceCartItems(cart.items)

        emit(Result.success(Unit))
    }.catch { e ->
        emit(Result.failure(e))
    }

}