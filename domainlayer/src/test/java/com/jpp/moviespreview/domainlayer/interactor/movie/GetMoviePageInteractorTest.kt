package com.jpp.moviespreview.domainlayer.interactor.movie


import com.jpp.moviespreview.domainlayer.ConnectivityVerifier
import com.jpp.moviespreview.domainlayer.MovieSection
import com.jpp.moviespreview.domainlayer.interactor.GetMoviePageInteractor
import com.jpp.moviespreview.domainlayer.interactor.MoviePageParam
import com.jpp.moviespreview.domainlayer.interactor.MoviePageResult
import com.jpp.moviespreview.domainlayer.repository.MoviesRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import com.jpp.moviespreview.domainlayer.MoviePage as DomainMoviePage

@ExtendWith(MockKExtension::class)
class GetMoviePageInteractorTest {

    data class ExecuteTestParameter(
            val moviePage: Int,
            val resultDomainPage: MoviesRepository.MoviesRepositoryOutput,
            val connectedToNetwork: Boolean = true,
            val expectedResult: MoviePageResult
    )

    companion object {

        private val resultDomainPageMock = mockk<DomainMoviePage>()

        @JvmStatic
        fun executeParameters() = listOf(
                ExecuteTestParameter(
                        moviePage = 1,
                        resultDomainPage =  MoviesRepository.MoviesRepositoryOutput.Success(resultDomainPageMock),
                        expectedResult = MoviePageResult.Success(resultDomainPageMock)
                ),
                ExecuteTestParameter(
                        moviePage = 1,
                        resultDomainPage =  MoviesRepository.MoviesRepositoryOutput.Error,
                        connectedToNetwork = true,
                        expectedResult = MoviePageResult.ErrorUnknown
                ),
                ExecuteTestParameter(
                        moviePage = 1,
                        resultDomainPage =  MoviesRepository.MoviesRepositoryOutput.Error,
                        connectedToNetwork = false,
                        expectedResult = MoviePageResult.ErrorNoConnectivity
                )
        )
    }

    @MockK
    private lateinit var moviesRepository: MoviesRepository
    @MockK
    private lateinit var connectivityVerifier: ConnectivityVerifier

    private lateinit var subject: GetMoviePageInteractor

    @BeforeEach
    fun setUp() {
        subject = GetMoviePageInteractorImpl(moviesRepository, connectivityVerifier)
    }

    @ParameterizedTest
    @MethodSource("executeParameters")
    fun `execute to get playing movies`(testParam: ExecuteTestParameter) {
        val param = MoviePageParam(testParam.moviePage, MovieSection.Playing)
        every { moviesRepository.getNowPlayingMoviePage(param.page) } returns testParam.resultDomainPage
        every { connectivityVerifier.isConnectedToNetwork() } returns testParam.connectedToNetwork


        val actual = subject.invoke(param)

        assertEquals(testParam.expectedResult, actual)
        verify(exactly = 1) { moviesRepository.getNowPlayingMoviePage(param.page) }
    }

    @ParameterizedTest
    @MethodSource("executeParameters")
    fun `execute to get popular movies`(testParam: ExecuteTestParameter) {
        val param = MoviePageParam(testParam.moviePage, MovieSection.Popular)
        every { moviesRepository.getPopularMoviePage(param.page) } returns testParam.resultDomainPage
        every { connectivityVerifier.isConnectedToNetwork() } returns testParam.connectedToNetwork

        val actual = subject.invoke(param)

        assertEquals(testParam.expectedResult, actual)
        verify(exactly = 1) { moviesRepository.getPopularMoviePage(param.page) }
    }

    @ParameterizedTest
    @MethodSource("executeParameters")
    fun `execute to get top rated movies`(testParam: ExecuteTestParameter) {
        val param = MoviePageParam(testParam.moviePage, MovieSection.TopRated)
        every { moviesRepository.getTopRatedMoviePage(param.page) } returns testParam.resultDomainPage
        every { connectivityVerifier.isConnectedToNetwork() } returns testParam.connectedToNetwork

        val actual = subject.invoke(param)

        assertEquals(testParam.expectedResult, actual)
        verify(exactly = 1) { moviesRepository.getTopRatedMoviePage(param.page) }
    }

    @ParameterizedTest
    @MethodSource("executeParameters")
    fun `execute to get upcoming movies`(testParam: ExecuteTestParameter) {
        val param = MoviePageParam(testParam.moviePage, MovieSection.Upcoming)
        every { moviesRepository.getUpcomingMoviePage(param.page) } returns testParam.resultDomainPage
        every { connectivityVerifier.isConnectedToNetwork() } returns testParam.connectedToNetwork

        val actual = subject.invoke(param)

        assertEquals(testParam.expectedResult, actual)
        verify(exactly = 1) { moviesRepository.getUpcomingMoviePage(param.page) }
    }
}