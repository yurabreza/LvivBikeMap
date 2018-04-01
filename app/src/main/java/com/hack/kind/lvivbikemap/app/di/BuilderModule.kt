package com.hack.kind.lvivbikemap.app.di


import com.hack.kind.lvivbikemap.presentation.map.view.MapActivity
import com.hack.kind.lvivbikemap.presentation.map.di.MapActivityModule
import com.hack.kind.lvivbikemap.presentation.map.di.MapActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BuildersModule {

    @MapActivityScope
    @ContributesAndroidInjector(modules = [MapActivityModule::class])
    abstract fun bindMapActivity(): MapActivity

}