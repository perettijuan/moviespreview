package com.jpp.moviespreview.screens.main.movies

import androidx.lifecycle.Observer
import com.jpp.moviespreview.utiltest.CurrentThreadExecutorService
import com.jpp.moviespreview.InstantTaskExecutorExtension
import com.jpp.moviespreview.resumedLifecycleOwner
import com.jpp.moviespreview.utiltest.successGetMoviesUCExecution
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.usecase.movies.ConfigMovieUseCase
import com.jpp.mpdomain.usecase.movies.GetMoviesUseCase
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.Executor
import com.jpp.mpdomain.usecase.movies.GetMoviesUseCase.GetMoviesResult.*

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class MoviesFragmentViewModelTest {

    @MockK
    private lateinit var getMoviesUseCase: GetMoviesUseCase

    @MockK
    private lateinit var configMovieUseCase: ConfigMovieUseCase

    private lateinit var subject: MoviesFragmentViewModel

    private val movieSection: MovieSection = MovieSection.Playing


    @BeforeEach
    fun setUp() {
        subject = TestMoviesFragmentViewModel(
                getMoviesUseCase = getMoviesUseCase,
                configMovieUseCase = configMovieUseCase,
                networkExecutor = CurrentThreadExecutorService(),
                movieSectionForTest = movieSection
        )
    }

    @Test
    fun `Should post Loading, fetch first movies page and then post InitialPageLoaded when VM is not initialized`() {
        val viewStates = mutableListOf<MoviesViewState>()
        val moviesInPageCount = 10

        every { getMoviesUseCase.getMoviePageForSection(any(), any()) } returns successGetMoviesUCExecution(moviesInPageCount)
        every { configMovieUseCase.configure(any(), any(), any()) } answers { arg(2) }

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            viewStates.add(it)
        })

        subject.init(moviePosterSize = 5, movieBackdropSize = 10)

        assertTrue(viewStates[0] is MoviesViewState.Loading)
        assertTrue(viewStates[1] is MoviesViewState.InitialPageLoaded)

        verify(exactly = 1) { getMoviesUseCase.getMoviePageForSection(1, movieSection) }

        val pagedList = (viewStates[1] as MoviesViewState.InitialPageLoaded).pagedList
        assertEquals(moviesInPageCount, pagedList.size)
    }

    @Test
    fun `Should do nothing when already initialized`() {
        val viewStates = mutableListOf<MoviesViewState>()
        val moviesInPageCount = 10

        every { getMoviesUseCase.getMoviePageForSection(any(), any()) } returns successGetMoviesUCExecution(moviesInPageCount)
        every { configMovieUseCase.configure(any(), any(), any()) } answers { arg(2) }

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            viewStates.add(it)
        })

        subject.init(moviePosterSize = 5, movieBackdropSize = 10)
        // at this point, it should be initialized
        subject.init(moviePosterSize = 5, movieBackdropSize = 10)

        assertEquals(2, viewStates.size)
        verify(exactly = 1) { getMoviesUseCase.getMoviePageForSection(1, movieSection) }
    }

    @Test
    fun `Should allow retrying when failed to load the first page`() {
        val viewStates = mutableListOf<MoviesViewState>()

        every { getMoviesUseCase.getMoviePageForSection(any(), any()) } returns ErrorUnknown

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            viewStates.add(it)
        })

        subject.init(moviePosterSize = 5, movieBackdropSize = 10)

        assertTrue(viewStates[0] is MoviesViewState.Loading)
        assertTrue(viewStates[1] is MoviesViewState.ErrorUnknown)

        subject.retryMoviesFetch()

        assertTrue(viewStates[2] is MoviesViewState.Loading)
        assertTrue(viewStates[3] is MoviesViewState.ErrorUnknown)

        verify(exactly = 2) { getMoviesUseCase.getMoviePageForSection(1, movieSection) }
    }

    /*
     * This scenario verifies:
     * 1 - VM init.
     * 2 - UC failed.
     * 3 - Never retried.
     * 4 - Rotate device -> VM.init again should do nothing
     */
    @Test
    fun `Should do nothing when failed and is already initialized`() {
        val viewStates = mutableListOf<MoviesViewState>()

        every { getMoviesUseCase.getMoviePageForSection(any(), any()) } returns ErrorUnknown

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            viewStates.add(it)
        })

        subject.init(moviePosterSize = 5, movieBackdropSize = 10)

        assertTrue(viewStates[0] is MoviesViewState.Loading)
        assertTrue(viewStates[1] is MoviesViewState.ErrorUnknown)

        subject.init(moviePosterSize = 5, movieBackdropSize = 10)

        assertEquals(2, viewStates.size)
        verify(exactly = 1) { getMoviesUseCase.getMoviePageForSection(1, movieSection) }
    }

    @Test
    fun `Should post ErrorNoConnectivity when not connected to network detected`() {
        var lastState: MoviesViewState = MoviesViewState.Loading

        every { getMoviesUseCase.getMoviePageForSection(any(), any()) } returns ErrorNoConnectivity

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            lastState = it
        })

        subject.init(moviePosterSize = 5, movieBackdropSize = 10)

        assertTrue(lastState is MoviesViewState.ErrorNoConnectivity)
    }

    @Test
    fun `Should post ErrorUnknown when not connected to network detected`() {
        var lastState: MoviesViewState = MoviesViewState.Loading

        every { getMoviesUseCase.getMoviePageForSection(any(), any()) } returns ErrorUnknown

        subject.viewState().observe(resumedLifecycleOwner(), Observer {
            lastState = it
        })

        subject.init(moviePosterSize = 5, movieBackdropSize = 10)

        assertTrue(lastState is MoviesViewState.ErrorUnknown)
    }


    private inner class TestMoviesFragmentViewModel(getMoviesUseCase: GetMoviesUseCase,
                                                    configMovieUseCase: ConfigMovieUseCase,
                                                    networkExecutor: Executor,
                                                    movieSectionForTest: MovieSection)
        : MoviesFragmentViewModel(getMoviesUseCase, configMovieUseCase, networkExecutor) {
        override val movieSection: MovieSection = movieSectionForTest
    }
}