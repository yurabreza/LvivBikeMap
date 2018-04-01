package com.hack.kind.lvivbikemap.app.di

import com.hack.kind.lvivbikemap.app.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(app: App) = app.applicationContext!!
}