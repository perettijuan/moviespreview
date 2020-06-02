package com.jpp.mp

import android.app.Activity
import android.app.Application
import com.crashlytics.android.Crashlytics
import com.jpp.mp.di.AppModule
import com.jpp.mp.di.DaggerAppComponent
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.fabric.sdk.android.Fabric
import javax.inject.Inject

/**
 * [Application] definition for Movies Preview.
 * Implements [HasActivityInjector] in order to provide a mechanism to inject dependencies
 * into the MainActivity and the Fragments that the activity uses.
 */
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
        /*
         * Use canary only on demand, since it is becoming too verbose.
         * UPDATE 08/22/2019 ---> executed a canary session and all seems to work OK.
         * launchCanary()
         */
    }

    private fun launchCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector
}
