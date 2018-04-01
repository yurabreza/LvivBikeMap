package com.hack.kind.lvivbikemap.presentation.main.di

import com.hack.kind.lvivbikemap.domain.repository.MapDataRepository
import com.hack.kind.lvivbikemap.presentation.main.presenter.MainPresenter
import dagger.Module
import dagger.Provides


@Module
class MainActivityModule {

    @Provides
    @MainActivityScope
    fun provideMainPresenter(repository: MapDataRepository): MainPresenter {
        return MainPresenter(repository)
    }

}