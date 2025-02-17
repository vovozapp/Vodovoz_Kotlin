package com.vodovoz.app.domain.general.use_case

import com.vodovoz.app.domain.general.model.CartItemModel
import com.vodovoz.app.domain.general.model.ProductModel
import com.vodovoz.app.domain.general.respository.CartDatabaseRepository
import com.vodovoz.app.domain.general.respository.VodovozServiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AddOrIncrementCartItemUseCase @Inject constructor(
    private val vodovozServiceRepository: VodovozServiceRepository,
    private val cartDatabaseRepository: CartDatabaseRepository,
    private val syncCartDataUseCase: SyncCartDataUseCase,
) : UseCase {

    suspend operator fun invoke(productModel: ProductModel): Flow<Result<Boolean>> = flow {
        val productId = productModel.id
        val newQuantity =
            cartDatabaseRepository.getCartItemById(productModel.id).getOrThrow().quantity.plus(1)
        val items = cartDatabaseRepository.getCart().getOrThrow().second


        cartDatabaseRepository.addItemToCart(CartItemModel(productId, newQuantity)).getOrThrow()
            .also { cartDatabaseRepository.updateVersion().getOrThrow() }


        val cart = cartDatabaseRepository.getCart().getOrThrow().first

        val cartOperationResult = if (newQuantity == 1) {
            vodovozServiceRepository.addProductToCart(productId, newQuantity).first()
        } else {
            vodovozServiceRepository.updateProductInCart(productId, newQuantity).first()
        }


        cartOperationResult.onSuccess {
            syncCartDataUseCase(cart.version)
            emit(Result.success(true))
        }.onFailure {
            cartDatabaseRepository.replaceCartItems(items)
            emit(Result.success(false))
        }
    }.catch { e ->
        Result.failure<Boolean>(e)
    }
}
