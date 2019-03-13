package com.jpp.moviespreview

import android.app.Activity
import android.app.Application
import com.jpp.moviespreview.di.AppModule
import com.jpp.moviespreview.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject
import com.crashlytics.android.Crashlytics
import com.squareup.leakcanary.LeakCanary
import io.fabric.sdk.android.Fabric



open class MPApp : Application(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>


    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
                .inject(this)

        Fabric.with(this, Crashlytics())
        launchCanary()
    }

    private fun launchCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector
}