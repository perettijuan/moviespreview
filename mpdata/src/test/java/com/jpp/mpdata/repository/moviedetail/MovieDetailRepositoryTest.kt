package com.jpp.mpdata.repository.moviedetail

import com.jpp.mpdata.datasources.moviedetail.MovieDetailApi
import com.jpp.mpdata.datasources.moviedetail.MovieDetailDb
import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.SupportedLanguage
import com.jpp.mpdomain.repository.MovieDetailRepository
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class MovieDetailRepositoryTest {

    @RelaxedMockK
    private lateinit var moviesDetailApi: MovieDetailApi
    @RelaxedMockK
    private lateinit var moviesDetailDb: MovieDetailDb

    private lateinit var subject: MovieDetailRepository

    @BeforeEach
    fun setUp() {
        subject = MovieDetailRepositoryImpl(moviesDetailApi, moviesDetailDb)
    }

    @Test
    fun `Should not get data from API when details is in DB`() = runBlocking {
        val movieDetail = mockk<MovieDetail>()
        every { moviesDetailDb.getMovieDetails(any()) } returns movieDetail

        val result = subject.getMovieDetails(10.toDouble(), SupportedLanguage.English)

        assertEquals(movieDetail, result)
        verify { moviesDetailDb.getMovieDetails(10.toDouble()) }
        verify(exactly = 0) { moviesDetailApi.getMovieDetails(any(), SupportedLanguage.English) }
    }

    @Test
    fun `Should get data from API and update the DB when details is not in DB`() = runBlocking {
        val movieDetail = mockk<MovieDetail>()
        every { moviesDetailDb.getMovieDetails(any()) } returns null
        every { moviesDetailApi.getMovieDetails(any(), any()) } returns movieDetail

        val result = subject.getMovieDetails(10.toDouble(), SupportedLanguage.English)

        assertEquals(result, result)
        verify { moviesDetailDb.getMovieDetails(10.toDouble()) }
        verify { moviesDetailApi.getMovieDetails(10.toDouble(), SupportedLanguage.English) }
        verify { moviesDetailDb.saveMovieDetails(movieDetail) }
    }

    @Test
    fun `Should not attempt to store null responses from API when fetching movie details`() = runBlocking {
        every { moviesDetailDb.getMovieDetails(any()) } returns null
        every { moviesDetailApi.getMovieDetails(any(), any()) } returns null

        val result = subject.getMovieDetails(10.toDouble(), SupportedLanguage.English)

        assertNull(result)
        verify(exactly = 0) { moviesDetailDb.saveMovieDetails(any()) }
    }
}
