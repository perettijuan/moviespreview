package com.jpp.mpdomain.usecase.credits

import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.CastCharacter
import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.repository.ConfigurationRepository
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class)
class ConfigCastCharacterUseCaseTest {

    @RelaxedMockK
    private lateinit var configurationRepository: ConfigurationRepository

    private lateinit var subject: ConfigCastCharacterUseCase

    @BeforeEach
    fun setUp() {
        subject = ConfigCastCharacterUseCase.Impl(configurationRepository)
    }

    @Test
    fun `Should return same search character when unable to fetch app config`() {
        val expectedResult = mockk<CastCharacter>()

        every { configurationRepository.getAppConfiguration() } returns null

        val actualResult = subject.configure(10, expectedResult)

        assertEquals(expectedResult, actualResult)
    }

    @ParameterizedTest
    @MethodSource("params")
    fun `Should configure search result with provided app config`(param: ExecuteConfigTestParameter) {
        val appConfig = mockk<AppConfiguration>()

        every { configurationRepository.getAppConfiguration() } returns appConfig
        every { appConfig.images } returns param.imagesConfig

        val result = subject.configure(param.targetImageSize, param.castCharacter)

        assertEquals(param.expectedProfilePath, result.profile_path)
    }


    data class ExecuteConfigTestParameter(
            val case: String,
            val imagesConfig: ImagesConfiguration,
            val castCharacter: CastCharacter,
            val targetImageSize: Int,
            val expectedProfilePath: String?
    )


    companion object {

        @JvmStatic
        fun params() = listOf(
                ExecuteConfigTestParameter(
                        case = "Should configure path with exact value",
                        imagesConfig = imagesConfig,
                        castCharacter = castCharacter,
                        targetImageSize = 185,
                        expectedProfilePath = "baseUrl/w185/m110vLaDDOCca4hfOcS5mK5cDke.jpg"
                ),
                ExecuteConfigTestParameter(
                        case = "Should configure path with first higher value",
                        imagesConfig = imagesConfig,
                        castCharacter = castCharacter,
                        targetImageSize = 200,
                        expectedProfilePath = "baseUrl/h632/m110vLaDDOCca4hfOcS5mK5cDke.jpg"
                ),
                ExecuteConfigTestParameter(
                        case = "Should configure path with original",
                        imagesConfig = imagesConfig,
                        castCharacter = castCharacter,
                        targetImageSize = 700,
                        expectedProfilePath = "baseUrl/original/m110vLaDDOCca4hfOcS5mK5cDke.jpg"
                )
        )

        private val imagesConfig = ImagesConfiguration(
                base_url = "baseUrl/",
                poster_sizes = listOf("w92",
                        "w154",
                        "w185",
                        "w342",
                        "w500",
                        "w780",
                        "original"),
                profile_sizes = listOf("w45",
                        "w185",
                        "h632",
                        "original"),
                backdrop_sizes = listOf("w300",
                        "w780",
                        "w1280",
                        "original")

        )

        private val castCharacter = CastCharacter(
              cast_id = 12.toDouble(),
                character = "aCharacter",
                credit_id = "aCredit",
                gender = 2,
                id = 22.toDouble(),
                name = "aName",
                order = 2,
                profile_path = "/m110vLaDDOCca4hfOcS5mK5cDke.jpg"
        )
    }
}