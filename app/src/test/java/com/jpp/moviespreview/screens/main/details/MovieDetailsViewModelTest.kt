package com.jpp.moviespreview.screens.main.details

import androidx.lifecycle.Observer
import com.jpp.moviespreview.screens.main.TestCoroutineDispatchers
import com.jpp.moviespreview.utiltest.InstantTaskExecutorExtension
import com.jpp.moviespreview.utiltest.resumedLifecycleOwner
import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.MovieGenre
import com.jpp.mpdomain.usecase.details.GetMovieDetailsUseCase
import com.jpp.mpdomain.usecase.details.GetMovieDetailsUseCaseResult
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class MovieDetailsViewModelTest {

    @MockK
    private lateinit var getMovieDetailsUseCase: GetMovieDetailsUseCase

    private lateinit var subject: MovieDetailsViewModel

    @BeforeEach
    fun setUp() {
        subject = MovieDetailsViewModel(TestCoroutineDispatchers(), getMovieDetailsUseCase)
    }

    @Test
    fun `Should execute GetMovieDetailsUseCase, adapt result to UI model and post value on init`() {
        val movieDetailId = 121.toDouble()
        val expectedTitle = "aTitle"
        val expectedOverview = "anOverview"
        val expectedReleaseDate = "aReleaseDat"
        val expectedVoteCount = 12.toDouble()
        val expectedVoteAverage = 15F
        val expectedPopularity = 178F
        val expectedMovieGenres = listOf(
                MovieGenreItem.Action,
                MovieGenreItem.Horror
        )

        val movieDetail = MovieDetail(
                id = movieDetailId,
                title = expectedTitle,
                overview = expectedOverview,
                release_date = expectedReleaseDate,
                vote_count = expectedVoteCount,
                vote_average = expectedVoteAverage,
                popularity = expectedPopularity,
                poster_path = null,
                genres = listOf(
                        MovieGenre(28, "Action"),
                        MovieGenre(27, "Horror")
                )
        )

        every { getMovieDetailsUseCase.getDetailsForMovie(movieDetailId) } returns GetMovieDetailsUseCaseResult.Success(movieDetail)

        executeInitTest(movieDetailId) {
            assertTrue(it is MovieDetailsViewState.ShowDetail)

            with((it as MovieDetailsViewState.ShowDetail).detail) {
                assertEquals(expectedTitle, title)
                assertEquals(expectedOverview, overview)
                assertEquals(expectedReleaseDate, releaseDate)
                assertEquals(expectedVoteCount, voteCount)
                assertEquals(expectedVoteAverage, voteAverage)
                assertEquals(expectedPopularity, popularity)
                assertEquals(expectedMovieGenres, genres)
            }
        }
        verify(exactly = 1) { getMovieDetailsUseCase.getDetailsForMovie(movieDetailId) }
    }

    @Test
    fun `Should execute GetMovieDetailsUseCase and show connectivity error`() {
        val movieDetailId = 121.toDouble()

        every { getMovieDetailsUseCase.getDetailsForMovie(movieDetailId) } returns GetMovieDetailsUseCaseResult.ErrorNoConnectivity

        executeInitTest(movieDetailId) {
            assertTrue(it is MovieDetailsViewState.ErrorNoConnectivity)
        }
    }

    @Test
    fun `Should fetch movie detail from repository and show unknown error`() {
        val movieDetailId = 121.toDouble()

        every { getMovieDetailsUseCase.getDetailsForMovie(movieDetailId) } returns GetMovieDetailsUseCaseResult.ErrorUnknown

        executeInitTest(movieDetailId) {
            assertTrue(it is MovieDetailsViewState.ErrorUnknown)
        }
    }

    @Test
    fun `Should not re-fetch movie detail when init is called twice with the same id`() {
        val movieDetailId = 121.toDouble()

        every { getMovieDetailsUseCase.getDetailsForMovie(movieDetailId) } returns GetMovieDetailsUseCaseResult.Success(mockk(relaxed = true))

        // first call
        subject.init(movieDetailId)

        // second call
        subject.init(movieDetailId)

        verify(exactly = 1) { getMovieDetailsUseCase.getDetailsForMovie(movieDetailId) }
    }


    private fun executeInitTest(movieId: Double, verification: (MovieDetailsViewState) -> Unit) {
        subject.init(movieId)

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            verification.invoke(it)
        })
    }

}