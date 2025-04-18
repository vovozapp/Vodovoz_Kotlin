package com.vodovoz.app.data.local_cart_database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vodovoz.app.data.local_cart_database.model.CartEntity
import com.vodovoz.app.data.local_cart_database.model.CartItemEntity


@Database(entities = [CartEntity::class, CartItemEntity::class], exportSchema = false, version = 1)
abstract class LocalCartDatabase : RoomDatabase() {

    abstract fun cartDao(): CartDao

}