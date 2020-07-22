<h1 align="center"> MoviesPreview </h1> <br>
<p align="center">
    <img alt="MoviesPreview" title="MoviesPreview" src="https://github.com/perettijuan/moviespreview/blob/develop/app/src/main/ic_launcher_round-web.png" width="450">
</p>

<p align="center">
  All your favorite movies in your pocket.
</p>

<p align="center">
  <a href="https://play.google.com/store/apps/details?id=com.jpp.mp">
    <img alt="Get it on Google Play" title="Google Play" src="https://github.com/perettijuan/moviespreview/blob/develop/art/gPlay.png" width="140">
  </a>
</p>

## Table of contents

* [Introduction](#introduction)
* [Medium](#medium-posts)
* [Features](#features)
* [Libraries and Stack](#libraries-and-stack)
* [License](#license)

## Introduction 

[![Build Status](https://travis-ci.org/perettijuan/moviespreview.svg?branch=master)](https://travis-ci.org/perettijuan/moviespreview)  [![CircleCI](https://circleci.com/gh/perettijuan/moviespreview.svg?style=svg)](https://circleci.com/gh/perettijuan/moviespreview)

MoviesPreview is an Android Application that queries The Movie DB API and presents the data available. It allows you to do things with that data, besides browsing. But mainly, MoviesPreview is an ongoing effort to expose my ideas and to try to stay up to date with the development of Android Applications.


## Medium Posts

  - [Main App Architecture](https://medium.com/@peretti.juan/android-architecture-example-layers-and-modules-19ecbfa57264)
      - [Data Layer](https://medium.com/swlh/data-layer-using-the-repository-pattern-e32b19b04466)
      - [Domain Layer](https://medium.com/@peretti.juan/from-one-god-interactor-to-focused-use-cases-72b51011c0fe)
      - [View Layer](https://medium.com/@peretti.juan/the-view-layer-a-story-of-states-and-views-f5c3ffbac96f)
  - [App Modules](https://medium.com/@peretti.juan/moviespreview-the-android-app-project-modularization-6a9620ec356a)
      - [Navigation - deprecated](https://medium.com/@peretti.juan/moviespreview-modularization-and-navigation-1a9bbeb28f08) 
  - [Integrity](https://medium.com/@peretti.juan/moviespreview-integrity-framework-8c034d2093bf)    

## Features

A few of the things you can do with MoviesPreview:

 * Browse different movies categories: Now Playing, Popular, Upcoming and Top Rated.
 * See movie details and perform different actions: rate the movie, add it to your favorite list and/or add it to your watch list.
 * Search for specific movies and actor/actress.
 * Check the credits in a specific movie.
 * See the bio of an actor/actress/director.
 * Login to your account and check the movies you have in favorites and in watch list.
 
 <p align="center">
  <img src = "https://github.com/perettijuan/moviespreview/blob/develop/art/movieLists.png" width=700>
 </p>
 
 <p align="center">
  <img src = "https://github.com/perettijuan/moviespreview/blob/develop/art/movieDetails.png" width=700>
 </p>
 
 <p align="center">
  <img src = "https://github.com/perettijuan/moviespreview/blob/develop/art/SearchCreditsPerson.png" width=700>
 </p>
 
 <p align="center">
  <img src = "https://github.com/perettijuan/moviespreview/blob/develop/art/account.png" width=700>
 </p>

## Libraries and Stack

 - [The Movie DB API](https://www.themoviedb.org/documentation/api)
 - [Kotlin Standard Library](https://kotlinlang.org/api/latest/jvm/stdlib/index.html)
 - [Android Support Library](https://developer.android.com/topic/libraries/support-library/packages.html)
 - [Android ConstraintLayout Library](https://developer.android.com/training/constraint-layout/index.html)
 - [Android Design Support Library](https://developer.android.com/training/material/design-library.html)
 - [Kotlin Anko](https://github.com/Kotlin/anko)
 - [Dagger 2](https://github.com/codepath/android_guides/wiki/Dependency-Injection-with-Dagger-2)
 - [Gson](https://github.com/google/gson)
 - [Retrofit](http://square.github.io/retrofit/) 
 - [Picasso](https://square.github.io/picasso/) 
 - [Android Room](https://developer.android.com/topic/libraries/architecture/room.html) 
 - [Espresso](https://developer.android.com/training/testing/espresso/index.html) 
 - [MockK](https://github.com/mockk/mockk) 
 
 ## License
 
 [Apache License 2.0](https://github.com/perettijuan/moviespreview/blob/develop/LICENSE)
