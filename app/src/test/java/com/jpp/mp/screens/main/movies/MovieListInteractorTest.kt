package com.jpp.mp.screens.main.movies

import androidx.lifecycle.MutableLiveData
import com.jpp.mp.screens.main.movies.MovieListInteractor.MovieListEvent.*
import com.jpp.mpdomain.*
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.MoviePageRepository
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class MovieListInteractorTest {

    @MockK
    private lateinit var connectivityRepository: ConnectivityRepository
    @RelaxedMockK
    private lateinit var moviePageRepository: MoviePageRepository
    @MockK
    private lateinit var languageRepository: LanguageRepository

    private val languageRepositoryLiveData = MutableLiveData<SupportedLanguage>()

    private lateinit var subject: MovieListInteractor

    @BeforeEach
    fun setUp() {
        every { languageRepository.getCurrentAppLanguage() } returns SupportedLanguage.English
        every { languageRepository.updates() } returns languageRepositoryLiveData

        subject = MovieListInteractor(
                moviePageRepository,
                connectivityRepository,
                languageRepository
        )
    }

    @Test
    fun `Should notify when user changes language`() {
        var eventPosted: MovieListInteractor.MovieListEvent? = null

        subject.events.observeWith { eventPosted = it }

        languageRepositoryLiveData.postValue(SupportedLanguage.Spanish)

        assertEquals(UserChangedLanguage, eventPosted)
    }

    @ParameterizedTest
    @MethodSource("errorCasesParams")
    fun `Should post not connected event when not connected to network`(whenAction: (MovieListInteractor) -> Unit) {
        var eventPosted: MovieListInteractor.MovieListEvent? = null

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Disconnected

        subject.events.observeWith { eventPosted = it }

        whenAction.invoke(subject)

        assertEquals(NotConnectedToNetwork, eventPosted)
        verify(exactly = 0) { moviePageRepository.getMoviePageForSection(any(), any(), any()) }
        verify(exactly = 0) { languageRepository.getCurrentAppLanguage() }
    }

    @ParameterizedTest
    @MethodSource("errorCasesParams")
    fun `Should post unknown error event when connected to network and attempts to fetch movies`(whenAction: (MovieListInteractor) -> Unit) {
        var eventPosted: MovieListInteractor.MovieListEvent? = null

        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { moviePageRepository.getMoviePageForSection(any(), any(), any()) } returns null

        subject.events.observeWith { eventPosted = it }

        whenAction.invoke(subject)

        assertEquals(UnknownError, eventPosted)
        verify(exactly = 1) { moviePageRepository.getMoviePageForSection(any(), any(), any()) }
        verify(exactly = 1) { languageRepository.getCurrentAppLanguage() }
    }


    @ParameterizedTest
    @MethodSource("successCasesParams")
    fun `Should execute callback with movie list`(param: MovieListTestParam) {
        val movieList = listOf<Movie>(mockk(), mockk(), mockk())
        val moviePage = mockk<MoviePage>()

        every { moviePage.results } returns movieList
        every { connectivityRepository.getCurrentConnectivity() } returns Connectivity.Connected
        every { moviePageRepository.getMoviePageForSection(any(), any(), any()) } returns moviePage

        param.whenAction.invoke(subject)

        verify { param.mockCallback.invoke(movieList) }
        verify { moviePageRepository.getMoviePageForSection(param.page, param.movieSection, param.language) }
    }


    data class MovieListTestParam(
            val whenAction: (MovieListInteractor) -> Unit,
            val mockCallback: (List<Movie>) -> Unit,
            val movieSection: MovieSection,
            val page: Int = 1,
            val language: SupportedLanguage = SupportedLanguage.English
    )

    companion object {

        private val callbackMock = mockk<(List<Movie>) -> Unit>(relaxed = true)

        @JvmStatic
        fun errorCasesParams() = listOf<(MovieListInteractor) -> Unit>(
                { it.fetchPlayingMoviePage(1, mockk()) },
                { it.fetchPopularMoviePage(1, mockk()) },
                { it.fetchTopRatedMoviePage(1, mockk()) },
                { it.fetchUpcomingMoviePage(1, mockk()) }
        )

        @JvmStatic
        fun successCasesParams() = listOf(
                MovieListTestParam(
                        whenAction = { it.fetchPlayingMoviePage(1, callbackMock) },
                        mockCallback = callbackMock,
                        movieSection = MovieSection.Playing
                ),
                MovieListTestParam(
                        whenAction = { it.fetchPopularMoviePage(1, callbackMock) },
                        mockCallback = callbackMock,
                        movieSection = MovieSection.Popular
                ),
                MovieListTestParam(
                        whenAction = { it.fetchTopRatedMoviePage(1, callbackMock) },
                        mockCallback = callbackMock,
                        movieSection = MovieSection.TopRated
                ),
                MovieListTestParam(
                        whenAction = { it.fetchUpcomingMoviePage(1, callbackMock) },
                        mockCallback = callbackMock,
                        movieSection = MovieSection.Upcoming
                )
        )

    }

}