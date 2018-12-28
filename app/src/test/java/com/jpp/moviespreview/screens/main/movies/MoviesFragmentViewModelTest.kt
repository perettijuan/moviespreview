package com.jpp.moviespreview.screens.main.movies

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.jpp.moviespreview.domainlayer.Movie
import com.jpp.moviespreview.domainlayer.MovieSection
import com.jpp.moviespreview.domainlayer.ds.movie.MoviesDataSourceState
import com.jpp.moviespreview.domainlayer.repository.movies.MoviesPagingDataSourceFactory
import com.jpp.moviespreview.screens.main.utiltest.InstantTaskExecutorExtension
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

@ExtendWith(MockKExtension::class, InstantTaskExecutorExtension::class)
class MoviesFragmentViewModelTest {

    @MockK
    lateinit var pagingDataSourceFactory: MoviesPagingDataSourceFactory
    @MockK
    lateinit var mapper: MovieItemMapper

    lateinit var subject: MoviesFragmentViewModel

    private val movieSection: UiMovieSection = UiMovieSection.Playing
    private val moviePosterSize: Int = 200
    private val movieBackdropSize: Int = 100

    @BeforeEach
    fun setUp() {
        subject = MoviesFragmentViewModel(pagingDataSourceFactory, mapper)
    }


    @Test
    fun `Should request a new list to the DS Factory when a new movie list is required`() {
        val domainSection = MovieSection.Playing
        val dsFactoryLiveData: LiveData<MoviesDataSourceState> = mockk()
        val expected: LiveData<PagedList<MovieItem>> = mockk()

        every { mapper.mapMovieSection(movieSection) } returns domainSection
        every { pagingDataSourceFactory.getMovieList(domainSection, movieBackdropSize, moviePosterSize, any<(Movie) -> MovieItem>()) } returns expected
        every { pagingDataSourceFactory.dataSourceLiveData } returns dsFactoryLiveData

        val actual = subject.getMovieList(movieSection, moviePosterSize, movieBackdropSize)

        assertEquals(actual, expected)
    }

    @Test
    fun `Should use the same PagedList if one already created and it is the same section`() {
        val domainSection = MovieSection.Playing
        val dsFactoryLiveData: LiveData<MoviesDataSourceState> = mockk()
        val expected: LiveData<PagedList<MovieItem>> = mockk()

        every { mapper.mapMovieSection(movieSection) } returns domainSection
        every { pagingDataSourceFactory.getMovieList(domainSection, movieBackdropSize, moviePosterSize, any<(Movie) -> MovieItem>()) } returns expected
        every { pagingDataSourceFactory.dataSourceLiveData } returns dsFactoryLiveData

        //1st call
        subject.getMovieList(movieSection, moviePosterSize, movieBackdropSize)
        //2nd call -> i.e. rotation
        val actual = subject.getMovieList(movieSection, moviePosterSize, movieBackdropSize)

        assertEquals(actual, expected)
        verify(exactly = 1) { pagingDataSourceFactory.getMovieList(domainSection, movieBackdropSize, moviePosterSize, any<(Movie) -> MovieItem>()) }
    }

    @Test
    fun `Should use a new PagedList if one already created but section changes`() {
        val domainSection = MovieSection.Playing
        val dsFactoryLiveData: LiveData<MoviesDataSourceState> = mockk()
        val expected: LiveData<PagedList<MovieItem>> = mockk()

        every { mapper.mapMovieSection(movieSection) } returns domainSection
        every { mapper.mapMovieSection(UiMovieSection.Upcoming) } returns MovieSection.Upcoming
        every { pagingDataSourceFactory.getMovieList(domainSection, movieBackdropSize, moviePosterSize, any<(Movie) -> MovieItem>()) } returns mockk()
        every { pagingDataSourceFactory.getMovieList(MovieSection.Upcoming, movieBackdropSize, moviePosterSize, any<(Movie) -> MovieItem>()) } returns expected
        every { pagingDataSourceFactory.dataSourceLiveData } returns dsFactoryLiveData

        //1st call
        subject.getMovieList(movieSection, moviePosterSize, movieBackdropSize)
        //2nd call -> changes section
        val actual = subject.getMovieList(UiMovieSection.Upcoming, moviePosterSize, movieBackdropSize)

        assertEquals(actual, expected)
        // 1st call
        verify(exactly = 1) { pagingDataSourceFactory.getMovieList(domainSection, movieBackdropSize, moviePosterSize, any<(Movie) -> MovieItem>()) }
        verify(exactly = 1) { pagingDataSourceFactory.getMovieList(MovieSection.Upcoming, movieBackdropSize, moviePosterSize, any<(Movie) -> MovieItem>()) }
    }

    @ParameterizedTest
    @MethodSource("executeParameters")
    fun `Should map and publish DS inner state as fragment view state`(stateToPost: MoviesDataSourceState, stateToVerify: MoviesFragmentViewState) {
        val lifecycleOwner: LifecycleOwner = mockk()
        val lifecycle = LifecycleRegistry(lifecycleOwner)

        val dsFactoryLiveData: MutableLiveData<MoviesDataSourceState> = MutableLiveData()

        every { mapper.mapMovieSection(movieSection) } returns MovieSection.Playing
        every { pagingDataSourceFactory.getMovieList(MovieSection.Playing, movieBackdropSize, moviePosterSize, any<(Movie) -> MovieItem>()) } returns mockk()
        every { pagingDataSourceFactory.dataSourceLiveData } returns dsFactoryLiveData
        every { lifecycleOwner.lifecycle } returns lifecycle


        subject.getMovieList(movieSection, moviePosterSize, movieBackdropSize)

        subject.bindViewState().observe(lifecycleOwner, Observer {
            assertEquals(stateToVerify, it)
        })

        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        dsFactoryLiveData.value = stateToPost
    }


    companion object {

        @JvmStatic
        fun executeParameters() = listOf(
                arguments(MoviesDataSourceState.LoadingInitial, MoviesFragmentViewState.Loading),
                arguments(MoviesDataSourceState.ErrorUnknown, MoviesFragmentViewState.ErrorUnknown),
                arguments(MoviesDataSourceState.ErrorNoConnectivity, MoviesFragmentViewState.ErrorNoConnectivity),
                arguments(MoviesDataSourceState.LoadingInitialDone, MoviesFragmentViewState.InitialPageLoaded)
        )
    }
}