package com.jpp.moviespreview.datalayer.cache.repository

import com.jpp.moviespreview.datalayer.MoviePage
import com.jpp.moviespreview.datalayer.cache.MovieType
import com.jpp.moviespreview.datalayer.cache.MoviesPreviewDataBase
import com.jpp.moviespreview.datalayer.cache.timestamp.MPTimestamps
import io.mockk.every
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

@ExtendWith(MockKExtension::class)
class DBMoviesRepositoryTest {

    data class GetMoviePageParameter(
            val caseName: String,
            val page: Int,
            val isCurrentMovieTypeStored: Boolean,
            val areMoviesUpToDate: Boolean,
            val dbClearTimes: Int,
            val getMoviePageTimes: Int,
            val expectedResult: MoviePage?
    )

    companion object {

        private val resultPageMock = mockk<MoviePage>()

        @JvmStatic
        fun getMoviesParameters() = listOf(
                GetMoviePageParameter(
                        caseName = "MovieType stored in DB and valid cache",
                        page = 1,
                        isCurrentMovieTypeStored = true,
                        areMoviesUpToDate = true,
                        dbClearTimes = 0,
                        getMoviePageTimes = 1,
                        expectedResult = resultPageMock
                ),
                GetMoviePageParameter(
                        caseName = "MovieType stored in DB and invalid cache",
                        page = 1,
                        isCurrentMovieTypeStored = true,
                        areMoviesUpToDate = false,
                        dbClearTimes = 1,
                        getMoviePageTimes = 0,
                        expectedResult = null
                ),
                GetMoviePageParameter(
                        caseName = "MovieType not stored in DB and invalid cache",
                        page = 1,
                        isCurrentMovieTypeStored = false,
                        areMoviesUpToDate = false,
                        dbClearTimes = 1,
                        getMoviePageTimes = 0,
                        expectedResult = null
                )
        )
    }

    @RelaxedMockK
    private lateinit var mpCache: MPTimestamps
    @RelaxedMockK
    private lateinit var mpDatabase: MoviesPreviewDataBase

    private lateinit var subject: DBMoviesRepository

    @BeforeEach
    fun setUp() {
        subject = DBMoviesRepository(mpCache, mpDatabase)
    }

    @ParameterizedTest
    @MethodSource("getMoviesParameters")
    fun getNowPlayingMoviePage(param: GetMoviePageParameter) {
        every { mpDatabase.isCurrentMovieTypeStored(MovieType.NowPlaying) } returns param.isCurrentMovieTypeStored
        every { mpCache.areMoviesUpToDate() } returns param.areMoviesUpToDate
        every { mpDatabase.getMoviePage(param.page) } returns param.expectedResult

        val actual = subject.getNowPlayingMoviePage(param.page)

        verify(exactly = param.dbClearTimes) { mpDatabase.clearMoviePagesStored() }
        verify(exactly = param.getMoviePageTimes) { mpDatabase.getMoviePage(param.page) }
        assertEquals(actual, param.expectedResult, param.caseName)
    }

    @ParameterizedTest
    @MethodSource("getMoviesParameters")
    fun getNowPopularMoviePage(param: GetMoviePageParameter) {
        every { mpDatabase.isCurrentMovieTypeStored(MovieType.Popular) } returns param.isCurrentMovieTypeStored
        every { mpCache.areMoviesUpToDate() } returns param.areMoviesUpToDate
        every { mpDatabase.getMoviePage(param.page) } returns param.expectedResult

        val actual = subject.getPopularMoviePage(param.page)

        verify(exactly = param.dbClearTimes) { mpDatabase.clearMoviePagesStored() }
        verify(exactly = param.getMoviePageTimes) { mpDatabase.getMoviePage(param.page) }
        assertEquals(actual, param.expectedResult, param.caseName)
    }

    @ParameterizedTest
    @MethodSource("getMoviesParameters")
    fun getTopRatedMoviePage(param: GetMoviePageParameter) {
        every { mpDatabase.isCurrentMovieTypeStored(MovieType.TopRated) } returns param.isCurrentMovieTypeStored
        every { mpCache.areMoviesUpToDate() } returns param.areMoviesUpToDate
        every { mpDatabase.getMoviePage(param.page) } returns param.expectedResult

        val actual = subject.getTopRatedMoviePage(param.page)

        verify(exactly = param.dbClearTimes) { mpDatabase.clearMoviePagesStored() }
        verify(exactly = param.getMoviePageTimes) { mpDatabase.getMoviePage(param.page) }
        assertEquals(actual, param.expectedResult, param.caseName)
    }

    @ParameterizedTest
    @MethodSource("getMoviesParameters")
    fun getUpcomingMoviePage(param: GetMoviePageParameter) {
        every { mpDatabase.isCurrentMovieTypeStored(MovieType.Upcoming) } returns param.isCurrentMovieTypeStored
        every { mpCache.areMoviesUpToDate() } returns param.areMoviesUpToDate
        every { mpDatabase.getMoviePage(param.page) } returns param.expectedResult

        val actual = subject.getUpcomingMoviePage(param.page)

        verify(exactly = param.dbClearTimes) { mpDatabase.clearMoviePagesStored() }
        verify(exactly = param.getMoviePageTimes) { mpDatabase.getMoviePage(param.page) }
        assertEquals(actual, param.expectedResult, param.caseName)
    }

    @Test
    fun updateNowPlayingMoviePage() {
        subject.updateNowPlayingMoviePage(resultPageMock)

        verify(exactly = 1) { mpDatabase.updateCurrentMovieTypeStored(MovieType.NowPlaying) }
        verify(exactly = 1) { mpDatabase.updateMoviePage(resultPageMock) }
        verify(exactly = 1) { mpCache.updateMoviesInserted() }
    }

    @Test
    fun updatePopularMoviePage() {
        subject.updatePopularMoviePage(resultPageMock)

        verify(exactly = 1) { mpDatabase.updateCurrentMovieTypeStored(MovieType.Popular) }
        verify(exactly = 1) { mpDatabase.updateMoviePage(resultPageMock) }
        verify(exactly = 1) { mpCache.updateMoviesInserted() }
    }

    @Test
    fun updateTopRatedMoviePage() {
        subject.updateTopRatedMoviePage(resultPageMock)

        verify(exactly = 1) { mpDatabase.updateCurrentMovieTypeStored(MovieType.TopRated) }
        verify(exactly = 1) { mpDatabase.updateMoviePage(resultPageMock) }
        verify(exactly = 1) { mpCache.updateMoviesInserted() }
    }

    @Test
    fun updateUpcomingMoviePage() {
        subject.updateUpcomingMoviePage(resultPageMock)

        verify(exactly = 1) { mpDatabase.updateCurrentMovieTypeStored(MovieType.Upcoming) }
        verify(exactly = 1) { mpDatabase.updateMoviePage(resultPageMock) }
        verify(exactly = 1) { mpCache.updateMoviesInserted() }
    }
}