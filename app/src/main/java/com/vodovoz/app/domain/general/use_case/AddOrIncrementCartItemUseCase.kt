package com.vodovoz.app.domain.general.use_case

import com.vodovoz.app.domain.general.model.CartOperation
import com.vodovoz.app.domain.general.model.RequestException
import com.vodovoz.app.domain.general.respository.CartManagerRepository
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AddOrIncrementCartItemUseCase @Inject constructor(
    private val vodovozServiceRepository: VodovozServiceRepository,
    private val cartManagerRepository: CartManagerRepository,
    private val syncCartDataUseCase: SyncCartDataUseCase,
) : UseCase {

    suspend operator fun invoke(productId: Long): Flow<Result<CartOperation>> = flow {
        val cartOperation = cartManagerRepository.incrementItemQuantity(productId)

        val operationFlow = if (cartOperation.newQuantity == 1) {
            vodovozServiceRepository.addProductToCart(productId, cartOperation.newQuantity)
        } else {
            vodovozServiceRepository.updateProductInCart(productId, cartOperation.newQuantity)
        }

        val operationResult = operationFlow.single()

        if (operationResult.isSuccess) {
            syncCartDataUseCase(cartOperation.cartVersion).collect{}
            emit(Result.success(cartOperation))
        } else {
            cartManagerRepository.decrementItemQuantity(productId)
            throw operationResult.exceptionOrNull() ?: RequestException("Unknown error")
        }
    }.catch { e ->
        emit(Result.failure(e))
    }
}
