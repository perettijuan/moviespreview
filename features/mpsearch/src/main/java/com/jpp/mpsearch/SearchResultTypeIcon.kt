package com.jpp.mpsearch

import androidx.annotation.DrawableRes

/**
 * Represents the icon in the type of the [SearchResultItem].
 */
internal enum class SearchResultTypeIcon(@DrawableRes val iconRes: Int) {
    Movie(R.drawable.ic_clapperboard),
    Person(R.drawable.ic_person_black)
}
