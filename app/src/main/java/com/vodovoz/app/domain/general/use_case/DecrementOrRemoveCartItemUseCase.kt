package com.vodovoz.app.domain.general.use_case

import com.vodovoz.app.domain.general.respository.CartManagerRepository
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DecrementOrRemoveCartItemUseCase @Inject constructor(
    private val vodovozServiceRepository: VodovozServiceRepository,
    private val cartManagerRepository: CartManagerRepository,
    private val syncCartDataUseCase: SyncCartDataUseCase,
) : UseCase {

    suspend operator fun invoke(productId: Long): Flow<Result<Boolean>> = flow {

        val cartOperation = cartManagerRepository.decrementItemQuantity(productId)


        val cartOperationResult = if (cartOperation.newQuantity <= 0) {
            vodovozServiceRepository.removeProductFromCart(productId).first()
        } else {
            vodovozServiceRepository.updateProductInCart(productId, cartOperation.newQuantity)
                .first()
        }

        cartOperationResult.onSuccess {
            syncCartDataUseCase(cartOperation.cartVersion)
            emit(Result.success(true))
        }.onFailure {
            cartManagerRepository.incrementItemQuantity(productId, false)
            emit(Result.success(false))
        }


    }.catch { e ->
        emit(Result.failure(e))
    }
}