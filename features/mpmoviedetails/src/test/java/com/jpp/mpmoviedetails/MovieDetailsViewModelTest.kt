package com.jpp.mpmoviedetails

import androidx.lifecycle.MutableLiveData
import com.jpp.mpdomain.MovieDetail
import com.jpp.mpdomain.MovieGenre
import com.jpp.mpdomain.usecase.details.GetMovieDetailsUseCase
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class MovieDetailsViewModelTest {

    @RelaxedMockK
    private lateinit var interactor: MovieDetailsInteractor

    private val lvInteractorEvents = MutableLiveData<MovieDetailsInteractor.MovieDetailEvent>()

    private lateinit var subject: MovieDetailsViewModel

    @BeforeEach
    fun setUp() {
        every { interactor.movieDetailEvents } returns lvInteractorEvents

        subject = MovieDetailsViewModel(
                TestMovieDetailCoroutineDispatchers(),
                interactor)
    }

    @Test
    fun `Should post no connectivity error when disconnected`() {
        var viewStatePosted: MovieDetailViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvInteractorEvents.postValue(MovieDetailsInteractor.MovieDetailEvent.NotConnectedToNetwork)

        assertEquals(MovieDetailViewState.ShowNotConnected, viewStatePosted)
    }

    @Test
    fun `Should post error when failing to fetch user account data`() {
        var viewStatePosted: MovieDetailViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvInteractorEvents.postValue(MovieDetailsInteractor.MovieDetailEvent.UnknownError)

        assertEquals(MovieDetailViewState.ShowError, viewStatePosted)
    }

    @Test
    fun `Should post loading and fetch user account onInit`() {
        var viewStatePosted: MovieDetailViewState? = null
        val expected = MovieDetailViewState.ShowLoading("aMovie")

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }
        subject.onInit(10.0, "aMovie")

        verify { interactor.fetchMovieDetail(10.0) }
        assertEquals(expected, viewStatePosted)
    }

    @Test
    fun `Should execute GetMovieDetailsUseCase, adapt result to UI model and post value on init`() {
        var viewStatePosted: MovieDetailViewState? = null
        val movieDetailId = 12.0

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

        val expected = MovieDetailViewState.ShowDetail(
                title = domainDetail.title,
                overview = domainDetail.overview,
                releaseDate = domainDetail.release_date,
                voteCount = domainDetail.vote_count.toString(),
                voteAverage = domainDetail.vote_average.toString(),
                popularity = domainDetail.popularity.toString(),
                genres = listOf(
                        MovieGenreItem.Action,
                        MovieGenreItem.Horror
                )
        )

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        lvInteractorEvents.postValue(MovieDetailsInteractor.MovieDetailEvent.Success(domainDetail))

        assertEquals(expected, viewStatePosted)
    }
}