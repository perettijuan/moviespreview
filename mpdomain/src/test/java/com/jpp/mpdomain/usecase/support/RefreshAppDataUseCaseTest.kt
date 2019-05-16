package com.jpp.mpdomain.usecase.support

import androidx.lifecycle.MutableLiveData
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class RefreshAppDataUseCaseTest {

    @RelaxedMockK
    private lateinit var accountRepository: AccountRepository
    @MockK
    private lateinit var languageRepository: LanguageRepository

    private val accountUpdates by lazy { MutableLiveData<AccountRepository.AccountDataUpdate>() }
    private val languageUpdates by lazy { MutableLiveData<LanguageRepository.LanguageEvent>() }

    private lateinit var subject: RefreshAppDataUseCase

    @BeforeEach
    fun setUp() {
        every { accountRepository.updates() } returns accountUpdates
        every { languageRepository.updates() } returns languageUpdates
        subject = RefreshAppDataUseCase.Impl(accountRepository, languageRepository)
    }

    @Test
    fun `Should update UserAccountMovies when account data is updated`() {
        var dataRefreshed: RefreshAppDataUseCase.AppDataRefresh? = null

        subject.appDataUpdates().observeWith { dataRefreshed = it }

        accountUpdates.postValue(AccountRepository.AccountDataUpdate.FavoritesMovies)

        assertEquals(RefreshAppDataUseCase.AppDataRefresh.UserAccountMovies, dataRefreshed)
    }


    @Test
    fun `Should update LanguageChanged and update account repository when language changes`() {
        var dataRefreshed: RefreshAppDataUseCase.AppDataRefresh? = null

        subject.appDataUpdates().observeWith { dataRefreshed = it }

        languageUpdates.postValue(LanguageRepository.LanguageEvent.LanguageChangeEvent)

        verify { accountRepository.refresh() }
        assertEquals(RefreshAppDataUseCase.AppDataRefresh.LanguageChanged, dataRefreshed)
    }
}