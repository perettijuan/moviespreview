package com.jpp.mp.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jpp.mp.common.navigation.NavigationViewModel
import com.jpp.mp.main.MainActivityViewModel
import com.jpp.mp.main.header.NavigationHeaderViewModel
import com.jpp.mpabout.AboutViewModel
import com.jpp.mpabout.licenses.LicensesViewModel
import com.jpp.mpabout.licenses.content.LicenseContentViewModel
import com.jpp.mpaccount.account.UserAccountViewModel
import com.jpp.mpaccount.account.lists.UserMovieListViewModel
import com.jpp.mpaccount.login.LoginViewModel
import com.jpp.mpcredits.CreditsViewModel
import com.jpp.mpmoviedetails.rates.RateMovieViewModel
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

    /*
     * Main module dependencies.
     */
    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    internal abstract fun postMainActivityViewModel(viewModel: MainActivityViewModel): ViewModel

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
    @ViewModelKey(RateMovieViewModel::class)
    internal abstract fun getRateMovieViewModel(viewModel: RateMovieViewModel): ViewModel

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
