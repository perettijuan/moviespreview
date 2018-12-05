package com.jpp.moviespreview.datalayer.repository

import com.jpp.moviespreview.datalayer.MoviePage
import com.jpp.moviespreview.datalayer.api.ServerRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class)
class MoviesRepositoryTest {

    data class ExecuteTestParameter(
            val caseName: String,
            val page: Int,
            val dbRepositoryCallTimes: Int,
            val serverRepositoryCallTimes: Int,
            val updateDbCallTimes: Int,
            val dbPage: MoviePage?,
            val serverPage: MoviePage?,
            val expectedResult: MoviePage?
    )

    companion object {

        private val resultPageMock = mockk<MoviePage>()

        @JvmStatic
        fun executeParameters() = listOf(
                ExecuteTestParameter(
                        caseName = "Data stored in DB",
                        page = 1,
                        dbRepositoryCallTimes = 1,
                        serverRepositoryCallTimes = 0,
                        updateDbCallTimes = 0,
                        dbPage = resultPageMock,
                        serverPage = null,
                        expectedResult = resultPageMock
                ),
                ExecuteTestParameter(
                        caseName = "Data retrieved from server",
                        page = 1,
                        dbRepositoryCallTimes = 1,
                        serverRepositoryCallTimes = 1,
                        updateDbCallTimes = 1,
                        dbPage = null,
                        serverPage = resultPageMock,
                        expectedResult = resultPageMock
                ),
                ExecuteTestParameter(
                        caseName = "Fails to retrieve from server",
                        page = 1,
                        dbRepositoryCallTimes = 1,
                        serverRepositoryCallTimes = 1,
                        updateDbCallTimes = 0,
                        dbPage = null,
                        serverPage = null,
                        expectedResult = null
                )
        )
    }


    @RelaxedMockK
    private lateinit var dbRepository: MoviesRepository
    @MockK
    private lateinit var serverRepository: ServerRepository

    private lateinit var subject: MoviesRepository

    @BeforeEach
    fun setUp() {
        subject = MoviesRepositoryImpl(dbRepository, serverRepository)
    }

    @ParameterizedTest
    @MethodSource("executeParameters")
    fun getNowPlayingMoviePage(testParam: ExecuteTestParameter) {
        every { dbRepository.getNowPlayingMoviePage(testParam.page) } returns testParam.dbPage
        every { serverRepository.getNowPlayingMoviePage(testParam.page) } returns testParam.serverPage

        val actual = subject.getNowPlayingMoviePage(testParam.page)

        verify(exactly = testParam.dbRepositoryCallTimes) { dbRepository.getNowPlayingMoviePage(testParam.page) }
        verify(exactly = testParam.serverRepositoryCallTimes) { serverRepository.getNowPlayingMoviePage(testParam.page) }
        testParam.serverPage?.let {
            verify(exactly = testParam.updateDbCallTimes) { dbRepository.updateNowPlayingMoviePage(it) }
        }
        assertEquals(testParam.expectedResult, actual, testParam.caseName)
    }

    @ParameterizedTest
    @MethodSource("executeParameters")
    fun getPopularMoviePage(testParam: ExecuteTestParameter) {
        every { dbRepository.getPopularMoviePage(testParam.page) } returns testParam.dbPage
        every { serverRepository.getPopularMoviePage(testParam.page) } returns testParam.serverPage

        val actual = subject.getPopularMoviePage(testParam.page)

        verify(exactly = testParam.dbRepositoryCallTimes) { dbRepository.getPopularMoviePage(testParam.page) }
        verify(exactly = testParam.serverRepositoryCallTimes) { serverRepository.getPopularMoviePage(testParam.page) }
        testParam.serverPage?.let {
            verify(exactly = testParam.updateDbCallTimes) { dbRepository.updatePopularMoviePage(it) }
        }
        assertEquals(testParam.expectedResult, actual, testParam.caseName)
    }

    @ParameterizedTest
    @MethodSource("executeParameters")
    fun getTopRatedMoviePage(testParam: ExecuteTestParameter) {
        every { dbRepository.getTopRatedMoviePage(testParam.page) } returns testParam.dbPage
        every { serverRepository.getTopRatedMoviePage(testParam.page) } returns testParam.serverPage

        val actual = subject.getTopRatedMoviePage(testParam.page)

        verify(exactly = testParam.dbRepositoryCallTimes) { dbRepository.getTopRatedMoviePage(testParam.page) }
        verify(exactly = testParam.serverRepositoryCallTimes) { serverRepository.getTopRatedMoviePage(testParam.page) }
        testParam.serverPage?.let {
            verify(exactly = testParam.updateDbCallTimes) { dbRepository.updateTopRatedMoviePage(it) }
        }
        assertEquals(testParam.expectedResult, actual, testParam.caseName)
    }

    @ParameterizedTest
    @MethodSource("executeParameters")
    fun getUpcomingMoviePage(testParam: ExecuteTestParameter) {
        every { dbRepository.getUpcomingMoviePage(testParam.page) } returns testParam.dbPage
        every { serverRepository.getUpcomingMoviePage(testParam.page) } returns testParam.serverPage

        val actual = subject.getUpcomingMoviePage(testParam.page)

        verify(exactly = testParam.dbRepositoryCallTimes) { dbRepository.getUpcomingMoviePage(testParam.page) }
        verify(exactly = testParam.serverRepositoryCallTimes) { serverRepository.getUpcomingMoviePage(testParam.page) }
        testParam.serverPage?.let {
            verify(exactly = testParam.updateDbCallTimes) { dbRepository.updateUpcomingMoviePage(it) }
        }
        assertEquals(testParam.expectedResult, actual, testParam.caseName)
    }
}