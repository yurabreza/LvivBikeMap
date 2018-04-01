package com.hack.kind.lvivbikemap.data.di

import com.hack.kind.lvivbikemap.app.App
import com.hack.kind.lvivbikemap.app.di.AppModule
import com.hack.kind.lvivbikemap.domain.repository.MapDataRepository
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, DataModule::class])
interface DataComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: App): Builder

        fun build(): DataComponent
    }

    fun getMapDataRepository(): MapDataRepository
}