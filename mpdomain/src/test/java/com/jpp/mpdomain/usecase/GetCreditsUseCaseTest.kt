package com.jpp.mpdomain.usecase

import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.CastCharacter
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Credits
import com.jpp.mpdomain.ImagesConfiguration
import com.jpp.mpdomain.repository.ConfigurationRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.CreditsRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetCreditsUseCaseTest {

    @MockK
    private lateinit var connectivityRepository: ConnectivityRepository

    @MockK
    private lateinit var creditsRepository: CreditsRepository

    @MockK
    private lateinit var configurationRepository: ConfigurationRepository

    private lateinit var subject: GetCreditsUseCase

    @BeforeEach
    fun setUp() {
        subject = GetCreditsUseCase(
            creditsRepository,
            configurationRepository,
            connectivityRepository
        )
    }

    @Test
    fun `Should fail with no connectivity message`() = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        val actual = subject.execute(1.0)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.NoConnectivity, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should fail with unknown reason`() = runBlocking {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { creditsRepository.getCreditsForMovie(any()) } returns null

        val actual = subject.execute(1.0)

        assertTrue(actual is Try.Failure)
        assertEquals(Try.FailureCause.Unknown, (actual as Try.Failure).cause)
    }

    @Test
    fun `Should retrieve movie credits`() = runBlocking {
        val credits = Credits(
            id = 1.0,
            cast = CAST_CHARACTERS,
            crew = listOf()
        )

        val expected = Credits(
            id = 1.0,
            cast = CONFIGURED_CAST_CHARACTERS,
            crew = listOf()
        )

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        coEvery { creditsRepository.getCreditsForMovie(1.0) } returns credits
        coEvery { configurationRepository.getAppConfiguration() } returns AppConfiguration(
            IMAGES_CONFIG
        )

        val actual = subject.execute(1.0)

        assertTrue(actual is Try.Success)
        assertEquals(expected, actual.getOrNull())
    }

    private companion object {

        private val CAST_CHARACTERS = listOf(
            CastCharacter(
                cast_id = 12.0,
                character = "aCharacter1",
                credit_id = "aCredit1",
                gender = 1,
                id = 1.0,
                name = "aName1",
                order = 1,
                profile_path = "/m110vLaDDOCca4hfOcS5mK5cDke1.jpg"
            ),
            CastCharacter(
                cast_id = 12.0,
                character = "aCharacter2",
                credit_id = "aCredit2",
                gender = 2,
                id = 2.0,
                name = "aName2",
                order = 2,
                profile_path = "/m110vLaDDOCca4hfOcS5mK5cDke2.jpg"
            ),
            CastCharacter(
                cast_id = 12.0,
                character = "aCharacter3",
                credit_id = "aCredit3",
                gender = 3,
                id = 3.0,
                name = "aName3",
                order = 3,
                profile_path = "/m110vLaDDOCca4hfOcS5mK5cDke3.jpg"
            ),
            CastCharacter(
                cast_id = 12.0,
                character = "aCharacter4",
                credit_id = "aCredit4",
                gender = 4,
                id = 4.0,
                name = "aName4",
                order = 4,
                profile_path = "/m110vLaDDOCca4hfOcS5mK5cDke4.jpg"
            )
        )

        private val CONFIGURED_CAST_CHARACTERS = listOf(
            CastCharacter(
                cast_id = 12.0,
                character = "aCharacter1",
                credit_id = "aCredit1",
                gender = 1,
                id = 1.0,
                name = "aName1",
                order = 1,
                profile_path = "baseUrl/original/m110vLaDDOCca4hfOcS5mK5cDke1.jpg"
            ),
            CastCharacter(
                cast_id = 12.0,
                character = "aCharacter2",
                credit_id = "aCredit2",
                gender = 2,
                id = 2.0,
                name = "aName2",
                order = 2,
                profile_path = "baseUrl/original/m110vLaDDOCca4hfOcS5mK5cDke2.jpg"
            ),
            CastCharacter(
                cast_id = 12.0,
                character = "aCharacter3",
                credit_id = "aCredit3",
                gender = 3,
                id = 3.0,
                name = "aName3",
                order = 3,
                profile_path = "baseUrl/original/m110vLaDDOCca4hfOcS5mK5cDke3.jpg"
            ),
            CastCharacter(
                cast_id = 12.0,
                character = "aCharacter4",
                credit_id = "aCredit4",
                gender = 4,
                id = 4.0,
                name = "aName4",
                order = 4,
                profile_path = "baseUrl/original/m110vLaDDOCca4hfOcS5mK5cDke4.jpg"
            )
        )

        private val IMAGES_CONFIG = ImagesConfiguration(
            base_url = "baseUrl/",
            poster_sizes = listOf(
                "w92",
                "w154",
                "w185",
                "w342",
                "w500",
                "w780",
                "original"
            ),
            profile_sizes = listOf(
                "w45",
                "w185",
                "h632",
                "original"
            ),
            backdrop_sizes = listOf(
                "w300",
                "w780",
                "w1280",
                "original"
            )
        )
    }
}
