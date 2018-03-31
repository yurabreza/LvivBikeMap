package com.hack.kind.lvivbikemap.app.di


import com.hack.kind.lvivbikemap.presentation.main.view.MainActivity
import com.hack.kind.lvivbikemap.presentation.main.di.MainActivityModule
import com.hack.kind.lvivbikemap.presentation.main.di.MainActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BuildersModule {

    @MainActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun bindMainActivity(): MainActivity

}