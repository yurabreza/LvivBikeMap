package com.hack.kind.lvivbikemap.data.di

import com.google.gson.Gson
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
    fun provideMapDataRepository(api: ApiInterface, gson: Gson): MapDataRepository {
        return MapDataRepositoryImpl(api, gson)
    }

    @Provides
    @Singleton
    fun provideUserDataRepository(api: ApiInterface): UserDataRepository {
        return UserDataRepositoryImpl(api)
    }

}
