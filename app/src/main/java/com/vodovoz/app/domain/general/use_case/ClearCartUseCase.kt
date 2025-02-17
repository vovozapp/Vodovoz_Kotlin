package com.vodovoz.app.domain.general.use_case

import com.vodovoz.app.domain.general.respository.CartDatabaseRepository
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClearCartUseCase @Inject constructor(
    private val vodovozServiceRepository: VodovozServiceRepository,
    private val cartDatabaseRepository: CartDatabaseRepository,
    private val syncCartDataUseCase: SyncCartDataUseCase,
): UseCase {

    suspend operator fun invoke(): Flow<Result<Boolean>> = flow {
        val previousItems = cartDatabaseRepository.getCart().getOrThrow().second
        cartDatabaseRepository.clearCart().getOrThrow()
            .also { cartDatabaseRepository.updateVersion().getOrThrow() }

        val cart = cartDatabaseRepository.getCart().getOrThrow().first

        val cartOperationResult = vodovozServiceRepository.clearCart().first()
        cartOperationResult.onSuccess {
            syncCartDataUseCase(cart.version)
            emit(Result.success(true))
        }.onFailure {
            cartDatabaseRepository.replaceCartItems(previousItems)
            emit(Result.success(false))
        }
        
        
    }.catch { e ->
        emit(Result.failure(e))
    }


}