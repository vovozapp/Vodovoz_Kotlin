package com.vodovoz.app.domain.general.use_case

import javax.inject.Inject
import javax.inject.Singleton


//todo - when you going to make clean arch
@Singleton
class CartUseCases @Inject constructor(
    val addOrIncrementCartItem: AddOrIncrementCartItemUseCase,
    val removeOrDecrementCartItem: DecrementOrRemoveCartItemUseCase,
    val addMultipleCartItems: AddMultipleCartItemsUseCase,
    val clearCart: ClearCartUseCase,
    val syncCartDataUseCase: SyncCartDataUseCase
)