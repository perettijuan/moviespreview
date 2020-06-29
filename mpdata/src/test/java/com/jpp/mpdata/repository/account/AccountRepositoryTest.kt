package com.jpp.mpdata.repository.account

import com.jpp.mpdata.datasources.account.AccountApi
import com.jpp.mpdata.datasources.account.AccountDb
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mptestutils.InstantTaskExecutorExtension
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class AccountRepositoryTest {

    @RelaxedMockK
    private lateinit var accountApi: AccountApi

    @RelaxedMockK
    private lateinit var accountDb: AccountDb

    private lateinit var subject: AccountRepository

    @BeforeEach
    fun setUp() {
        subject = AccountRepositoryImpl(accountApi, accountDb)
    }

    @Test
    fun `Should not fetch data from API if present in DB`() = runBlocking {
        val expected = mockk<UserAccount>()
        val session = mockk<Session>()

        every { accountDb.getUserAccountInfo() } returns expected

        val actual = subject.getUserAccount(session)

        assertEquals(expected, actual)
        verify(exactly = 0) { accountApi.getUserAccountInfo(any()) }
    }

    @Test
    fun `Should update DB if data fetched from API`() = runBlocking {
        val expected = mockk<UserAccount>()
        val session = mockk<Session>()

        every { accountDb.getUserAccountInfo() } returns null
        every { accountApi.getUserAccountInfo(session) } returns expected

        val actual = subject.getUserAccount(session)

        assertEquals(expected, actual)
        verify { accountApi.getUserAccountInfo(any()) }
        verify { accountDb.storeUserAccountInfo(expected) }
    }
}
