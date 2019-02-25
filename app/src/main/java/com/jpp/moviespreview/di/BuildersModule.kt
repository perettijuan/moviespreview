package com.jpp.moviespreview.di

import com.jpp.moviespreview.screens.main.MainActivity
import com.jpp.moviespreview.screens.main.details.MovieDetailsFragment
import com.jpp.moviespreview.screens.main.movies.fragments.PlayingMoviesFragment
import com.jpp.moviespreview.screens.main.movies.fragments.PopularMoviesFragment
import com.jpp.moviespreview.screens.main.movies.fragments.TopRatedMoviesFragment
import com.jpp.moviespreview.screens.main.movies.fragments.UpcomingMoviesFragment
import com.jpp.moviespreview.screens.main.person.PersonFragment
import com.jpp.moviespreview.screens.main.search.SearchFragment
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
}