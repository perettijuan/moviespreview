package com.jpp.mp.di

import com.jpp.mp.main.MainActivity
import com.jpp.mp.main.header.NavigationHeaderFragment
import com.jpp.mp.main.movies.fragments.PlayingMoviesFragment
import com.jpp.mp.main.movies.fragments.PopularMoviesFragment
import com.jpp.mp.main.movies.fragments.TopRatedMoviesFragment
import com.jpp.mp.main.movies.fragments.UpcomingMoviesFragment
import com.jpp.mpabout.AboutFragment
import com.jpp.mpabout.licenses.LicensesFragment
import com.jpp.mpabout.licenses.content.LicenseContentFragment
import com.jpp.mpaccount.account.UserAccountFragment
import com.jpp.mpaccount.account.lists.UserMovieListFragment
import com.jpp.mpaccount.login.LoginFragment
import com.jpp.mpcredits.CreditsFragment
import com.jpp.mpmoviedetails.MovieDetailsFragment
import com.jpp.mpmoviedetails.rates.RateMovieDialogFragment
import com.jpp.mpperson.PersonFragment
import com.jpp.mpsearch.SearchActivity
import com.jpp.mpsearch.SearchFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Dagger module used to bind the [MainActivity] and all the Fragments in the application
 * with the dependencies they need.
 */
@Module
abstract class BuildersModule {
    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun bindSearchActivity(): SearchActivity

    @ContributesAndroidInjector
    abstract fun bindPlayingMoviesFragment(): PlayingMoviesFragment

    @ContributesAndroidInjector
    abstract fun bindPopularMoviesFragment(): PopularMoviesFragment

    @ContributesAndroidInjector
    abstract fun bindTopRatedMoviesFragment(): TopRatedMoviesFragment

    @ContributesAndroidInjector
    abstract fun bindUpcomingMoviesFragment(): UpcomingMoviesFragment

    @ContributesAndroidInjector
    abstract fun bindMovieDetailsFragment(): MovieDetailsFragment

    @ContributesAndroidInjector
    abstract fun bindNavigationHeaderFragment(): NavigationHeaderFragment

    @ContributesAndroidInjector
    abstract fun bindLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun bindUserAccountFragment(): UserAccountFragment

    @ContributesAndroidInjector
    abstract fun bindUserMovieListFragment(): UserMovieListFragment

    @ContributesAndroidInjector
    abstract fun bindSearchFragment(): SearchFragment

    @ContributesAndroidInjector
    abstract fun bindPersonFragment(): PersonFragment

    @ContributesAndroidInjector
    abstract fun bindCreditsFragment(): CreditsFragment

    @ContributesAndroidInjector
    abstract fun bindAboutFragment(): AboutFragment

    @ContributesAndroidInjector
    abstract fun bindLicensesFragment(): LicensesFragment

    @ContributesAndroidInjector
    abstract fun bindLicenseContentFragment(): LicenseContentFragment

    @ContributesAndroidInjector
    abstract fun bindRateMovieDialogFragment(): RateMovieDialogFragment
}
