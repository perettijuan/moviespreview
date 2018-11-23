package com.jpp.moviespreview.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jpp.moviespreview.screens.main.movies.MoviesFragmentViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Provides all the ViewModels needed by the application.
 */
@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: MPViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MoviesFragmentViewModel::class)
    internal abstract fun postMoviesFragmentViewModel(viewModel: MoviesFragmentViewModel): ViewModel

    // Add more view models here
}