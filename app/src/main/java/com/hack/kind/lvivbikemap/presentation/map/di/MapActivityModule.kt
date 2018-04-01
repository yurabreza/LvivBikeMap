package com.hack.kind.lvivbikemap.presentation.map.di

import com.hack.kind.lvivbikemap.domain.repository.MapDataRepository
import com.hack.kind.lvivbikemap.presentation.map.presenter.MapPresenter
import dagger.Module
import dagger.Provides


@Module
class MapActivityModule {

    @Provides
    @MapActivityScope
    fun provideMapPresenter(repository: MapDataRepository): MapPresenter {
        return MapPresenter(repository)
    }

}