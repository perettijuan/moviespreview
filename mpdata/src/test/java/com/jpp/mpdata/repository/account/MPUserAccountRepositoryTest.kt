package com.jpp.mpdata.repository.account


import com.jpp.mpdata.datasources.account.AccountApi
import com.jpp.mpdata.datasources.account.AccountDb
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.MPUserAccountRepository
import com.jpp.mpdomain.repository.MPUserAccountRepository.UserAccountData
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
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

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class MPUserAccountRepositoryTest {

    @RelaxedMockK
    private lateinit var accountApi: AccountApi
    @RelaxedMockK
    private lateinit var accountDb: AccountDb

    private lateinit var subject: MPUserAccountRepository

    @BeforeEach
    fun setUp() {
        subject = MPUserAccountRepositoryImpl(accountApi, accountDb)
    }

    @Test
    fun `Should not fetch data from API if present in DB`() {
        var actual: UserAccountData? = null
        val expected = mockk<UserAccount>()
        val session = mockk<Session>()

        every { accountDb.getUserAccountInfo() } returns expected

        subject.data().observeWith { data -> actual = data }
        subject.getUserAccount(session)

        assertTrue(actual is UserAccountData.Success)
        assertEquals(expected, (actual as UserAccountData.Success).data)
        verify(exactly = 0) { accountApi.getUserAccountInfo(any()) }
    }

    @Test
    fun `Should update DB if data fetched from API`() {
        var actual: UserAccountData? = null
        val expected = mockk<UserAccount>()
        val session = mockk<Session>()

        every { accountDb.getUserAccountInfo() } returns null
        every { accountApi.getUserAccountInfo(session) } returns expected

        subject.data().observeWith { data -> actual = data }
        subject.getUserAccount(session)

        assertTrue(actual is UserAccountData.Success)
        assertEquals(expected, (actual as UserAccountData.Success).data)
        verify { accountApi.getUserAccountInfo(session) }
        verify { accountDb.storeUserAccountInfo(expected) }
    }

    @Test
    fun `Should report no data if API call fails`() {
        var actual: UserAccountData? = null
        val session = mockk<Session>()

        every { accountDb.getUserAccountInfo() } returns null
        every { accountApi.getUserAccountInfo(session) } returns null

        subject.data().observeWith { data -> actual = data }
        subject.getUserAccount(session)

        assertTrue(actual is UserAccountData.NoUserAccountData)
    }
}