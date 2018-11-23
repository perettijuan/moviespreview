package com.jpp.moviespreview.di

import com.jpp.moviespreview.MPApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, AndroidInjectionModule::class, BuildersModule::class, ViewModelModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: MPApp): Builder

        fun build(): AppComponent
    }

    fun inject(app: MPApp)
}