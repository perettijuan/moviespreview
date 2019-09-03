package com.jpp.mpmoviedetails

import androidx.lifecycle.MutableLiveData
import com.jpp.mpdomain.MovieState
import com.jpp.mpmoviedetails.MovieDetailsInteractor.MovieStateEvent
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class MovieDetailsActionViewModelTest {

    @RelaxedMockK
    private lateinit var movieDetailsInteractor: MovieDetailsInteractor

    private val lvInteractorEvents = MutableLiveData<MovieStateEvent>()

    private lateinit var subject: MovieDetailsActionViewModel


    @BeforeEach
    fun setUp() {
        every { movieDetailsInteractor.movieStateEvents } returns lvInteractorEvents

        subject = MovieDetailsActionViewModel(
                TestMovieDetailCoroutineDispatchers(),
                movieDetailsInteractor
        )
    }

    @ParameterizedTest
    @MethodSource("movieActionEvents")
    fun `Map interactor events to view state`(expected: MovieDetailActionViewState, pushed: MovieStateEvent) {
        var viewStatePosted: MovieDetailActionViewState? = null

        subject.viewStates.observeWith { viewState -> viewStatePosted = viewState }

        lvInteractorEvents.postValue(pushed)

        assertEquals(expected, viewStatePosted)
    }

    @Test
    fun `Should push clean ShowUserNotLogged when interactor pushes UserNotLogged and no previous view state`() {
        var viewStatePosted: MovieDetailActionViewState? = null
        val expected = MovieDetailActionViewState.ShowUserNotLogged(false, false)

        subject.viewStates.observeWith { viewState -> viewStatePosted = viewState }

        lvInteractorEvents.postValue(MovieStateEvent.UserNotLogged)

        assertEquals(expected, viewStatePosted)
    }

    companion object {

        @JvmStatic
        fun movieActionEvents() = listOf(
                arguments(
                        MovieDetailActionViewState.ShowNoMovieState(false, false),
                        MovieStateEvent.NoStateFound
                ),
                arguments(
                        MovieDetailActionViewState.ShowError,
                        MovieStateEvent.NotConnectedToNetwork
                ),
                arguments(
                        MovieDetailActionViewState.ShowError,
                        MovieStateEvent.UnknownError
                ),
                arguments(
                        MovieDetailActionViewState.ShowNoMovieState(false, false),
                        MovieStateEvent.NoStateFound
                ),
                arguments(
                        MovieDetailActionViewState.ShowMovieState(
                                showActionsExpanded = false,
                                animateActionsExpanded = false,
                                favorite = ActionButtonState.ShowAsEmpty,
                                isInWatchlist = ActionButtonState.ShowAsEmpty,
                                isRated = false
                        ),
                        MovieStateEvent.FetchSuccess(MOVIE_STATE_NO_FAVORITE)
                ),
                arguments(
                        MovieDetailActionViewState.ShowMovieState(
                                showActionsExpanded = false,
                                animateActionsExpanded = false,
                                favorite = ActionButtonState.ShowAsFilled,
                                isInWatchlist = ActionButtonState.ShowAsEmpty,
                                isRated = false
                        ),
                        MovieStateEvent.FetchSuccess(MOVIE_STATE_FAVORITE)
                ),
                arguments(
                        MovieDetailActionViewState.ShowMovieState(
                                showActionsExpanded = false,
                                animateActionsExpanded = false,
                                favorite = ActionButtonState.ShowAsFilled,
                                isInWatchlist = ActionButtonState.ShowAsFilled,
                                isRated = false
                        ),
                        MovieStateEvent.FetchSuccess(MOVIE_STATE_FAVORITE_AND_WATCHLIST)
                )
        )

        private val MOVIE_STATE_NO_FAVORITE = MovieState(
                id = 12.0,
                favorite = false,
                watchlist = false,
                rated = false
        )

        private val MOVIE_STATE_FAVORITE = MovieState(
                id = 12.0,
                favorite = true,
                watchlist = false,
                rated = false
        )

        private val MOVIE_STATE_FAVORITE_AND_WATCHLIST = MovieState(
                id = 12.0,
                favorite = true,
                watchlist = true,
                rated = false
        )
    }
}