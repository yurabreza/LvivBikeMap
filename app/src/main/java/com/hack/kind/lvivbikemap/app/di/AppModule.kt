package com.hack.kind.lvivbikemap.app.di

import com.hack.kind.lvivbikemap.app.App
import dagger.Module
import dagger.Provides

@Module
class AppModule {
    @Provides fun provideContext(app: App) = app.applicationContext!!
}