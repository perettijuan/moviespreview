package com.jpp.mpmoviedetails

import android.view.View
import androidx.lifecycle.SavedStateHandle
import com.jpp.mpdomain.MovieState
import com.jpp.mpdomain.MovieStateRate
import com.jpp.mpdomain.usecase.GetMovieStateUseCase
import com.jpp.mpdomain.usecase.Try
import com.jpp.mpdomain.usecase.UpdateFavoriteMovieStateUseCase
import com.jpp.mpdomain.usecase.UpdateWatchlistMovieStateUseCase
import com.jpp.mptestutils.CoroutineTestExtension
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

@ExperimentalCoroutinesApi
@ExtendWith(
    MockKExtension::class,
    InstantTaskExecutorExtension::class,
    CoroutineTestExtension::class
)
internal class MovieDetailsActionViewModelTest {

    @RelaxedMockK
    private lateinit var getMovieStateUseCase: GetMovieStateUseCase

    @RelaxedMockK
    private lateinit var updateFavoriteUseCase: UpdateFavoriteMovieStateUseCase

    @RelaxedMockK
    private lateinit var updateWatchListUseCase: UpdateWatchlistMovieStateUseCase

    private val savedStateHandle: SavedStateHandle by lazy {
        SavedStateHandle()
    }

    private lateinit var subject: MovieDetailsActionViewModel

    @BeforeEach
    fun setUp() {
        subject = MovieDetailsActionViewModel(
            getMovieStateUseCase,
            updateFavoriteUseCase,
            updateWatchListUseCase,
            CoroutineTestExtension.testDispatcher,
            savedStateHandle
        )
    }

    @ParameterizedTest
    @MethodSource("movieActionEvents")
    fun `Should fetch movie state in onInit`(
        expected: MovieDetailActionViewState,
        movieState: MovieState
    ) {
        val movieId = 10.toDouble()
        var viewStatePosted: MovieDetailActionViewState? = null

        coEvery { getMovieStateUseCase.execute(movieId) } returns Try.Success(movieState)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(movieId)

        assertNotNull(viewStatePosted)
        assertEquals(expected.loadingVisibility, viewStatePosted?.loadingVisibility)
        assertEquals(expected.reloadButtonVisibility, viewStatePosted?.reloadButtonVisibility)
        assertEquals(expected.actionButtonVisibility, viewStatePosted?.actionButtonVisibility)
        assertEquals(expected.errorState, viewStatePosted?.errorState)
        assertEquals(expected.animate, viewStatePosted?.animate)
        assertEquals(expected.expanded, viewStatePosted?.expanded)

        assertEquals(expected.rateButtonState.visibility, viewStatePosted?.rateButtonState?.visibility)
        assertEquals(expected.rateButtonState.animateLoading, viewStatePosted?.rateButtonState?.animateLoading)
        assertEquals(expected.rateButtonState.asFilled, viewStatePosted?.rateButtonState?.asFilled)
        assertEquals(expected.rateButtonState.asClickable, viewStatePosted?.rateButtonState?.asClickable)

        assertEquals(expected.favoriteButtonState.visibility, viewStatePosted?.favoriteButtonState?.visibility)
        assertEquals(expected.favoriteButtonState.animateLoading, viewStatePosted?.favoriteButtonState?.animateLoading)
        assertEquals(expected.favoriteButtonState.asFilled, viewStatePosted?.favoriteButtonState?.asFilled)
        assertEquals(expected.favoriteButtonState.asClickable, viewStatePosted?.favoriteButtonState?.asClickable)

        assertEquals(expected.watchListButtonState.visibility, viewStatePosted?.watchListButtonState?.visibility)
        assertEquals(expected.watchListButtonState.animateLoading, viewStatePosted?.watchListButtonState?.animateLoading)
        assertEquals(expected.watchListButtonState.asFilled, viewStatePosted?.watchListButtonState?.asFilled)
        assertEquals(expected.watchListButtonState.asClickable, viewStatePosted?.watchListButtonState?.asClickable)
    }

    @Test
    fun `On error unknown should post all empty`() {
        val movieId = 10.toDouble()
        var viewStatePosted: MovieDetailActionViewState? = null

        coEvery { getMovieStateUseCase.execute(movieId) } returns Try.Failure(Try.FailureCause.Unknown)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(movieId)

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.reloadButtonVisibility)
        assertEquals(View.VISIBLE, viewStatePosted?.actionButtonVisibility)
        assertEquals(ActionErrorViewState.None, viewStatePosted?.errorState)
        assertEquals(false, viewStatePosted?.animate)
        assertEquals(false, viewStatePosted?.expanded)

