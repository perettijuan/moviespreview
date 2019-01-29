package com.jpp.moviespreview.di

import android.app.Activity
import androidx.fragment.app.Fragment
import com.jpp.moviespreview.screens.main.movies.fragments.PlayingMoviesFragment
import dagger.android.AndroidInjector
import dagger.android.AndroidInjector.Factory
import dagger.android.DispatchingAndroidInjector
import dagger.android.DispatchingAndroidInjector_Factory
import javax.inject.Provider


/**
 * Source -> https://proandroiddev.com/activity-espresso-test-with-daggers-android-injector-82f3ee564aa4
 */
inline fun <reified T : Activity> createFakeActivityInjector(crossinline block: T.() -> Unit)
        : DispatchingAndroidInjector<Activity> {
    val injector = AndroidInjector<Activity> { instance ->
        if (instance is T) {
            instance.block()
        }
    }
    val factory = AndroidInjector.Factory<Activity> { injector }
    val map = mapOf(Pair<Class<out Activity>, Provider<Factory<out Activity>>>(T::class.java, Provider { factory }))
    return DispatchingAndroidInjector_Factory.newDispatchingAndroidInjector(map)
}


inline fun <reified T : Fragment> createFakeFragmentInjector(crossinline block: T.() -> Unit)
        : DispatchingAndroidInjector<Fragment> {
    val injector = AndroidInjector<Fragment> { instance ->
        if (instance is T) {
            instance.block()
        }
    }
    val factory = AndroidInjector.Factory<Fragment> { injector }
    val map = mapOf(Pair<Class<out Fragment>, Provider<Factory<out Fragment>>>(T::class.java, Provider { factory }))
    return DispatchingAndroidInjector_Factory.newDispatchingAndroidInjector(map)
}