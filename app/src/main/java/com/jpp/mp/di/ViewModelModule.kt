package com.jpp.mp.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jpp.mp.common.navigation.NavigationViewModel
import com.jpp.mp.screens.main.MainActivityViewModel
import com.jpp.mp.screens.main.header.NavigationHeaderViewModel
import com.jpp.mp.screens.main.licenses.content.LicenseContentViewModelDeprecated
import com.jpp.mp.screens.main.movies.fragments.PlayingMoviesFragment
import com.jpp.mp.screens.main.movies.fragments.PopularMoviesFragment
import com.jpp.mp.screens.main.movies.fragments.TopRatedMoviesFragment
import com.jpp.mp.screens.main.movies.fragments.UpcomingMoviesFragment
import com.jpp.mpabout.AboutViewModel
import com.jpp.mpabout.licenses.LicensesViewModel
import com.jpp.mpabout.licenses.content.LicenseContentViewModel
import com.jpp.mpaccount.account.UserAccountViewModel
import com.jpp.mpaccount.account.lists.UserMovieListViewModel
import com.jpp.mpaccount.login.LoginViewModel
import com.jpp.mpcredits.CreditsViewModel
import com.jpp.mpmoviedetails.MovieDetailsActionViewModel
import com.jpp.mpmoviedetails.MovieDetailsViewModel
import com.jpp.mpperson.PersonViewModel
import com.jpp.mpsearch.SearchViewModel
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
    @ViewModelKey(MainActivityViewModel::class)
    internal abstract fun postMainActivityViewModel(viewModel: MainActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LicenseContentViewModelDeprecated::class)
    internal abstract fun postLicenseContentViewModelDeprecated(viewModel: LicenseContentViewModelDeprecated): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NavigationHeaderViewModel::class)
    internal abstract fun postNavigationHeaderViewModel(viewModel: NavigationHeaderViewModel): ViewModel

    /*
     * User account feature dependencies.
     */
    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    internal abstract fun getLoginViewModel(viewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserAccountViewModel::class)
    internal abstract fun getUserAccountViewModel(viewModel: UserAccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserMovieListViewModel::class)
    internal abstract fun getUserMovieListViewModel(viewModel: UserMovieListViewModel): ViewModel

    /*
     * Movie details feature dependencies.
     */
    @Binds
    @IntoMap
    @ViewModelKey(MovieDetailsViewModel::class)
    internal abstract fun getMovieDetailsViewModel(viewModel: MovieDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MovieDetailsActionViewModel::class)
    internal abstract fun getMovieDetailsActionViewModel(viewModel: MovieDetailsActionViewModel): ViewModel

    /*
     * Navigation dependencies.
     */
    @Binds
    @IntoMap
    @ViewModelKey(NavigationViewModel::class)
    internal abstract fun getNavigationViewModel(viewModel: NavigationViewModel): ViewModel

    /*
     * Search feature dependencies
     */
    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    internal abstract fun getSearchViewModel(viewModel: SearchViewModel): ViewModel

    /*
     * Person feature dependencies
     */
    @Binds
    @IntoMap
    @ViewModelKey(PersonViewModel::class)
    internal abstract fun getPersonViewModel(viewModel: PersonViewModel): ViewModel

    /*
     * Credits feature dependencies
     */
    @Binds
    @IntoMap
    @ViewModelKey(CreditsViewModel::class)
    internal abstract fun provideCreditsViewModel(viewModel: CreditsViewModel): ViewModel

    /*
     * About feature dependencies
     */
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