package com.jpp.mpdomain.usecase.about

sealed class AboutNavigationType {
    object TheMovieDbTermsOfUse : AboutNavigationType()
    object AppCodeRepo : AboutNavigationType()
}