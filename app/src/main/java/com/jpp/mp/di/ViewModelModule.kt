package com.jpp.mp.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jpp.mp.screens.main.MainActivityViewModel
import com.jpp.mp.screens.main.RefreshAppViewModel
import com.jpp.mp.screens.main.SearchViewViewModel
import com.jpp.mp.screens.main.about.AboutViewModel
import com.jpp.mp.screens.main.account.AccountFavoriteMoviesViewModel
import com.jpp.mp.screens.main.account.AccountViewModel
import com.jpp.mp.screens.main.credits.CreditsViewModel
import com.jpp.mp.screens.main.details.MovieActionsViewModel
import com.jpp.mp.screens.main.details.MovieDetailsViewModel
import com.jpp.mp.screens.main.favorites.UserMoviesViewModel
import com.jpp.mp.screens.main.header.NavigationHeaderViewModel
import com.jpp.mp.screens.main.licenses.LicensesViewModel
import com.jpp.mp.screens.main.licenses.content.LicenseContentViewModel
import com.jpp.mp.screens.main.movies.fragments.PlayingMoviesFragment
import com.jpp.mp.screens.main.movies.fragments.PopularMoviesFragment
import com.jpp.mp.screens.main.movies.fragments.TopRatedMoviesFragment
import com.jpp.mp.screens.main.movies.fragments.UpcomingMoviesFragment
import com.jpp.mp.screens.main.person.PersonViewModel
import com.jpp.mp.screens.main.search.SearchFragmentViewModel
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
    @ViewModelKey(MovieActionsViewModel::class)
    internal abstract fun postMovieActionsViewModel(viewModel: MovieActionsViewModel): ViewModel

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

    @Binds
    @IntoMap
    @ViewModelKey(RefreshAppViewModel::class)
    internal abstract fun postRefreshAppViewModel(viewModel: RefreshAppViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NavigationHeaderViewModel::class)
    internal abstract fun postNavigationHeaderViewModel(viewModel: NavigationHeaderViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    internal abstract fun getAccountViewModel(viewModel: AccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AccountFavoriteMoviesViewModel::class)
    internal abstract fun getAccountFavoriteMoviesViewModel(viewModel: AccountFavoriteMoviesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserMoviesViewModel::class)
    internal abstract fun getFavoriteMoviesViewModel(viewModel: UserMoviesViewModel): ViewModel
}