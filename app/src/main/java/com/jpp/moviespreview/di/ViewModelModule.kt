package com.jpp.moviespreview.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jpp.moviespreview.screens.main.MainActivityViewModel
import com.jpp.moviespreview.screens.main.SearchViewViewModel
import com.jpp.moviespreview.screens.main.about.AboutViewModel
import com.jpp.moviespreview.screens.main.credits.CreditsViewModel
import com.jpp.moviespreview.screens.main.details.MovieDetailsViewModel
import com.jpp.moviespreview.screens.main.licenses.LicensesViewModel
import com.jpp.moviespreview.screens.main.licenses.content.LicenseContentViewModel
import com.jpp.moviespreview.screens.main.movies.fragments.PlayingMoviesFragment
import com.jpp.moviespreview.screens.main.movies.fragments.PopularMoviesFragment
import com.jpp.moviespreview.screens.main.movies.fragments.TopRatedMoviesFragment
import com.jpp.moviespreview.screens.main.movies.fragments.UpcomingMoviesFragment
import com.jpp.moviespreview.screens.main.person.PersonViewModel
import com.jpp.moviespreview.screens.main.search.SearchFragmentViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Provides all the ViewModels needed by the appModule.
 */
@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: MPViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(PlayingMoviesFragment.PlayingMoviesFragmentViewModel::class)
    internal abstract fun postPlayingMoviesFragmentViewModel(viewModel: PlayingMoviesFragment.PlayingMoviesFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PopularMoviesFragment.PopularMoviesFragmentViewModel::class)
    internal abstract fun postPopularMoviesFragmentViewModel(viewModel: PopularMoviesFragment.PopularMoviesFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TopRatedMoviesFragment.TopRatedMoviesFragmentViewModel::class)
    internal abstract fun postTopRatedMoviesFragmentViewModel(viewModel: TopRatedMoviesFragment.TopRatedMoviesFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UpcomingMoviesFragment.UpcomingMoviesFragmentViewModel::class)
    internal abstract fun postUpcomingMoviesFragmentViewModel(viewModel: UpcomingMoviesFragment.UpcomingMoviesFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MovieDetailsViewModel::class)
    internal abstract fun postMovieDetailsViewModel(viewModel: MovieDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    internal abstract fun postMainActivityViewModel(viewModel: MainActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchFragmentViewModel::class)
    internal abstract fun postSearchViewModel(viewModel: SearchFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewViewModel::class)
    internal abstract fun postSearchViewViewModel(viewModel: SearchViewViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PersonViewModel::class)
    internal abstract fun postPersonViewModel(viewModel: PersonViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreditsViewModel::class)
    internal abstract fun postCreditsViewModel(viewModel: CreditsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AboutViewModel::class)
    internal abstract fun postAboutViewModel(viewModel: AboutViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LicensesViewModel::class)
    internal abstract fun postLicensesViewModel(viewModel: LicensesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LicenseContentViewModel::class)
    internal abstract fun postLicenseContentViewModel(viewModel: LicenseContentViewModel): ViewModel
}