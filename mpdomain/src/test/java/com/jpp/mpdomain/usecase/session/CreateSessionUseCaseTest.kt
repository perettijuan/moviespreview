package com.jpp.mpdomain.usecase.session

import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.SessionRepository
import com.jpp.mpdomain.usecase.session.CreateSessionUseCase.CreateSessionResult.*
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CreateSessionUseCaseTest {

    @RelaxedMockK
    private lateinit var sessionRepository: SessionRepository

    @RelaxedMockK
    private lateinit var connectivityRepository: ConnectivityRepository

    private lateinit var subject: CreateSessionUseCase

    @BeforeEach
    fun setUp() {
        subject = CreateSessionUseCase.Impl(sessionRepository, connectivityRepository)
    }

    @Test
    fun `Should check connectivity before creating the session and return ErrorNoConnectivity`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        subject.createSessionWith(mockk()).let { result ->
            verify(exactly = 0) { sessionRepository.createSession(any()) }
            assertEquals(ErrorNoConnectivity, result)
        }
    }

    @Test
    fun `Should return ErrorUnknown when connected to network and an error occurs`() {
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.createSession(any()) } returns null
        val accessToken = mockk<AccessToken>()

        subject.createSessionWith(accessToken).let { result ->
            verify(exactly = 1) { sessionRepository.createSession(accessToken) }
            assertEquals(ErrorUnknown, result)
        }
    }

    @Test
    fun `Should return Success when connected to network and an can create a new session`() {
        val session = Session(success = false, session_id = "anId")
        val accessToken = mockk<AccessToken>()

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.createSession(any()) } returns session

        subject.createSessionWith(accessToken).let { result ->
            verify(exactly = 1) { sessionRepository.createSession(accessToken) }
            assertEquals(ErrorUnknown, result)
        }
    }

    @Test
    fun `Should return Success when connected to network and an can create session`() {
        val session = Session(success = true, session_id = "anId")
        val accessToken = mockk<AccessToken>()

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { sessionRepository.createSession(any()) } returns session

        subject.createSessionWith(accessToken).let { result ->
            verify(exactly = 1) { sessionRepository.createSession(accessToken) }
            assertEquals(Success, result)
        }
    }
}