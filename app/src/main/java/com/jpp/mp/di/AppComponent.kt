package com.jpp.mp.di

import com.jpp.mp.MPApp
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class,
    AndroidInjectionModule::class,
    BuildersModule::class,
    ViewModelModule::class,
    DomainLayerModule::class,
    DataLayerModule::class])
interface AppComponent {

    fun inject(app: MPApp)
}