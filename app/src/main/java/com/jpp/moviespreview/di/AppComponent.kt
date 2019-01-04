package com.jpp.moviespreview.di

import com.jpp.moviespreview.MPApp
import com.jpp.mpdata.DataLayerModule
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