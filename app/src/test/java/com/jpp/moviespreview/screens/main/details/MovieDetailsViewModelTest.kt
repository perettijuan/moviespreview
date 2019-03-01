package com.jpp.moviespreview.screens.main.details

import androidx.lifecycle.Observer
import com.jpp.moviespreview.InstantTaskExecutorExtension
import com.jpp.moviespreview.resumedLifecycleOwner
import com.jpp.moviespreview.screens.main.TestCoroutineDispatchers
import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.MovieGenre
import com.jpp.mpdomain.usecase.details.GetMovieDetailsUseCase
import com.jpp.mpdomain.usecase.details.GetMovieDetailsResult
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
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
        val viewStatePosted = mutableListOf<MovieDetailsViewState>()

        val movieDetailId = 121.toDouble()

        val domainDetail = MovieDetail(
                id = movieDetailId,
                title = "aTitle",
                overview = "anOverview",
                release_date = "aReleaseDate",
                vote_count = 12.toDouble(),
                vote_average = 15F,
                popularity = 178F,
                poster_path = null,
                genres = listOf(
                        MovieGenre(28, "Action"),
                        MovieGenre(27, "Horror")
                )
        )

        val expectedUiMovieDetails = UiMovieDetails(
                title = domainDetail.title,
                overview = domainDetail.overview,
                releaseDate = domainDetail.release_date,
                voteCount = domainDetail.vote_count,
                voteAverage = domainDetail.vote_average,
                popularity = domainDetail.popularity,
                genres = listOf(
                        MovieGenreItem.Action,
                        MovieGenreItem.Horror
                )
        )

        every { getMovieDetailsUseCase.getDetailsForMovie(any()) } returns GetMovieDetailsResult.Success(domainDetail)

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            viewStatePosted.add(it)
        })

        subject.init(movieDetailId)

        assertTrue(viewStatePosted[0] is MovieDetailsViewState.Loading)
        assertTrue(viewStatePosted[1] is MovieDetailsViewState.ShowDetail)
        assertEquals(expectedUiMovieDetails, (viewStatePosted[1] as MovieDetailsViewState.ShowDetail).detail)
        verify(exactly = 1) { getMovieDetailsUseCase.getDetailsForMovie(movieDetailId) }
    }

    @Test
    fun `Should execute GetMovieDetailsUseCase and show connectivity error`() {
        val viewStatePosted = mutableListOf<MovieDetailsViewState>()
        val movieDetailId = 121.toDouble()

        every { getMovieDetailsUseCase.getDetailsForMovie(movieDetailId) } returns GetMovieDetailsResult.ErrorNoConnectivity

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            viewStatePosted.add(it)
        })

        subject.init(movieDetailId)

        assertTrue(viewStatePosted[0] is MovieDetailsViewState.Loading)
        assertTrue(viewStatePosted[1] is MovieDetailsViewState.ErrorNoConnectivity)
    }

    @Test
    fun `Should fetch movie detail from repository and show unknown error`() {
        val viewStatePosted = mutableListOf<MovieDetailsViewState>()
        val movieDetailId = 121.toDouble()

        every { getMovieDetailsUseCase.getDetailsForMovie(movieDetailId) } returns GetMovieDetailsResult.ErrorUnknown

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            viewStatePosted.add(it)
        })

        subject.init(movieDetailId)

        assertTrue(viewStatePosted[0] is MovieDetailsViewState.Loading)
        assertTrue(viewStatePosted[1] is MovieDetailsViewState.ErrorUnknown)
    }
}