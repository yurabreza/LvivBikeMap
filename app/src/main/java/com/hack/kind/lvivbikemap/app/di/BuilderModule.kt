package com.hack.kind.lvivbikemap.app.di


import com.hack.kind.lvivbikemap.MainActivityModule
import com.hack.kind.lvivbikemap.MainActivityScope
import com.hack.kind.lvivbikemap.MapActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BuildersModule {

    @MainActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun bindMainActivity(): MapActivity

}