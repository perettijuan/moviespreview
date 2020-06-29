package com.jpp.mpdata.repository.credits

import com.jpp.mpdomain.Credits
import com.jpp.mpdomain.repository.CreditsRepository
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CreditsRepositoryTest {

    @RelaxedMockK
    private lateinit var creditsApi: CreditsApi
    @RelaxedMockK
    private lateinit var creditsDb: CreditsDb

    private val movieId = 22.toDouble()
    private lateinit var subject: CreditsRepository

    @BeforeEach
    fun setUp() {
        subject = CreditsRepositoryImpl(creditsApi, creditsDb)
    }

    @Test
    fun `Should never get from API when data is in cache`() = runBlocking {
        every { creditsDb.getCreditsForMovie(any()) } returns mockk()

        subject.getCreditsForMovie(movieId)

        verify { creditsDb.getCreditsForMovie(movieId) }
        verify(exactly = 0) { creditsApi.getCreditsForMovie(any()) }
        verify(exactly = 0) { creditsDb.storeCredits(any()) }
    }

    @Test
    fun `Should update cache when data retrieved from API`() = runBlocking {
        val credits = mockk<Credits>()
        every { creditsDb.getCreditsForMovie(any()) } returns null
        every { creditsApi.getCreditsForMovie(any()) } returns credits

        subject.getCreditsForMovie(movieId)

        verify { creditsApi.getCreditsForMovie(movieId) }
        verify { creditsDb.storeCredits(credits) }
    }

    @Test
    fun `Should return null when it fails`() = runBlocking {
        every { creditsDb.getCreditsForMovie(any()) } returns null
        every { creditsApi.getCreditsForMovie(any()) } returns null

        val result = subject.getCreditsForMovie(movieId)

        verify { creditsApi.getCreditsForMovie(movieId) }
        verify { creditsDb.getCreditsForMovie(movieId) }
        assertNull(result)
    }
}
