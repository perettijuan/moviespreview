package com.jpp.mp.screens.main.details

import androidx.lifecycle.Observer
import com.jpp.mp.InstantTaskExecutorExtension
import com.jpp.mp.resumedLifecycleOwner
import com.jpp.mp.screens.main.TestCoroutineDispatchers
import com.jpp.mpdomain.MovieAccountState
import com.jpp.mpdomain.usecase.details.GetMovieAccountStateUseCase
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class MovieActionsViewModelTest {

    @MockK
    private lateinit var getMovieAccountStateUseCase: GetMovieAccountStateUseCase

    private lateinit var subject: MovieActionsViewModel

    private val movieDetailId = 121.toDouble()

    @BeforeEach
    fun setUp() {
        subject = MovieActionsViewModel(TestCoroutineDispatchers(), getMovieAccountStateUseCase)
    }


    @ParameterizedTest
    @MethodSource("executeParameters")
    fun `Should push Hidden and execute use case to fetch view state on init`(ucResponse: GetMovieAccountStateUseCase.MovieAccountStateResult,
                                                                              expectedState: MovieActionsState) {
        val viewStatePosted = mutableListOf<MovieActionsState>()

        every { getMovieAccountStateUseCase.getMovieAccountState(any()) } returns ucResponse

        subject.actionsState().observe(resumedLifecycleOwner(), Observer {
            viewStatePosted.add(it)
        })

        subject.init(movieDetailId)

        assertTrue(viewStatePosted[0] is MovieActionsState.Hidden)
        assertEquals(viewStatePosted[1], expectedState)
        verify(exactly = 1) { getMovieAccountStateUseCase.getMovieAccountState(movieDetailId) }
    }

    companion object {

        @JvmStatic
        fun executeParameters() = listOf(
                Arguments.of(
                        GetMovieAccountStateUseCase.MovieAccountStateResult.ErrorUnknown,
                        MovieActionsState.Hidden
                ),
                Arguments.of(
                        GetMovieAccountStateUseCase.MovieAccountStateResult.UserNotLogged,
                        MovieActionsState.Shown(isFavorite = false)
                ),
                Arguments.of(
                        GetMovieAccountStateUseCase.MovieAccountStateResult.ErrorNoConnectivity,
                        MovieActionsState.Hidden
                ),
                Arguments.of(
                        GetMovieAccountStateUseCase.MovieAccountStateResult.Success(
                                MovieAccountState(121.toDouble(), favorite = true, rated = false, watchlist = false)),
                        MovieActionsState.Shown(isFavorite = true)
                ),
                Arguments.of(
                        GetMovieAccountStateUseCase.MovieAccountStateResult.Success(
                                MovieAccountState(121.toDouble(), favorite = false, rated = false, watchlist = false)),
                        MovieActionsState.Shown(isFavorite = false)
                )
        )
    }
}