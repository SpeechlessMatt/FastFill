package com.czy4201b.fastfill.feature.update.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UpdateModule {

    @Binds
    @Singleton
    abstract fun bindUpdateRepository(
        gitHubUpdateRepository: GitHubUpdateRepository
    ): UpdateRepository
}