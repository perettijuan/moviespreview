package com.jpp.mpmoviedetails.rates

import android.view.View
import androidx.lifecycle.SavedStateHandle
import com.jpp.mpdomain.MovieState
import com.jpp.mpdomain.MovieStateRate
import com.jpp.mpdomain.usecase.DeleteMovieRatingUseCase
import com.jpp.mpdomain.usecase.GetMovieStateUseCase
import com.jpp.mpdomain.usecase.RateMovieUseCase
import com.jpp.mpdomain.usecase.Try
import com.jpp.mptestutils.CoroutineTestExtension
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(
    MockKExtension::class,
    InstantTaskExecutorExtension::class,
    CoroutineTestExtension::class
)
internal class RateMovieViewModelTest {

    @MockK
    private lateinit var getMovieStateUseCase: GetMovieStateUseCase

    @MockK
    private lateinit var rateMovieUseCase: RateMovieUseCase

    @MockK
    private lateinit var deleteMovieRatingUseCase: DeleteMovieRatingUseCase

    @MockK
    private lateinit var rateMovieNavigator: RateMovieNavigator

    private val savedStateHandle: SavedStateHandle by lazy {
        SavedStateHandle()
    }

    private val param = RateMovieParam(
        movieId = 12.0,
        screenTitle = "aMovie",
        movieImageUrl = "aUrl"
    )

    private lateinit var subject: RateMovieViewModel

    @BeforeEach
    fun setUp() {
        subject = RateMovieViewModel(
            getMovieStateUseCase,
            rateMovieUseCase,
            deleteMovieRatingUseCase,
            rateMovieNavigator,
            CoroutineTestExtension.testDispatcher,
            savedStateHandle
        )
    }

    @Test
    fun `Should fetch and show rated movie on init`() {
        var viewStatePosted: RateMovieViewState? = null

        val movieState = MovieState(
            id = 12.0,
            favorite = false,
            watchlist = true,
            rated = MovieStateRate(
                isRated = true,
                value = "5.5"
            )
        )

        coEvery { getMovieStateUseCase.execute(12.0) } returns Try.Success(movieState)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(param)

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.VISIBLE, viewStatePosted?.ratingBarVisibility)
        assertEquals(View.VISIBLE, viewStatePosted?.deleteVisibility)
        assertEquals(View.VISIBLE, viewStatePosted?.submitVisibility)
        assertEquals("aMovie", viewStatePosted?.movieTitle)
        assertEquals("aUrl", viewStatePosted?.movieImageUrl)
        assertEquals(2.75F, viewStatePosted?.rating)
    }

    @Test
    fun `Should fetch and show not rated movie on init`() {
        var viewStatePosted: RateMovieViewState? = null

        val movieState = MovieState(
            id = 12.0,
            favorite = false,
            watchlist = true,
            rated = MovieStateRate(
                isRated = false,
                value = null
            )
        )

        coEvery { getMovieStateUseCase.execute(12.0) } returns Try.Success(movieState)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(param)

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.deleteVisibility)
        assertEquals(View.VISIBLE, viewStatePosted?.ratingBarVisibility)
        assertEquals(View.VISIBLE, viewStatePosted?.submitVisibility)
        assertEquals("aMovie", viewStatePosted?.movieTitle)
        assertEquals("aUrl", viewStatePosted?.movieImageUrl)
        assertEquals(0.0F, viewStatePosted?.rating)
    }

    @Test
    fun `Should post user not logged and exit`() {
        var userMessagePosted: RateMovieEvent? = null

        subject.event.observeWith {
            it.actionIfNotHandled { userMessage ->
                userMessagePosted = userMessage
            }
        }

        coEvery { getMovieStateUseCase.execute(12.0) } returns Try.Failure(Try.FailureCause.UserNotLogged)

        subject.onInit(param)

        assertEquals(RateMovieEvent.USER_NOT_LOGGED, userMessagePosted)
        verify { rateMovieNavigator.navigateBack() }
    }

    @Test
    fun `Should post error and exit`() {
        var userMessagePosted: RateMovieEvent? = null

        subject.event.observeWith {
            it.actionIfNotHandled { userMessage ->
                userMessagePosted = userMessage
            }
        }

        coEvery { getMovieStateUseCase.execute(12.0) } returns Try.Failure(Try.FailureCause.Unknown)

        subject.onInit(param)

        assertEquals(RateMovieEvent.ERROR_FETCHING_DATA, userMessagePosted)
        verify { rateMovieNavigator.navigateBack() }
    }

    @Test
    fun `Should rate movie and exit`() {
        var userMessagePosted: RateMovieEvent? = null

        val movieState = MovieState(
            id = 12.0,
            favorite = false,
            watchlist = true,
            rated = MovieStateRate(
                isRated = true,
                value = "5.5"
            )
        )

        subject.event.observeWith {
            it.actionIfNotHandled { userMessage ->
                userMessagePosted = userMessage
            }
        }

        coEvery { getMovieStateUseCase.execute(12.0) } returns Try.Success(movieState)
        coEvery { rateMovieUseCase.execute(12.0, 8F) } returns Try.Success(Unit)

        subject.onInit(param)
        subject.onRateMovie(4F)

        assertEquals(RateMovieEvent.RATE_SUCCESS, userMessagePosted)
        verify { rateMovieNavigator.navigateBack() }
    }

    @Test
    fun `Should post error when adding rate and exit`() {
        var userMessagePosted: RateMovieEvent? = null

        val movieState = MovieState(
            id = 12.0,
            favorite = false,
            watchlist = true,
            rated = MovieStateRate(
                isRated = true,
                value = "5.5"
            )
        )

        subject.event.observeWith {
            it.actionIfNotHandled { userMessage ->
                userMessagePosted = userMessage
            }
        }

        coEvery { getMovieStateUseCase.execute(12.0) } returns Try.Success(movieState)
        coEvery { rateMovieUseCase.execute(12.0, 8F) } returns Try.Failure(Try.FailureCause.Unknown)

        subject.onInit(param)
        subject.onRateMovie(4F)

        assertEquals(RateMovieEvent.RATE_ERROR, userMessagePosted)
        verify { rateMovieNavigator.navigateBack() }
    }
}
