package com.vodovoz.app.core.network

import com.vodovoz.app.data.local.LocalData
import com.vodovoz.app.data.local.LocalDataSource
import com.vodovoz.app.data.remote.RemoteData
import com.vodovoz.app.data.remote.RemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindLocalDataSource(impl: LocalData): LocalDataSource

    @Binds
    abstract fun bindRemoteDataSource(impl: RemoteData): RemoteDataSource
}