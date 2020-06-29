package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.CastCharacter
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Credits
import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.repository.ConfigurationRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.CreditsRepository

/**
 * Use case to retrieve the [Credits] of a particular movie.
 */
class GetCreditsUseCase(
    private val creditsRepository: CreditsRepository,
    private val configurationRepository: ConfigurationRepository,
    private val connectivityRepository: ConnectivityRepository
) {

    suspend fun execute(movieId: Double): Try<Credits> {
        return when (connectivityRepository.getCurrentConnectivity()) {
            is Connectivity.Disconnected -> Try.Failure(Try.FailureCause.NoConnectivity)
            is Connectivity.Connected -> creditsRepository.getCreditsForMovie(movieId)
                ?.let { credits ->
                    Try.Success(credits.copy(cast = credits.cast.map { character ->
                        configureCharacterProfilePath(
                            character
                        )
                    }))
                } ?: Try.Failure(Try.FailureCause.Unknown)
        }
    }

    private suspend fun configureCharacterProfilePath(castCharacter: CastCharacter): CastCharacter {
        return configurationRepository.getAppConfiguration()?.let { appConfiguration ->
            castCharacter.configureProfilePath(appConfiguration.images)
        } ?: castCharacter
    }

    private fun CastCharacter.configureProfilePath(imagesConfig: ImagesConfiguration): CastCharacter {
        return copy(
            profile_path = profile_path.createUrlForPath(
                imagesConfig.base_url,
                imagesConfig.profile_sizes.last()
            )
        )
    }
}
