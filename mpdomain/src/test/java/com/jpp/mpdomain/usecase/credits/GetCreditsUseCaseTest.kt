package com.jpp.mpdomain.usecase.credits

import com.jpp.mpdomain.CastCharacter
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Credits
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.CreditsRepository
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetCreditsUseCaseTest {

    @RelaxedMockK
    private lateinit var credRepository: CreditsRepository
    @RelaxedMockK
    private lateinit var connectivityRepository: ConnectivityRepository

    private lateinit var subject: GetCreditsUseCase

    @BeforeEach
    fun setUp() {
        subject = GetCreditsUseCase.Impl(credRepository, connectivityRepository)
    }

    @Test
    fun `Should check connectivity before fetching credits and return ErrorNoConnectivity`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        subject.getCreditsForMovie(1.toDouble()).let { result ->
            verify(exactly = 0) { credRepository.getCreditsForMovie(any()) }
            assertEquals(GetCreditsResult.ErrorNoConnectivity, result)
        }
    }

    @Test
    fun `Should return ErrorUnknown when connected to network and an error occurs`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { credRepository.getCreditsForMovie(any()) } returns null

        subject.getCreditsForMovie(1.toDouble()).let { result ->
            verify(exactly = 1) { credRepository.getCreditsForMovie(any()) }
            assertEquals(GetCreditsResult.ErrorUnknown, result)
        }
    }

    @Test
    fun `Should return Success when connected to network and an can get credits`() {
        val credits = Credits(
                id = 22.toDouble(),
                cast = listOf(),
                crew = listOf()
        )
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { credRepository.getCreditsForMovie(any()) } returns credits

        subject.getCreditsForMovie(1.toDouble()).let { result ->
            verify(exactly = 1) { credRepository.getCreditsForMovie(any()) }
            assertTrue(result is GetCreditsResult.Success)
            assertEquals((result as GetCreditsResult.Success).credits, credits)
        }
    }

    @Test
    fun `Should return sorted cast ascending by default`() {
        val credits = Credits(
                id = 22.toDouble(),
                cast = unSortedCast,
                crew = listOf()
        )
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { credRepository.getCreditsForMovie(any()) } returns credits

        subject.getCreditsForMovie(1.toDouble()).let { result ->

            result as GetCreditsResult.Success

            assertEquals(1, result.credits.cast[0].order)
            assertEquals(2, result.credits.cast[1].order)
            assertEquals(3, result.credits.cast[2].order)
            assertEquals(4, result.credits.cast[3].order)
        }
    }

    @Test
    fun `Should return sorted cast descending`() {
        val credits = Credits(
                id = 22.toDouble(),
                cast = unSortedCast,
                crew = listOf()
        )
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { credRepository.getCreditsForMovie(any()) } returns credits

        subject.getCreditsForMovie(1.toDouble(), GetCreditsUseCase.Ordering.Descending).let { result ->

            result as GetCreditsResult.Success

            assertEquals(4, result.credits.cast[0].order)
            assertEquals(3, result.credits.cast[1].order)
            assertEquals(2, result.credits.cast[2].order)
            assertEquals(1, result.credits.cast[3].order)
        }
    }


    private val unSortedCast by lazy {
        listOf(
                CastCharacter(
                        cast_id = 12.toDouble(),
                        character = "aCharacter",
                        credit_id = "aCredit",
                        gender = 1,
                        id = 1.toDouble(),
                        name = "aName",
                        order = 3,
                        profile_path = "/m110vLaDDOCca4hfOcS5mK5cDke.jpg"
                ),
                CastCharacter(
                        cast_id = 12.toDouble(),
                        character = "aCharacter",
                        credit_id = "aCredit",
                        gender = 1,
                        id = 2.toDouble(),
                        name = "aName",
                        order = 1,
                        profile_path = "/m110vLaDDOCca4hfOcS5mK5cDke.jpg"
                ),
                CastCharacter(
                        cast_id = 12.toDouble(),
                        character = "aCharacter",
                        credit_id = "aCredit",
                        gender = 1,
                        id = 3.toDouble(),
                        name = "aName",
                        order = 2,
                        profile_path = "/m110vLaDDOCca4hfOcS5mK5cDke.jpg"
                ),
                CastCharacter(
                        cast_id = 12.toDouble(),
                        character = "aCharacter",
                        credit_id = "aCredit",
                        gender = 1,
                        id = 4.toDouble(),
                        name = "aName",
                        order = 4,
                        profile_path = "/m110vLaDDOCca4hfOcS5mK5cDke.jpg"
                )
        )
    }
}