package com.vodovoz.app.common.resources

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

interface ResourcesProvider {
    fun getString(@StringRes resId: Int, vararg args: Any): String

    fun getDrawable(@DrawableRes id: Int): Drawable
}

@Singleton
class ResourcesProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ResourcesProvider {

    override fun getString(@StringRes resId: Int, vararg args: Any): String {
        return context.getString(resId, *args)
    }

    override fun getDrawable(@DrawableRes id: Int): Drawable {
        return context.getDrawable(id) ?: ColorDrawable()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class StringProviderModule {

    @Binds
    @Singleton
    abstract fun provideStringProvider(
        resourcesProvider: ResourcesProviderImpl,
    ): ResourcesProvider
}