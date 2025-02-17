package com.vodovoz.app.data.local_cart_database.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vodovoz.app.data.local_cart_database.CartDao
import com.vodovoz.app.data.local_cart_database.LocalCartDatabase
import com.vodovoz.app.data.local_cart_database.repository.CartDatabaseRepositoryImpl
import com.vodovoz.app.domain.general.respository.CartDatabaseRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CartDatabaseModule {

    @Binds
    @Singleton
    abstract fun bindCartDatabaseRepository(
        cartDatabaseRepository: CartDatabaseRepositoryImpl
    ): CartDatabaseRepository

    companion object {

        @Provides
        @Singleton
        fun provideCartDatabase(
            @ApplicationContext
            context: Context
        ): LocalCartDatabase {
            return Room.databaseBuilder(
                context,
                LocalCartDatabase::class.java, "cart_database"
            ).addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    db.execSQL("INSERT INTO cart (id, version) VALUES (0, 1)")
                }
            }).build()
        }

        @Provides
        fun provideCartDao(database: LocalCartDatabase): CartDao {
            return database.cartDao()
        }

    }

}