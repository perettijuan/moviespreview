package com.jpp.mpcredits

/**
 * Provides navigation for the credits module.
 */
interface CreditNavigator {

    fun navigateToCreditDetail(
        personId: String,
        personImageUrl: String,
        personName: String
    )
}
