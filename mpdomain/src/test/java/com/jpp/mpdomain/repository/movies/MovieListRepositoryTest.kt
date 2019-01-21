package com.jpp.mpdomain.repository.movies

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.paging.PageKeyedDataSource
import com.jpp.mpdomain.*
import com.jpp.mpdomain.handlers.ConnectivityHandler
import com.jpp.mpdomain.handlers.configuration.ConfigurationHandler
import com.jpp.mpdomain.repository.RepositoryState
import com.jpp.mpdomain.repository.configuration.ConfigurationApi
import com.jpp.mpdomain.repository.configuration.ConfigurationDb
import com.jpp.mpdomain.utils.CurrentThreadExecutorService
import com.jpp.moviespreview.utiltest.InstantTaskExecutorExtension
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
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

    @Test
    fun `Repository should config movies path using the application configuration`() {
        val moviePage = mockk<MoviePage>()
        val movieMock = mockk<Movie>()
        val movies = listOf(movieMock)

        val appConfig = mockk<AppConfiguration>()
        val imagesConfig = mockk<ImagesConfiguration>()

        every { moviePage.results } returns movies
        every { moviesDb.getMoviePageForSection(any(), any()) } returns moviePage
        every { appConfig.images } returns imagesConfig
        every { configurationDb.getAppConfiguration() } returns appConfig

        executeMovieFetchingInRepository(2) {
            verify { configurationHandler.configureMovieImagesPath(movieMock, imagesConfig, targetBackdropSize, targetPosterSize) }
        }
    }

    @Test
    fun `Repository should fetch config from API and store it locally when no data is in database`() {
        val moviePage = mockk<MoviePage>()
        val movieMock = mockk<Movie>()
        val movies = listOf(movieMock)

        val appConfig = mockk<AppConfiguration>()
        val imagesConfig = mockk<ImagesConfiguration>()

        every { moviePage.results } returns movies
        every { moviesDb.getMoviePageForSection(any(), any()) } returns moviePage
        every { appConfig.images } returns imagesConfig
        every { configurationDb.getAppConfiguration() } returns null
        every { configurationApi.getAppConfiguration() } returns appConfig

        executeMovieFetchingInRepository(2) {
            verify { configurationDb.getAppConfiguration() }
            verify { configurationDb.saveAppConfiguration(appConfig) }
            verify { configurationApi.getAppConfiguration() }
        }
    }

    @Test
    fun `Repository should notify error unknown with items when an error occurs retrieving movie page`() {
        every { moviesDb.getMoviePageForSection(any(), any()) } returns null
        every { moviesApi.getNowPlayingMoviePage(any()) } returns null

        every { connectivityHandler.isConnectedToNetwork() } returns true

        executeMovieFetchingInRepositoryAndNotifyRepositoryState(2) {
            assertEquals(RepositoryState.ErrorUnknown(true), it)
        }
    }

    @Test
    fun `Repository should notify error unknown without when an error occurs retrieving first movie page`() {
        every { moviesDb.getMoviePageForSection(any(), any()) } returns null
        every { moviesApi.getNowPlayingMoviePage(any()) } returns null

        every { connectivityHandler.isConnectedToNetwork() } returns true

        executeMovieFetchingInRepositoryAndNotifyRepositoryState(1) {
            assertEquals(RepositoryState.ErrorUnknown(false), it)
        }
    }


    @Test
    fun `Repository should notify connectivity error with items when an error occurs retrieving movie page`() {
        every { moviesDb.getMoviePageForSection(any(), any()) } returns null
        every { moviesApi.getNowPlayingMoviePage(any()) } returns null

        every { connectivityHandler.isConnectedToNetwork() } returns false

        executeMovieFetchingInRepositoryAndNotifyRepositoryState(2) {
            assertEquals(RepositoryState.ErrorNoConnectivity(true), it)
        }
    }

    @Test
    fun `Repository should notify connectivity error without when an error occurs retrieving first movie page`() {
        every { moviesDb.getMoviePageForSection(any(), any()) } returns null
        every { moviesApi.getNowPlayingMoviePage(any()) } returns null

        every { connectivityHandler.isConnectedToNetwork() } returns false

        executeMovieFetchingInRepositoryAndNotifyRepositoryState(1) {
            assertEquals(RepositoryState.ErrorNoConnectivity(false), it)
        }
    }


    private fun executeMovieFetchingInRepository(page: Int, verification: () -> Unit) {
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
            dataSource.loadAfter(PageKeyedDataSource.LoadParams(page, 1), mockk(relaxed = true))
            verification.invoke()
        })

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    private fun executeMovieFetchingInRepositoryAndNotifyRepositoryState(page: Int, verification: (RepositoryState) -> Unit) {
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
            dataSource.loadAfter(PageKeyedDataSource.LoadParams(page, 1), mockk(relaxed = true))
        })

        listing.operationState.observe(lifecycleOwner, Observer {
            verification.invoke(it)
        })

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }
}