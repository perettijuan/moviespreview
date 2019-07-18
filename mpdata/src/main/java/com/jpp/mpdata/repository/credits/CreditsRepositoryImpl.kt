package com.jpp.mpdata.repository.credits

import com.jpp.mpdomain.Credits
import com.jpp.mpdomain.repository.CreditsRepository

class CreditsRepositoryImpl(private val creditsApi: CreditsApi,
                            private val creditsDb: CreditsDb) : CreditsRepository {

    override fun getCreditsForMovie(movieId: Double): Credits? {
        return creditsDb.getCreditsForMovie(movieId) ?: run {
            creditsApi.getCreditsForMovie(movieId)?.also {
                creditsDb.storeCredits(it)
            }
        }
    }

    override fun flushCreditsData() {
        creditsDb.clearAllData()
    }
}