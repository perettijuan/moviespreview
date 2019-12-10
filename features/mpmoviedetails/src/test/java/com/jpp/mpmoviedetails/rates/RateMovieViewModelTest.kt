package com.jpp.mpmoviedetails.rates

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.common.navigation.Destination
import com.jpp.mpdomain.MovieState
import com.jpp.mpdomain.MovieStateRate
import com.jpp.mpmoviedetails.MovieDetailsInteractor
import com.jpp.mpmoviedetails.MovieDetailsInteractor.MovieStateEvent
import com.jpp.mpmoviedetails.TestMovieDetailCoroutineDispatchers
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(
        MockKExtension::class,
        InstantTaskExecutorExtension::class
)
class RateMovieViewModelTest {

    @RelaxedMockK
    private lateinit var movieDetailsInteractor: MovieDetailsInteractor

    private val lvInteractorEvents = MutableLiveData<MovieStateEvent>()
    private val lvRateMovieEvents = MutableLiveData<MovieDetailsInteractor.RateMovieEvent>()

    private lateinit var subject: RateMovieViewModel

    @BeforeEach
    fun setUp() {
        every { movieDetailsInteractor.movieStateEvents } returns lvInteractorEvents
        every { movieDetailsInteractor.rateMovieEvents } returns lvRateMovieEvents

        subject = RateMovieViewModel(
                TestMovieDetailCoroutineDispatchers(),
                movieDetailsInteractor
        )
    }

    @ParameterizedTest
    @MethodSource("rateMovieEvents")
    fun `Map interactor events to user messages and exit`(expected: RateMovieUserMessages, pushed: MovieDetailsInteractor.RateMovieEvent) {
        var userMessagePosted: RateMovieUserMessages? = null
        var postedDestination: Destination? = null

        subject.userMessages.observeWith { it.actionIfNotHandled { userMessage -> userMessagePosted = userMessage } }
        subject.navigationEvents.observeWith { it.actionIfNotHandled { destination -> postedDestination = destination } }

        lvRateMovieEvents.postValue(pushed)

        assertEquals(expected, userMessagePosted)
        assertEquals(Destination.PreviousDestination, postedDestination)
    }

    @Test
    fun `Should map interactor FetchSuccess event to ViewState`() {
        var postedViewState: RateMovieViewState? = null

        val param = RateMovieParam(
                movieId = 12.0,
                screenTitle = "aMovie",
                movieImageUrl = "aUrl"
        )

        val rating = MovieStateRate(
                isRated = true,
                value = "5.5"
        )
        val movieState = MovieState(
                id = 12.0,
                favorite = false,
                rated = rating,
                watchlist = false
        )

        subject.viewState.observeWith { viewState -> postedViewState = viewState }

        subject.onInit(param)

        lvRateMovieEvents.postValue(MovieDetailsInteractor.RateMovieEvent.FetchSuccess(movieState))

        assertNotNull(postedViewState)
        assertEquals(View.INVISIBLE, postedViewState?.loadingVisibility)
        assertEquals(View.VISIBLE, postedViewState?.ratingBarVisibility)
        assertEquals(View.VISIBLE, postedViewState?.deleteVisibility)
        assertEquals(View.VISIBLE, postedViewState?.submitVisibility)
        assertEquals("aMovie", postedViewState?.movieTitle)
        assertEquals("aUrl", postedViewState?.movieImageUrl)
        assertEquals(2.75F, postedViewState?.rating)
    }

    @Test
    fun `Should map interactor FetchSuccess movie not rated event to ViewState`() {
        var postedViewState: RateMovieViewState? = null

        val param = RateMovieParam(
                movieId = 12.0,
                screenTitle = "aMovie",
                movieImageUrl = "aUrl"
        )

        val rating = MovieStateRate(
                isRated = false,
                value = null
        )
        val movieState = MovieState(
                id = 12.0,
                favorite = false,
                rated = rating,
                watchlist = false
        )

        subject.viewState.observeWith { viewState -> postedViewState = viewState }

        subject.onInit(param)

        lvRateMovieEvents.postValue(MovieDetailsInteractor.RateMovieEvent.FetchSuccess(movieState))

        assertNotNull(postedViewState)
        assertEquals(View.INVISIBLE, postedViewState?.loadingVisibility)
        assertEquals(View.VISIBLE, postedViewState?.ratingBarVisibility)
        assertEquals(View.INVISIBLE, postedViewState?.deleteVisibility)
        assertEquals(View.VISIBLE, postedViewState?.submitVisibility)
        assertEquals("aMovie", postedViewState?.movieTitle)
        assertEquals("aUrl", postedViewState?.movieImageUrl)
        assertEquals(0.0F, postedViewState?.rating)
    }

