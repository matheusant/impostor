package com.game.impostor.di

import com.game.impostor.data.repository.CategoryRepositoryImpl
import com.game.impostor.data.repository.FirebaseAuthRepository
import com.game.impostor.data.repository.ThemeRepositoryImpl
import com.game.impostor.domain.repository.AuthRepository
import com.game.impostor.domain.repository.CategoryRepository
import com.game.impostor.domain.repository.ThemeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindThemeRepository(impl: ThemeRepositoryImpl): ThemeRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: FirebaseAuthRepository): AuthRepository
}
