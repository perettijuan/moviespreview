package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.CastCharacter
import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.repository.ConfigurationRepository

/**
 * Use case to configure the profile path
 */
class ConfigureCastCharacterImagesPathUseCase(
    private val configurationRepository: ConfigurationRepository
) {

    suspend fun execute(castCharacter: CastCharacter): Try<CastCharacter> {
        return configurationRepository.getAppConfiguration()?.let { appConfiguration ->
            Try.Success(castCharacter.configureProfilePath(appConfiguration.images))
        } ?: Try.Failure(Try.FailureCause.Unknown)
    }


    private fun CastCharacter.configureProfilePath(imagesConfig: ImagesConfiguration): CastCharacter {
        return copy(
            profile_path = profile_path.createUrlForPath(
                imagesConfig.base_url,
                imagesConfig.poster_sizes.last()
            )
        )
    }
}