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
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.*
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
        expected: MovieActionsViewState,
        movieState: MovieState
    ) {
        val movieId = 10.toDouble()
        var viewStatePosted: MovieActionsViewState? = null

        coEvery { getMovieStateUseCase.execute(movieId) } returns Try.Success(movieState)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(movieId)

        assertNotNull(viewStatePosted)
        assertEquals(expected.visibility, viewStatePosted?.visibility)

        assertEquals(expected.favoriteButtonState.imageRes, viewStatePosted?.favoriteButtonState?.imageRes)
        assertEquals(expected.favoriteButtonState.asClickable, viewStatePosted?.favoriteButtonState?.asClickable)

        assertEquals(expected.watchListButtonState.imageRes, viewStatePosted?.watchListButtonState?.imageRes)
        assertEquals(expected.watchListButtonState.asClickable, viewStatePosted?.watchListButtonState?.asClickable)

        assertEquals(expected.rateImage, viewStatePosted?.rateImage)
        assertEquals(expected.creditsText, viewStatePosted?.creditsText)
    }

    @Test
    fun `On error unknown should post all empty`() {
        val movieId = 10.toDouble()
        var viewStatePosted: MovieActionsViewState? = null

        coEvery { getMovieStateUseCase.execute(movieId) } returns Try.Failure(Try.FailureCause.Unknown)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(movieId)

        assertNotNull(viewStatePosted)
        assertEquals(View.VISIBLE, viewStatePosted?.visibility)
        assertEquals(R.drawable.ic_rate_empty, viewStatePosted?.rateImage)
        assertEquals(R.string.movie_credits_title, viewStatePosted?.creditsText)

        assertEquals(R.drawable.ic_favorite_empty, viewStatePosted?.favoriteButtonState?.imageRes)
        assertTrue(viewStatePosted?.favoriteButtonState?.asClickable ?: false)

        assertEquals(R.drawable.ic_watchlist_empty, viewStatePosted?.watchListButtonState?.imageRes)
        assertTrue(viewStatePosted?.watchListButtonState?.asClickable ?: false)
    }


    @ParameterizedTest
    @MethodSource("movieActionExecutedEvents")
    fun `Should update favorite state`(
        expected: MovieActionsViewState,
        movieState: MovieState
    ) {
        val movieId = 10.toDouble()
        var viewStatePosted: MovieActionsViewState? = null

        coEvery { getMovieStateUseCase.execute(movieId) } returns Try.Success(movieState)
        coEvery { updateFavoriteUseCase.execute(movieId, any()) } returns Try.Success(Unit)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(movieId)

        subject.onFavoriteStateChanged()

        assertNotNull(viewStatePosted)

        assertEquals(expected.favoriteButtonState.asClickable, viewStatePosted?.favoriteButtonState?.asClickable)
        assertEquals(expected.favoriteButtonState.imageRes, viewStatePosted?.favoriteButtonState?.imageRes)

        coVerify { updateFavoriteUseCase.execute(movieId, !movieState.favorite) }
    }

    @Test
    fun `Should update favorite state when user not logged`() {
        val movieId = 10.toDouble()
        var viewStatePosted: MovieActionsViewState? = null
        var postedEvent: MovieActionsEvent? = null

        coEvery { getMovieStateUseCase.execute(movieId) } returns Try.Success(mockk(relaxed = true))
        coEvery { updateFavoriteUseCase.execute(movieId, any()) } returns Try.Failure(Try.FailureCause.UserNotLogged)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.events.observeWith { handledEvent -> postedEvent = handledEvent.peekContent() }

        subject.onInit(movieId)

        subject.onFavoriteStateChanged()

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.visibility)

        assertNotNull(postedEvent)
        assertTrue(postedEvent is MovieActionsEvent.ShowUserNotLogged)
    }

    @ParameterizedTest
    @MethodSource("movieActionExecutedEvents")
    fun `Should update watchlist state`(
        expected: MovieActionsViewState,
        movieState: MovieState
    ) {
        val movieId = 10.toDouble()
        var viewStatePosted: MovieActionsViewState? = null

        coEvery { getMovieStateUseCase.execute(movieId) } returns Try.Success(movieState)
        coEvery { updateWatchListUseCase.execute(movieId, any()) } returns Try.Success(Unit)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(movieId)

        subject.onWatchlistStateChanged()

        assertNotNull(viewStatePosted)

        assertEquals(expected.watchListButtonState.asClickable, viewStatePosted?.watchListButtonState?.asClickable)
        assertEquals(expected.watchListButtonState.asClickable, viewStatePosted?.watchListButtonState?.asClickable)
    }

    companion object {

        @JvmStatic
        fun movieActionEvents() = listOf(
            arguments(
                MovieActionsViewState.showLoading()
                    .showLoadedNoRating(
                        favoriteButtonState = ActionButtonState().noFavorite(),
                        watchListButtonState = ActionButtonState().noWatchList()
                    ),
                MOVIE_STATE_NO_FAVORITE_NO_WATCHLIST
            ),
            arguments(
                MovieActionsViewState.showLoading()
                    .showLoadedNoRating(
                        favoriteButtonState = ActionButtonState().favorite(),
                        watchListButtonState = ActionButtonState().noWatchList()
                    ),
                MOVIE_STATE_FAVORITE
            ),
            arguments(
                MovieActionsViewState.showLoading()
                    .showLoadedNoRating(
                        favoriteButtonState = ActionButtonState().favorite(),
                        watchListButtonState = ActionButtonState().watchList()
                    ),
                MOVIE_STATE_FAVORITE_AND_WATCHLIST
            )
        )

        @JvmStatic
        fun movieActionExecutedEvents() = listOf(
            arguments(
                MovieActionsViewState.showLoading()
                    .showLoadedNoRating(
                        favoriteButtonState = ActionButtonState().favorite(),
                        watchListButtonState = ActionButtonState().noWatchList()
                    ),
                MOVIE_STATE_NO_FAVORITE_NO_WATCHLIST
            ),
            arguments(
                MovieActionsViewState.showLoading()
                    .showLoadedNoRating(
                        favoriteButtonState = ActionButtonState().noFavorite(),
                        watchListButtonState = ActionButtonState().noWatchList()
                    ),
                MOVIE_STATE_FAVORITE
            ),
            arguments(
                MovieActionsViewState.showLoading()
                    .showLoadedNoRating(
                        favoriteButtonState = ActionButtonState().noFavorite(),
                        watchListButtonState = ActionButtonState().watchList()
                    ),
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
