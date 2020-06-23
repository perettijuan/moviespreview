package com.jpp.mp.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jpp.mp.common.navigation.NavigationViewModel
import com.jpp.mp.main.MainActivityViewModel
import com.jpp.mp.main.header.NavigationHeaderViewModel
import com.jpp.mpaccount.account.UserAccountViewModel
import com.jpp.mpaccount.account.lists.UserMovieListViewModel
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
    @ViewModelKey(UserAccountViewModel::class)
    internal abstract fun getUserAccountViewModel(viewModel: UserAccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UserMovieListViewModel::class)
    internal abstract fun getUserMovieListViewModel(viewModel: UserMovieListViewModel): ViewModel

    /*
     * Navigation dependencies.
     */
    @Binds
    @IntoMap
    @ViewModelKey(NavigationViewModel::class)
    internal abstract fun getNavigationViewModel(viewModel: NavigationViewModel): ViewModel
}
