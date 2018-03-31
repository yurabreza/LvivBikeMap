package com.hack.kind.lvivbikemap.app.di

import com.hack.kind.lvivbikemap.app.App
import com.hack.kind.lvivbikemap.data.di.DataComponent
import com.hack.kind.lvivbikemap.data.di.DataScope
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule

@DataScope
@Component(dependencies = [DataComponent::class],
        modules = [AndroidSupportInjectionModule::class, AppModule::class, BuildersModule::class])
interface AppComponent {
    fun inject(app: App)
}
