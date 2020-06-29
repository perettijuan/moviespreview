package com.jpp.mp.di

import com.jpp.mp.MPApp
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

/**
 * Dagger AppComponent interface that defines all the modules that are involved on the
 * dependency injection of the application.
 */
@Singleton
@Component(
    modules = [
        AppModule::class,
        AndroidInjectionModule::class,
        BuildersModule::class,
        NavigationModule::class,
        DomainLayerModule::class,
        DataLayerModule::class]
)
interface AppComponent {

    fun inject(app: MPApp)
}
