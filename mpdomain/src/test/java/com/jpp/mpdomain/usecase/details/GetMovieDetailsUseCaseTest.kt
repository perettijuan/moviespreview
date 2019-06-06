package com.jpp.mpdomain.usecase.details

import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.MoviesRepository
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
import com.jpp.mpdomain.usecase.details.GetMovieDetailsUseCase.GetMovieDetailsResult.*

@ExtendWith(MockKExtension::class)
class GetMovieDetailsUseCaseTest {

    @RelaxedMockK
    private lateinit var moviesRepository: MoviesRepository
    @RelaxedMockK
    private lateinit var connectivityRepository: ConnectivityRepository
    @RelaxedMockK
    private lateinit var languageRepository: LanguageRepository

    private val language = SupportedLanguage.English

    private lateinit var subject: GetMovieDetailsUseCase

    @BeforeEach
    fun setUp() {
        subject = GetMovieDetailsUseCase.Impl(moviesRepository, connectivityRepository, languageRepository)
        every { languageRepository.getCurrentAppLanguage() } returns language
    }


}