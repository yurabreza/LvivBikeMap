package com.hack.kind.lvivbikemap.data.di

import com.hack.kind.lvivbikemap.data.api.ApiInterface
import com.hack.kind.lvivbikemap.data.repository.MapDataRepositoryImpl
import com.hack.kind.lvivbikemap.data.repository.UserDataRepositoryImpl
import com.hack.kind.lvivbikemap.domain.repository.MapDataRepository
import com.hack.kind.lvivbikemap.domain.repository.UserDataRepository
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

    @Provides
    @Singleton
    fun provideUserDataRepository(api: ApiInterface): UserDataRepository {
        return UserDataRepositoryImpl(api)
    }

}