    @Test
    fun `Should fetch movie state and post loading in onInit`() {
        var postedViewState: RateMovieViewState? = null

        subject.viewState.observeWith { viewState -> postedViewState = viewState }

        val param = RateMovieParam(
                movieId = 12.0,
                screenTitle = "aMovie",
                movieImageUrl = "aUrl"
        )

        subject.onInit(param)

        assertNotNull(postedViewState)
        assertEquals(View.VISIBLE, postedViewState?.loadingVisibility)
        assertEquals(View.INVISIBLE, postedViewState?.ratingBarVisibility)
        assertEquals(View.INVISIBLE, postedViewState?.deleteVisibility)
        assertEquals(View.INVISIBLE, postedViewState?.submitVisibility)
        assertEquals("aMovie", postedViewState?.movieTitle)
        assertEquals("aUrl", postedViewState?.movieImageUrl)
        assertEquals(0.0F, postedViewState?.rating)

        verify { movieDetailsInteractor.fetchMovieRating(12.0) }
    }

    @Test
    fun `Should rate movie and post loading when onRateMovie`() {
        var postedViewState: RateMovieViewState? = null

        subject.viewState.observeWith { viewState -> postedViewState = viewState }

        subject.onInit(RateMovieParam(
                movieId = 12.0,
                screenTitle = "aMovie",
                movieImageUrl = "aUrl"
        ))

        subject.onRateMovie(2F)

        assertNotNull(postedViewState)
        assertEquals(View.VISIBLE, postedViewState?.loadingVisibility)
        assertEquals(View.INVISIBLE, postedViewState?.ratingBarVisibility)
        assertEquals(View.INVISIBLE, postedViewState?.deleteVisibility)
        assertEquals(View.INVISIBLE, postedViewState?.submitVisibility)
        assertEquals("aMovie", postedViewState?.movieTitle)
        assertEquals("aUrl", postedViewState?.movieImageUrl)
        assertEquals(0.0F, postedViewState?.rating)

        verify { movieDetailsInteractor.rateMovie(12.0, 4F) }
    }

    @Test
    fun `Should NOT rate movie onRateMovie when it has been rated with same value`() {
        subject.viewState.observeForever { }

        val rating = MovieStateRate(
                isRated = true,
                value = "5.5"
        )
        val movieState = MovieState(
                id = 12.0,
                favorite = false,
                rated = rating,
                watchlist = false
        )

        subject.onInit(RateMovieParam(
                movieId = 12.0,
                screenTitle = "aMovie",
                movieImageUrl = "aUrl"
        ))

        lvInteractorEvents.postValue(MovieStateEvent.FetchSuccess(movieState))

        subject.onRateMovie(5.5F)

        verify(exactly = 0) { movieDetailsInteractor.rateMovie(12.0, 4F) }
    }

    @Test
    fun `Should delete movie rating and post loading when onDeleteMovieRating`() {
        var postedViewState: RateMovieViewState? = null

        subject.viewState.observeWith { viewState -> postedViewState = viewState }

        subject.onInit(RateMovieParam(
                movieId = 12.0,
                screenTitle = "aMovie",
                movieImageUrl = "aUrl"
        ))

        subject.onDeleteMovieRating()

        assertNotNull(postedViewState)
        assertEquals(View.VISIBLE, postedViewState?.loadingVisibility)
        assertEquals(View.INVISIBLE, postedViewState?.ratingBarVisibility)
        assertEquals(View.INVISIBLE, postedViewState?.deleteVisibility)
        assertEquals(View.INVISIBLE, postedViewState?.submitVisibility)
        assertEquals("aMovie", postedViewState?.movieTitle)
        assertEquals("aUrl", postedViewState?.movieImageUrl)
        assertEquals(0.0F, postedViewState?.rating)

        verify { movieDetailsInteractor.deleteMovieRating(12.0) }
    }

    companion object {

        @JvmStatic
        fun rateMovieEvents() = listOf(
                arguments(
                        RateMovieUserMessages.RATE_SUCCESS,
                        MovieDetailsInteractor.RateMovieEvent.RateMovie(true)
                ),
                arguments(
                        RateMovieUserMessages.RATE_ERROR,
                        MovieDetailsInteractor.RateMovieEvent.RateMovie(false)
                ),
                arguments(
                        RateMovieUserMessages.DELETE_SUCCESS,
                        MovieDetailsInteractor.RateMovieEvent.RatingDeleted(true)
                ),
                arguments(
                        RateMovieUserMessages.DELETE_ERROR,
                        MovieDetailsInteractor.RateMovieEvent.RatingDeleted(false)
                )
        )
    }
}
