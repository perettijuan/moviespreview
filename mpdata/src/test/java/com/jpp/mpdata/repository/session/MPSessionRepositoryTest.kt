package com.jpp.mpdata.repository.session

import com.jpp.mpdata.datasources.session.SessionApi
import com.jpp.mpdata.datasources.session.SessionDb
import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.repository.MPSessionRepository
import com.jpp.mpdomain.repository.MPSessionRepository.SessionData
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class MPSessionRepositoryTest {

    @MockK
    private lateinit var sessionApi: SessionApi
    @RelaxedMockK
    private lateinit var sessionDb: SessionDb

    private lateinit var subject: MPSessionRepository

    @BeforeEach
    fun setUp() {
        subject = MPSessionRepositoryImpl(sessionApi, sessionDb)
    }

    @Test
    fun `Should retrieve current session when present in DB`() {
        var postedData: SessionData? = null
        val currentSession = mockk<Session>()

        every { sessionDb.getSession() } returns currentSession

        subject.data().observeWith { data -> postedData = data }

        subject.getCurrentSession()

        assertTrue(postedData is SessionData.CurrentSession)
        with(postedData as SessionData.CurrentSession) {
            assertEquals(currentSession, data)
        }
    }

    @Test
    fun `Should send NoCurrentSessionAvailable when no session present in DB`() {
        var postedData: SessionData? = null

        every { sessionDb.getSession() } returns null

        subject.data().observeWith { data -> postedData = data }

        subject.getCurrentSession()

        assertTrue(postedData is SessionData.NoCurrentSessionAvailable)
    }

    @Test
    fun `Should create session and store in DB when able to create in API`() {
        var postedData: SessionData? = null
        val createdSession = mockk<Session>()
        val accessToken = mockk<AccessToken>()

        every { sessionApi.createSession(any()) } returns createdSession

        subject.data().observeWith { data -> postedData = data }

        subject.createAndStoreSession(accessToken)

        assertTrue(postedData is SessionData.SessionCreated)
        with(postedData as SessionData.SessionCreated) {
            assertEquals(createdSession, data)
        }
        verify { sessionApi.createSession(accessToken) }
        verify { sessionDb.updateSession(createdSession) }
    }

    @Test
    fun `Should send UnableToCreateSession when API fails`() {
        var postedData: SessionData? = null
        val accessToken = mockk<AccessToken>()

        every { sessionApi.createSession(any()) } returns null

        subject.data().observeWith { data -> postedData = data }

        subject.createAndStoreSession(accessToken)

        assertTrue(postedData is SessionData.UnableToCreateSession)
    }

}