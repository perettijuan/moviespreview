package com.jpp.mpdomain.usecase.support

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.jpp.mp.utiltest.InstantTaskExecutorExtension
import com.jpp.mp.utiltest.resumedLifecycleOwner
import com.jpp.mpdomain.repository.AccountRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class RefreshAppDataUseCaseTest {

    @MockK
    private lateinit var accountRepository: AccountRepository

    private val accountUpdates by lazy { MutableLiveData<AccountRepository.AccountDataUpdate>() }

    private lateinit var subject: RefreshAppDataUseCase

    @BeforeEach
    fun setUp() {
        every { accountRepository.updates() } returns accountUpdates
        subject = RefreshAppDataUseCase.Impl(accountRepository)
    }

    @Test
    fun `Should update UserAccountMovies when account data is updated`() {
        var dataRefreshed: RefreshAppDataUseCase.AppDataRefresh? = null

        subject.appDataUpdates().observe(resumedLifecycleOwner(), Observer {
            dataRefreshed = it
        })

        accountUpdates.postValue(AccountRepository.AccountDataUpdate.FavoritesMovies)

        assertEquals(RefreshAppDataUseCase.AppDataRefresh.UserAccountMovies, dataRefreshed)
    }
}