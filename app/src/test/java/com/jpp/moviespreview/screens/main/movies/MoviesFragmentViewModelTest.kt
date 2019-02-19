package com.jpp.moviespreview.screens.main.movies

import androidx.lifecycle.Observer
import com.jpp.moviespreview.utiltest.CurrentThreadExecutorService
import com.jpp.moviespreview.utiltest.InstantTaskExecutorExtension
import com.jpp.moviespreview.utiltest.resumedLifecycleOwner
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.usecase.movies.ConfigMovieUseCase
import com.jpp.mpdomain.usecase.movies.GetMoviesUseCase
import com.jpp.mpdomain.usecase.movies.GetMoviesUseCaseResult
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.Executor

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

        every { getMoviesUseCase.getMoviePageForSection(any(), any()) } returns successUCExecution(moviesInPageCount)
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

        every { getMoviesUseCase.getMoviePageForSection(any(), any()) } returns successUCExecution(moviesInPageCount)
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

        every { getMoviesUseCase.getMoviePageForSection(any(), any()) } returns GetMoviesUseCaseResult.ErrorUnknown

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

        every { getMoviesUseCase.getMoviePageForSection(any(), any()) } returns GetMoviesUseCaseResult.ErrorUnknown

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


    private inner class TestMoviesFragmentViewModel(getMoviesUseCase: GetMoviesUseCase,
                                                    configMovieUseCase: ConfigMovieUseCase,
                                                    networkExecutor: Executor,
                                                    movieSectionForTest: MovieSection)
        : MoviesFragmentViewModel(getMoviesUseCase, configMovieUseCase, networkExecutor) {
        override val movieSection: MovieSection = movieSectionForTest
    }


    private companion object {

        fun successUCExecution(moviesInPageCount: Int) =  GetMoviesUseCaseResult.Success(createMoviesPage(1, moviesInPageCount))

        private fun createMoviesPage(page: Int, totalResults: Int) = MoviePage(
                page = page,
                results = createMoviesForPage(page, totalResults),
                total_pages = 10,
                total_results = 1000
        )

        private fun createMoviesForPage(page: Int, totalResults: Int = 10): List<Movie> {
            return mutableListOf<Movie>().apply {
                for (i in 1..totalResults) {
                    add(Movie(
                            id = (page + i).toDouble(),
                            poster_path = "/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                            backdrop_path = "/m110vLaDDOCca4hfOcS5mK5cDke.jpg",
                            title = "Movie $i",
                            original_title = "Movie Title $i",
                            original_language = "US",
                            overview = "Overview for $i",
                            release_date = "aReleaseDate for $i",
                            vote_count = i.toDouble(),
                            vote_average = i.toFloat(),
                            popularity = i.toFloat()
                    ))
                }
            }
        }
    }
}