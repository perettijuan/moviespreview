package com.jpp.mpcredits

/**
 * Represents a person in the credits list. It might be a character or
 * a crew member.
 */
internal data class CreditPerson(
    val id: Double,
    val profilePath: String,
    val title: String,
    val subTitle: String
)
