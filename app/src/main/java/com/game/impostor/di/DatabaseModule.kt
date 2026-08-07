package com.game.impostor.di

import android.content.Context
import com.game.impostor.data.local.AppDatabase
import com.game.impostor.data.local.dao.CategoryDao
import com.game.impostor.data.local.dao.RemoteThemeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getInstance(context)

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideRemoteThemeDao(db: AppDatabase): RemoteThemeDao = db.remoteThemeDao()
}
