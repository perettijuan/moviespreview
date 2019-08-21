package com.jpp.mp.screens.main.movies

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.common.coroutines.CoroutineDispatchers
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.interactors.ImagesPathInteractor
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class MovieListViewModelTest {

    @RelaxedMockK
    private lateinit var movieListInteractor: MovieListInteractor

    @MockK
    private lateinit var imagesPathInteractor: ImagesPathInteractor

    private val lvInteractorEvents = MutableLiveData<MovieListInteractor.MovieListEvent>()

    private lateinit var subject: MovieListViewModel

    @BeforeEach
    fun setUp() {
        every { movieListInteractor.events } returns lvInteractorEvents

        val dispatchers = object : CoroutineDispatchers {
            override fun main(): CoroutineDispatcher = Dispatchers.Unconfined
            override fun default(): CoroutineDispatcher = Dispatchers.Unconfined
        }

        subject = MovieListViewModel(
                dispatchers,
                movieListInteractor,
                imagesPathInteractor
        )
    }

    @Test
    fun `Should post no connectivity error when disconnected`() {
        var viewStatePosted: MovieListViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        subject.onInitWithPlayingSection(10, 10)

        lvInteractorEvents.postValue(MovieListInteractor.MovieListEvent.NotConnectedToNetwork)

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(true, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @Test
    fun `Should retry to fetch data when not connected and retry is executed`() {
        var viewStatePosted: MovieListViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        subject.onInitWithPlayingSection(10, 12)
        lvInteractorEvents.postValue(MovieListInteractor.MovieListEvent.NotConnectedToNetwork)

        viewStatePosted?.let {
            it.errorViewState.errorHandler?.invoke()
            verify(exactly = 2) { movieListInteractor.fetchMoviePageForSection(1, MovieSection.Playing, any()) }
        } ?: fail()
    }

    @Test
    fun `Should post error when failing to fetch movies`() {
        var viewStatePosted: MovieListViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        subject.onInitWithPlayingSection(10, 10)

        lvInteractorEvents.postValue(MovieListInteractor.MovieListEvent.UnknownError)

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(false, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @Test
    fun `Should retry to fetch data when error unknown and retry is executed`() {
        var viewStatePosted: MovieListViewState? = null

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        subject.onInitWithPlayingSection(10, 12)
        lvInteractorEvents.postValue(MovieListInteractor.MovieListEvent.UnknownError)

        viewStatePosted?.let {
            it.errorViewState.errorHandler?.invoke()
            verify(exactly = 2) { movieListInteractor.fetchMoviePageForSection(1, MovieSection.Playing, any()) }
        } ?: fail()
    }

    @Test
    fun `Should fetch movies, adapt result to UI and post value`() {
        var viewStatePosted: MovieListViewState? = null
        val mockedList = getMockedMovies()
        val slot = slot<(List<Movie>) -> Unit>()

        every { imagesPathInteractor.configurePathMovie(any(), any(), any()) } answers { arg(2) }
        every { movieListInteractor.fetchMoviePageForSection(any(), any(), capture(slot)) } answers { slot.captured.invoke(mockedList) }

        subject.viewStates.observeWith { it.actionIfNotHandled { viewState -> viewStatePosted = viewState } }

        subject.onInitWithPlayingSection(10, 10)

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.contentViewState?.visibility)
        assertEquals(mockedList.size, viewStatePosted?.contentViewState?.movieList?.size)

        verify { movieListInteractor.fetchMoviePageForSection(1, MovieSection.Playing, any()) }
        verify(exactly = mockedList.size) { imagesPathInteractor.configurePathMovie(10, 10, any()) }
    }


    private fun getMockedMovies(): List<Movie> {
        return mutableListOf<Movie>().apply {
            for (i in 0..50) {
                add(
                        Movie(
                                id = i.toDouble(),
                                title = "title$i",
                                original_language = "oTitle$i",
                                overview = "overview$i",
                                release_date = "releaseDate$i",
                                original_title = "originalLanguage$i",
                                poster_path = "posterPath$i",
                                backdrop_path = "backdropPath$i",
                                vote_count = i.toDouble(),
                                vote_average = i.toFloat(),
                                popularity = i.toFloat()
                        )
                )
            }
        }
    }

}