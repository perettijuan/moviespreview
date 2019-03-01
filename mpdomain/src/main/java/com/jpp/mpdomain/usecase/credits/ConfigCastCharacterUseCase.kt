package com.jpp.mpdomain.usecase.credits

import com.jpp.mpdomain.CastCharacter
import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.repository.ConfigurationRepository
import com.jpp.mpdomain.usecase.ConfigureImagePathUseCase

/**
 * Defines a use case that configures the image path of a [CastCharacter]. By default the [CastCharacter.profile_path]
 * property is initialized to a value that does not contains the full path of the image. This use
 * case takes care of adjusting such property based on the provided image size.
 */
interface ConfigCastCharacterUseCase {
    /**
     * Configures the provided [castCharacter] adjusting the profile path image to the provided
     * [targetImageSize].
     * @return a [CastCharacter] with the exact same properties as the provided one, but with the
     * profile image path pointing the correct resource.
     */
    fun configure(targetImageSize: Int, castCharacter: CastCharacter): CastCharacter


    class Impl(private val configurationRepository: ConfigurationRepository) : ConfigCastCharacterUseCase, ConfigureImagePathUseCase() {
        override fun configure(targetImageSize: Int, castCharacter: CastCharacter): CastCharacter {
            return configurationRepository.getAppConfiguration()?.let {
                configureCastCharacter(castCharacter, it.images, targetImageSize)
            } ?: run {
                castCharacter
            }
        }

        private fun configureCastCharacter(castCharacter: CastCharacter, imagesConfig: ImagesConfiguration, targetImageSize: Int): CastCharacter {
            return castCharacter.copy(
                    profile_path = createUrlForPath(castCharacter.profile_path, imagesConfig.base_url, imagesConfig.profile_sizes, targetImageSize)
            )
        }
    }
}