package com.vodovoz.app.core.storage

import android.app.Application
import com.vodovoz.app.common.datastore.DataStoreRepository
import com.vodovoz.app.common.datastore.DataStoreRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class StorageModule {

    @Provides
    @Singleton
    fun providesDataStore(context: Application): DataStoreRepository {
        return DataStoreRepositoryImpl(context)
    }

}