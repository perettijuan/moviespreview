package com.jpp.mpdomain.repository.movies

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.paging.PageKeyedDataSource
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.handlers.ConnectivityHandler
import com.jpp.mpdomain.handlers.configuration.ConfigurationHandler
import com.jpp.mpdomain.repository.configuration.ConfigurationApi
import com.jpp.mpdomain.repository.configuration.ConfigurationDb
import com.jpp.mpdomain.utils.CurrentThreadExecutorService
import com.jpp.mpdomain.utils.InstantTaskExecutorExtension
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class MovieListRepositoryTest {

    @RelaxedMockK
    private lateinit var moviesApi: MoviesApi
    @RelaxedMockK
    private lateinit var moviesDb: MoviesDb
    @RelaxedMockK
    private lateinit var configurationApi: ConfigurationApi
    @RelaxedMockK
    private lateinit var configurationDb: ConfigurationDb
    @RelaxedMockK
    private lateinit var connectivityHandler: ConnectivityHandler
    @RelaxedMockK
    private lateinit var configurationHandler: ConfigurationHandler


    private val movieSection = MovieSection.Playing
    private val targetBackdropSize = 60
    private val targetPosterSize = 70
    private val moviesMapper: ((Movie) -> Movie) = { movie -> movie }


    private lateinit var subject: MovieListRepository


    @BeforeEach
    fun setUp() {
        subject = MovieListRepositoryImpl(moviesApi,
                moviesDb,
                configurationApi,
                configurationDb,
                connectivityHandler,
                configurationHandler,
                CurrentThreadExecutorService())
    }


    /**
     * Tests that GetMoviesDataSource delegates the retrieval of the first movie page to
     * the repository, that the repository fetches the data from the database and
     * the ds notifies the callback.
     */
    @Test
    fun `DataSource should ask to repository to fetch movies with first page and notify result to callback`() {
        val dsCallback = mockk<PageKeyedDataSource.LoadInitialCallback<Int, Movie>>(relaxed = true)
        val moviePage = mockk<MoviePage>()
        val movies = listOf<Movie>(mockk(), mockk(), mockk())
        val expectedNextPage = 2

        every { moviePage.results } returns movies
        every { moviesDb.getMoviePageForSection(any(), any()) } returns moviePage

        executeTestUsingDataSource { ds ->
            ds.loadInitial(PageKeyedDataSource.LoadInitialParams(1, false), dsCallback)

            verify { moviesDb.getMoviePageForSection(1, movieSection) }
            verify { dsCallback.onResult(any(), null, expectedNextPage) }
        }
    }

    /**
     * Tests that GetMoviesDataSource delegates the retrieval of any movie page to
     * the repository, that the repository fetches the data from the database and
     * the ds notifies the callback.
     */
    @Test
    fun `DataSource should ask to repository to fetch movies with any other page and notify result to callback`() {
        val dsCallback = mockk<PageKeyedDataSource.LoadCallback<Int, Movie>>(relaxed = true)
        val moviePage = mockk<MoviePage>()
        val movies = listOf<Movie>(mockk(), mockk(), mockk())
        val expectedNextPage = 3

        every { moviePage.results } returns movies
        every { moviesDb.getMoviePageForSection(any(), any()) } returns moviePage

        executeTestUsingDataSource { ds ->
            ds.loadAfter(PageKeyedDataSource.LoadParams(2, 1), dsCallback)

            verify { moviesDb.getMoviePageForSection(2, movieSection) }
            verify { dsCallback.onResult(any(), expectedNextPage) }
        }
    }

    @Test
    fun `Repository should fetch data from API when not available locally and update the local data`() {
        val moviePage = mockk<MoviePage>()
        val movies = listOf<Movie>(mockk(), mockk(), mockk())

        every { moviePage.results } returns movies
        every { moviesDb.getMoviePageForSection(any(), any()) } returns null
        every { moviesApi.getNowPlayingMoviePage(any()) } returns moviePage

        executeMovieFetchingInRepository(2) {
            verify { moviesDb.getMoviePageForSection(2, movieSection) }
            verify { moviesApi.getNowPlayingMoviePage(2) }
            verify { moviesDb.saveMoviePageForSection(moviePage, movieSection) }
        }
    }


    private fun executeTestUsingDataSource(test: (PageKeyedDataSource<Int, Movie>) -> Unit) {
        val lifecycleOwner: LifecycleOwner = mockk()
        val lifecycle = LifecycleRegistry(lifecycleOwner)

        every { lifecycleOwner.lifecycle } returns lifecycle

        val listing = subject.moviePageForSection(
                movieSection,
                targetBackdropSize,
                targetPosterSize,
                moviesMapper
        )

        listing.pagedList.observe(lifecycleOwner, Observer {
            val dataSource = it.dataSource as PageKeyedDataSource<Int, Movie>
            test(dataSource)
        })

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }


    private fun executeMovieFetchingInRepository(page: Int, verification: () -> Unit) {
        executeTestUsingDataSource { ds ->
            ds.loadAfter(PageKeyedDataSource.LoadParams(page, 1), mockk(relaxed = true))
            verification.invoke()
        }
    }
}