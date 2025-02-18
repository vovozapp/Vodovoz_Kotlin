package com.vodovoz.app.domain.general.use_case

import com.vodovoz.app.domain.general.model.parseCartString
import com.vodovoz.app.domain.general.respository.CartManagerRepository
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddMultipleCartItemsUseCase @Inject constructor(
    private val vodovozServiceRepository: VodovozServiceRepository,
    private val cartManagerRepository: CartManagerRepository,
    private val syncCartDataUseCase: SyncCartDataUseCase,
) : UseCase {

    suspend operator fun invoke(idsWithQuantity: String): Flow<Result<Boolean>> = flow {
        val cartItems = parseCartString(idsWithQuantity)
        val previousItems = cartManagerRepository.getCartItems()
        val cartBatchOperation = cartManagerRepository.addItems(cartItems)


        val cartOperationResult =
            vodovozServiceRepository.addMultipleProductsToCart(idsWithQuantity).first()

        cartOperationResult.onSuccess {
            syncCartDataUseCase(cartBatchOperation.cartVersion)
            emit(Result.success(true))
        }.onFailure {
            cartManagerRepository.replaceItems(previousItems, false)
            emit(Result.success(false))
        }
    }.catch { e ->
        Result.failure<Boolean>(e)
    }


}