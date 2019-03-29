package com.jpp.mp.di

import com.jpp.mp.screens.main.MainActivity
import com.jpp.mp.screens.main.about.AboutFragment
import com.jpp.mp.screens.main.account.AccountFragment
import com.jpp.mp.screens.main.credits.CreditsFragment
import com.jpp.mp.screens.main.details.MovieDetailsFragment
import com.jpp.mp.screens.main.header.NavigationHeaderFragment
import com.jpp.mp.screens.main.licenses.LicensesFragment
import com.jpp.mp.screens.main.licenses.content.LicenseContentFragment
import com.jpp.mp.screens.main.movies.fragments.PlayingMoviesFragment
import com.jpp.mp.screens.main.movies.fragments.PopularMoviesFragment
import com.jpp.mp.screens.main.movies.fragments.TopRatedMoviesFragment
import com.jpp.mp.screens.main.movies.fragments.UpcomingMoviesFragment
import com.jpp.mp.screens.main.person.PersonFragment
import com.jpp.mp.screens.main.search.SearchFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BuildersModule {
    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity

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
    abstract fun bindLicensesContentFragment(): LicenseContentFragment

    @ContributesAndroidInjector
    abstract fun bindNavigationHeaderFragment(): NavigationHeaderFragment

    @ContributesAndroidInjector
    abstract fun bindAccountFragment() : AccountFragment
}