        assertEquals(View.VISIBLE, viewStatePosted?.rateButtonState?.visibility)
        assertEquals(false, viewStatePosted?.rateButtonState?.animateLoading)
        assertEquals(false, viewStatePosted?.rateButtonState?.asFilled)
        assertEquals(true, viewStatePosted?.rateButtonState?.asClickable)

        assertEquals(View.VISIBLE, viewStatePosted?.favoriteButtonState?.visibility)
        assertEquals(false, viewStatePosted?.favoriteButtonState?.animateLoading)
        assertEquals(false, viewStatePosted?.favoriteButtonState?.asFilled)
        assertEquals(true, viewStatePosted?.favoriteButtonState?.asClickable)

        assertEquals(View.VISIBLE, viewStatePosted?.watchListButtonState?.visibility)
        assertEquals(false, viewStatePosted?.watchListButtonState?.animateLoading)
        assertEquals(false, viewStatePosted?.watchListButtonState?.asFilled)
        assertEquals(true, viewStatePosted?.watchListButtonState?.asClickable)
    }

    @ParameterizedTest
    @MethodSource("movieActionEvents")
    fun `Should animate expanded - not expanded`(
        expected: MovieDetailActionViewState,
        movieState: MovieState
    ) {
        val movieId = 10.toDouble()
        var viewStatePosted: MovieDetailActionViewState? = null

        coEvery { getMovieStateUseCase.execute(movieId) } returns Try.Success(movieState)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(movieId)

        subject.onMainActionSelected()

        assertNotNull(viewStatePosted)
        assertEquals(expected.loadingVisibility, viewStatePosted?.loadingVisibility)
        assertEquals(expected.reloadButtonVisibility, viewStatePosted?.reloadButtonVisibility)
        assertEquals(expected.actionButtonVisibility, viewStatePosted?.actionButtonVisibility)
        assertEquals(expected.errorState, viewStatePosted?.errorState)

        assertEquals(expected.rateButtonState.visibility, viewStatePosted?.rateButtonState?.visibility)
        assertEquals(expected.rateButtonState.animateLoading, viewStatePosted?.rateButtonState?.animateLoading)
        assertEquals(expected.rateButtonState.asFilled, viewStatePosted?.rateButtonState?.asFilled)
        assertEquals(expected.rateButtonState.asClickable, viewStatePosted?.rateButtonState?.asClickable)

        assertEquals(expected.favoriteButtonState.visibility, viewStatePosted?.favoriteButtonState?.visibility)
        assertEquals(expected.favoriteButtonState.animateLoading, viewStatePosted?.favoriteButtonState?.animateLoading)
        assertEquals(expected.favoriteButtonState.asFilled, viewStatePosted?.favoriteButtonState?.asFilled)
        assertEquals(expected.favoriteButtonState.asClickable, viewStatePosted?.favoriteButtonState?.asClickable)

        assertEquals(expected.watchListButtonState.visibility, viewStatePosted?.watchListButtonState?.visibility)
        assertEquals(expected.watchListButtonState.animateLoading, viewStatePosted?.watchListButtonState?.animateLoading)
        assertEquals(expected.watchListButtonState.asFilled, viewStatePosted?.watchListButtonState?.asFilled)
        assertEquals(expected.watchListButtonState.asClickable, viewStatePosted?.watchListButtonState?.asClickable)

        assertEquals(true, viewStatePosted?.animate)
        assertEquals(!expected.expanded, viewStatePosted?.expanded)
    }

    @ParameterizedTest
    @MethodSource("movieActionEvents")
    fun `Should toggle expanded`(
        expected: MovieDetailActionViewState,
        movieState: MovieState
    ) {
        val movieId = 10.toDouble()
        var viewStatePosted: MovieDetailActionViewState? = null

        coEvery { getMovieStateUseCase.execute(movieId) } returns Try.Success(movieState)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(movieId)

        subject.onMainActionSelected()

        assertEquals(true, viewStatePosted?.animate)
        assertEquals(!expected.expanded, viewStatePosted?.expanded)

        subject.onMainActionSelected()

        assertEquals(true, viewStatePosted?.animate)
        assertEquals(expected.expanded, viewStatePosted?.expanded)
    }

    @ParameterizedTest
    @MethodSource("movieActionEvents")
    fun `Should update favorite state`(
        expected: MovieDetailActionViewState,
        movieState: MovieState
    ) {
        val movieId = 10.toDouble()
        var viewStatePosted: MovieDetailActionViewState? = null

        coEvery { getMovieStateUseCase.execute(movieId) } returns Try.Success(movieState)
        coEvery { updateFavoriteUseCase.execute(movieId, !expected.favoriteButtonState.asFilled) } returns Try.Success(Unit)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(movieId)

        subject.onFavoriteStateChanged()

        assertNotNull(viewStatePosted)

        assertEquals(expected.favoriteButtonState.visibility, viewStatePosted?.favoriteButtonState?.visibility)
        assertEquals(expected.favoriteButtonState.animateLoading, viewStatePosted?.favoriteButtonState?.animateLoading)
        assertEquals(!expected.favoriteButtonState.asFilled, viewStatePosted?.favoriteButtonState?.asFilled)
        assertEquals(expected.favoriteButtonState.asClickable, viewStatePosted?.favoriteButtonState?.asClickable)
    }

    @Test
    fun `Should update favorite state when user not logged`() {
        val movieId = 10.toDouble()
        var viewStatePosted: MovieDetailActionViewState? = null

        coEvery { getMovieStateUseCase.execute(movieId) } returns Try.Success(mockk(relaxed = true))
        coEvery { updateFavoriteUseCase.execute(movieId, any()) } returns Try.Failure(Try.FailureCause.UserNotLogged)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(movieId)

        subject.onFavoriteStateChanged()

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.reloadButtonVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.actionButtonVisibility)
        assertEquals(ActionErrorViewState.UserNotLogged, viewStatePosted?.errorState)
        assertEquals(true, viewStatePosted?.animate)
        assertEquals(false, viewStatePosted?.expanded)

        assertEquals(View.INVISIBLE, viewStatePosted?.rateButtonState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.favoriteButtonState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.watchListButtonState?.visibility)
    }

    @ParameterizedTest
    @MethodSource("movieActionEvents")
    fun `Should update watchlist state`(
        expected: MovieDetailActionViewState,
        movieState: MovieState
    ) {
        val movieId = 10.toDouble()
        var viewStatePosted: MovieDetailActionViewState? = null

        coEvery { getMovieStateUseCase.execute(movieId) } returns Try.Success(movieState)
        coEvery { updateWatchListUseCase.execute(movieId, !expected.watchListButtonState.asFilled) } returns Try.Success(Unit)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(movieId)

        subject.onWatchlistStateChanged()

        assertNotNull(viewStatePosted)

        assertEquals(expected.watchListButtonState.visibility, viewStatePosted?.watchListButtonState?.visibility)
        assertEquals(expected.watchListButtonState.animateLoading, viewStatePosted?.watchListButtonState?.animateLoading)
        assertEquals(!expected.watchListButtonState.asFilled, viewStatePosted?.watchListButtonState?.asFilled)
        assertEquals(expected.watchListButtonState.asClickable, viewStatePosted?.watchListButtonState?.asClickable)
    }

    @Test
    fun `Should update watchlist state when user not logged`() {
        val movieId = 10.toDouble()
        var viewStatePosted: MovieDetailActionViewState? = null

        coEvery { getMovieStateUseCase.execute(movieId) } returns Try.Success(mockk(relaxed = true))
        coEvery { updateWatchListUseCase.execute(movieId, any()) } returns Try.Failure(Try.FailureCause.UserNotLogged)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(movieId)

        subject.onWatchlistStateChanged()

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.reloadButtonVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.actionButtonVisibility)
        assertEquals(ActionErrorViewState.UserNotLogged, viewStatePosted?.errorState)
        assertEquals(true, viewStatePosted?.animate)
        assertEquals(false, viewStatePosted?.expanded)

        assertEquals(View.INVISIBLE, viewStatePosted?.rateButtonState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.favoriteButtonState?.visibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.watchListButtonState?.visibility)
    }

    companion object {

        @JvmStatic
        fun movieActionEvents() = listOf(
            arguments(
                MovieDetailActionViewState.showLoading()
                    .showLoaded(ActionButtonState().asEmpty(), ActionButtonState().asEmpty()),
                MOVIE_STATE_NO_FAVORITE_NO_WATCHLIST
            ),
            arguments(
                MovieDetailActionViewState.showLoading()
                    .showLoaded(ActionButtonState().asEmpty(), ActionButtonState().asFilled()),
                MOVIE_STATE_FAVORITE
            ),
            arguments(
                MovieDetailActionViewState.showLoading()
                    .showLoaded(ActionButtonState().asFilled(), ActionButtonState().asFilled()),
                MOVIE_STATE_FAVORITE_AND_WATCHLIST
            )

        )

        private val MOVIE_STATE_NO_FAVORITE_NO_WATCHLIST = MovieState(
            id = 12.0,
            favorite = false,
            watchlist = false,
            rated = MovieStateRate(false)
        )

        private val MOVIE_STATE_FAVORITE = MovieState(
            id = 12.0,
            favorite = true,
            watchlist = false,
            rated = MovieStateRate(false)
        )

        private val MOVIE_STATE_FAVORITE_AND_WATCHLIST = MovieState(
            id = 12.0,
            favorite = true,
            watchlist = true,
            rated = MovieStateRate(false)
        )
    }
}
