package com.jpp.mpperson

import android.os.Bundle

/**
 * The initialization parameter for the [PersonViewModel.onInit] method.
 */
internal data class PersonParam(
    val personId: Double,
    val personName: String,
    val imageUrl: String
) {
    companion object {
        fun fromArguments(arguments: Bundle?) = PersonParam(
            NavigationPerson.personId(arguments).toDouble(),
            NavigationPerson.personName(arguments),
            NavigationPerson.personImageUrl(arguments)
        )
    }
}
