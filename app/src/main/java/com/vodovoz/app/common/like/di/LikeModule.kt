package com.vodovoz.app.common.like.di

import com.vodovoz.app.common.like.LikeRepository
import com.vodovoz.app.common.like.LikeRepositoryImpl
import com.vodovoz.app.common.like.api.LikeApi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
abstract class LikeModule {

    companion object {

        @Provides
        fun provideLikeApi(@Named("main") retrofit: Retrofit): LikeApi = retrofit.create()
    }

    @Binds
    abstract fun bindLikeRepository(impl: LikeRepositoryImpl): LikeRepository

}