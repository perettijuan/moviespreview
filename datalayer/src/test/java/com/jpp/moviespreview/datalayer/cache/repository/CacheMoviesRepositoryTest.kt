package com.jpp.moviespreview.datalayer.cache.repository

import com.jpp.moviespreview.datalayer.DataModelMapper
import com.jpp.moviespreview.datalayer.cache.MPDataBase
import com.jpp.moviespreview.datalayer.cache.MovieType
import com.jpp.moviespreview.datalayer.cache.timestamp.MPTimestamps
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
import com.jpp.moviespreview.datalayer.MoviePage as DataMoviePage
import com.jpp.moviespreview.domainlayer.MoviePage as DomainMoviePage

@ExtendWith(MockKExtension::class)
class CacheMoviesRepositoryTest {

    data class GetMoviePageParameter(
            val caseName: String,
            val page: Int,
            val isCurrentMovieTypeStored: Boolean,
            val areMoviesUpToDate: Boolean,
            val dbClearTimes: Int,
            val getMoviePageTimes: Int,
            val dataMoviePage: DataMoviePage?,
            val expectedResult: DomainMoviePage?
    )

    companion object {

        private val dataPageMock = mockk<DataMoviePage>()
        private val domainPageMock = mockk<DomainMoviePage>()

        @JvmStatic
        fun getMoviesParameters() = listOf(
                GetMoviePageParameter(
                        caseName = "MovieType stored in DB and valid cache",
                        page = 1,
                        isCurrentMovieTypeStored = true,
                        areMoviesUpToDate = true,
                        dbClearTimes = 0,
                        getMoviePageTimes = 1,
                        dataMoviePage = dataPageMock,
                        expectedResult = domainPageMock
                ),
                GetMoviePageParameter(
                        caseName = "MovieType stored in DB and invalid cache",
                        page = 1,
                        isCurrentMovieTypeStored = true,
                        areMoviesUpToDate = false,
                        dbClearTimes = 1,
                        getMoviePageTimes = 0,
                        dataMoviePage = null,
                        expectedResult = null
                ),
                GetMoviePageParameter(
                        caseName = "MovieType not stored in DB and invalid cache",
                        page = 1,
                        isCurrentMovieTypeStored = false,
                        areMoviesUpToDate = false,
                        dbClearTimes = 1,
                        getMoviePageTimes = 0,
                        dataMoviePage = null,
                        expectedResult = null
                )
        )
    }

    @RelaxedMockK
    private lateinit var mpCache: MPTimestamps
    @RelaxedMockK
    private lateinit var mpDatabase: MPDataBase
    @MockK
    private lateinit var mapper: DataModelMapper

    private lateinit var subject: CacheMoviesRepository

    @BeforeEach
    fun setUp() {
        subject = CacheMoviesRepository(mpCache, mpDatabase, mapper)
    }

    @ParameterizedTest
    @MethodSource("getMoviesParameters")
    fun getNowPlayingMoviePage(param: GetMoviePageParameter) {
        every { mpDatabase.isCurrentMovieTypeStored(MovieType.NowPlaying) } returns param.isCurrentMovieTypeStored
        every { mpCache.areMoviesUpToDate() } returns param.areMoviesUpToDate
        every { mpDatabase.getMoviePage(param.page) } returns param.dataMoviePage
        if (param.dataMoviePage != null && param.expectedResult != null) {
            every { mapper.mapDataMoviePage(param.dataMoviePage) } returns param.expectedResult
        }


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
        every { mpDatabase.getMoviePage(param.page) } returns param.dataMoviePage
        if (param.dataMoviePage != null && param.expectedResult != null) {
            every { mapper.mapDataMoviePage(param.dataMoviePage) } returns param.expectedResult
        }

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
        every { mpDatabase.getMoviePage(param.page) } returns param.dataMoviePage
        if (param.dataMoviePage != null && param.expectedResult != null) {
            every { mapper.mapDataMoviePage(param.dataMoviePage) } returns param.expectedResult
        }

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
        every { mpDatabase.getMoviePage(param.page) } returns param.dataMoviePage
        if (param.dataMoviePage != null && param.expectedResult != null) {
            every { mapper.mapDataMoviePage(param.dataMoviePage) } returns param.expectedResult
        }

        val actual = subject.getUpcomingMoviePage(param.page)

        verify(exactly = param.dbClearTimes) { mpDatabase.clearMoviePagesStored() }
        verify(exactly = param.getMoviePageTimes) { mpDatabase.getMoviePage(param.page) }
        assertEquals(actual, param.expectedResult, param.caseName)
    }

    @Test
    fun updateNowPlayingMoviePage() {
        every { mapper.mapDomainMoviePage(domainPageMock) } returns dataPageMock

        subject.updateNowPlayingMoviePage(domainPageMock)

        verify(exactly = 1) { mpDatabase.updateCurrentMovieTypeStored(MovieType.NowPlaying) }
        verify(exactly = 1) { mpDatabase.updateMoviePage(dataPageMock) }
        verify(exactly = 1) { mpCache.updateMoviesInserted() }
    }

    @Test
    fun updatePopularMoviePage() {
        every { mapper.mapDomainMoviePage(domainPageMock) } returns dataPageMock

        subject.updatePopularMoviePage(domainPageMock)

        verify(exactly = 1) { mpDatabase.updateCurrentMovieTypeStored(MovieType.Popular) }
        verify(exactly = 1) { mpDatabase.updateMoviePage(dataPageMock) }
        verify(exactly = 1) { mpCache.updateMoviesInserted() }
    }

    @Test
    fun updateTopRatedMoviePage() {
        every { mapper.mapDomainMoviePage(domainPageMock) } returns dataPageMock

        subject.updateTopRatedMoviePage(domainPageMock)

        verify(exactly = 1) { mpDatabase.updateCurrentMovieTypeStored(MovieType.TopRated) }
        verify(exactly = 1) { mpDatabase.updateMoviePage(dataPageMock) }
        verify(exactly = 1) { mpCache.updateMoviesInserted() }
    }

    @Test
    fun updateUpcomingMoviePage() {
        every { mapper.mapDomainMoviePage(domainPageMock) } returns dataPageMock

        subject.updateUpcomingMoviePage(domainPageMock)

        verify(exactly = 1) { mpDatabase.updateCurrentMovieTypeStored(MovieType.Upcoming) }
        verify(exactly = 1) { mpDatabase.updateMoviePage(dataPageMock) }
        verify(exactly = 1) { mpCache.updateMoviesInserted() }
    }
}