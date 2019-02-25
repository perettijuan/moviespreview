package com.jpp.moviespreview

import android.app.Activity
import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.jpp.moviespreview.di.AppModule
import com.jpp.moviespreview.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

open class MPApp : Application(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>


    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
                .inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector
}