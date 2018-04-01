package com.hack.kind.lvivbikemap.data.di

import com.hack.kind.lvivbikemap.data.api.ApiInterface
import com.hack.kind.lvivbikemap.data.repository.MapDataRepositoryImpl
import com.hack.kind.lvivbikemap.domain.repository.MapDataRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun provideMapDataRepository(api: ApiInterface): MapDataRepository {
        return MapDataRepositoryImpl(api)
    }
}
