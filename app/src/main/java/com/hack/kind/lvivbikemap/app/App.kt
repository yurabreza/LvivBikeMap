package com.hack.kind.lvivbikemap.app

import android.app.Activity
import android.app.Application
import com.crashlytics.android.Crashlytics
import com.hack.kind.lvivbikemap.app.di.DaggerAppComponent
import com.hack.kind.lvivbikemap.data.di.DaggerDataComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.fabric.sdk.android.BuildConfig
import io.fabric.sdk.android.Fabric
import java.io.File
import javax.inject.Inject


class App : Application(), HasActivityInjector {

    @Inject lateinit var dispatchAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector() = dispatchAndroidInjector

    override fun onCreate() {
        super.onCreate()
        cacheDirect = this.cacheDir
        DaggerAppComponent.builder()
                .dataComponent(DaggerDataComponent.builder().application(this).build())
                .build()
                .inject(this)
        if (!BuildConfig.DEBUG) Fabric.with(this, Crashlytics())
    }

    companion object {
        lateinit var cacheDirect: File
    }
}