package com.jpp.mpdata.repository.tokens


import com.jpp.mpdata.datasources.tokens.AccessTokenApi
import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.repository.MPAccessTokenRepository
import com.jpp.mpdomain.repository.MPAccessTokenRepository.AccessTokenData
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class MPAccessTokenRepositoryTest {

    @MockK
    private lateinit var accessTokenApi: AccessTokenApi

    private lateinit var subject: MPAccessTokenRepository

    @BeforeEach
    fun setUp() {
        subject = MPAccessTokenRepositoryImpl(accessTokenApi)
    }

    @Test
    fun `Should report no access token available when API call fails`() {
        var actual: AccessTokenData? = null

        every { accessTokenApi.getAccessToken() } returns null

        subject.data().observeWith { data -> actual = data }
        subject.getAccessToken()

        assertTrue(actual is AccessTokenData.NoAccessTokenAvailable)
    }

    @Test
    fun `Should report no access token available when API retrieves an invalid token`() {
        var actual: AccessTokenData? = null
        val accessToken = mockk<AccessToken>()

        every { accessToken.success } returns false
        every { accessTokenApi.getAccessToken() } returns accessToken

        subject.data().observeWith { data -> actual = data }
        subject.getAccessToken()

        assertTrue(actual is AccessTokenData.NoAccessTokenAvailable)
    }

    @Test
    fun `Should report success when API retrieves a valid token`() {
        var actual: AccessTokenData? = null
        val accessToken = mockk<AccessToken>()

        every { accessToken.success } returns true
        every { accessTokenApi.getAccessToken() } returns accessToken

        subject.data().observeWith { data -> actual = data }
        subject.getAccessToken()

        assertTrue(actual is AccessTokenData.Success)
        assertEquals(accessToken, (actual as AccessTokenData.Success).data)
    }

}