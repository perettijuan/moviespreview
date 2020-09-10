package com.jpp.mp.main.movies

import android.view.View
import androidx.lifecycle.SavedStateHandle
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.usecase.GetMoviePageUseCase
import com.jpp.mpdomain.usecase.Try
import com.jpp.mptestutils.CoroutineTestExtension
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.fail
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
class MovieListViewModelTest {

    @MockK
    private lateinit var getMoviePageUseCase: GetMoviePageUseCase

    @RelaxedMockK
    private lateinit var navigator: MovieListNavigator

    private lateinit var subject: MovieListViewModel

    @BeforeEach
    fun setUp() {
        subject = MovieListViewModel(
                getMoviePageUseCase,
                navigator,
                CoroutineTestExtension.testDispatcher,
                SavedStateHandle()
        )
    }

    @ParameterizedTest
    @MethodSource("movieListTestParams")
    fun `Should post no connectivity error when disconnected`(section: MovieSection, screenTitle: String) {
        var viewStatePosted: MovieListViewState? = null

        coEvery { getMoviePageUseCase.execute(any(), section) } returns Try.Failure(Try.FailureCause.NoConnectivity)

        subject.onInit(section, screenTitle)
        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        assertEquals(screenTitle, viewStatePosted?.screenTitle)

        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(true, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @ParameterizedTest
    @MethodSource("movieListTestParams")
    fun `Should retry to fetch data when not connected and retry is executed`(section: MovieSection, screenTitle: String) {
        var viewStatePosted: MovieListViewState? = null

        coEvery { getMoviePageUseCase.execute(any(), section) } returns Try.Failure(Try.FailureCause.NoConnectivity)

        subject.onInit(section, screenTitle)
        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        viewStatePosted?.let {
            it.errorViewState.errorHandler?.invoke()
            coVerify(exactly = 2) { getMoviePageUseCase.execute(1, section) }
        } ?: fail()
    }

    @ParameterizedTest
    @MethodSource("movieListTestParams")
    fun `Should post error when failing to fetch movies`(section: MovieSection, screenTitle: String) {
        var viewStatePosted: MovieListViewState? = null

        coEvery { getMoviePageUseCase.execute(any(), section) } returns Try.Failure(Try.FailureCause.Unknown)

        subject.onInit(section, screenTitle)
        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        assertNotNull(viewStatePosted)

        assertEquals(screenTitle, viewStatePosted?.screenTitle)

        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(false, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @ParameterizedTest
    @MethodSource("movieListTestParams")
    fun `Should retry to fetch data when error unknown and retry is executed`(section: MovieSection, screenTitle: String) {
        var viewStatePosted: MovieListViewState? = null

        coEvery { getMoviePageUseCase.execute(any(), section) } returns Try.Failure(Try.FailureCause.Unknown)

        subject.onInit(section, screenTitle)
        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        viewStatePosted?.let {
            it.errorViewState.errorHandler?.invoke()
            coVerify(exactly = 2) { getMoviePageUseCase.execute(1, section) }
        } ?: fail()
    }

    @Test
    fun `Should fetch movies, adapt result to UI and post value`() {
        val screenTitle = "aTitle"
        val section = MovieSection.Playing
        var viewStatePosted: MovieListViewState? = null
        val mockedList = getMockedMovies()
        val moviePage = MoviePage(
                page = 1,
                results = mockedList,
                total_pages = 100,
                total_results = 2000
        )

        coEvery { getMoviePageUseCase.execute(any(), section) } returns Try.Success(moviePage)

        subject.onInit(section, screenTitle)
        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        assertNotNull(viewStatePosted)

        assertEquals(screenTitle, viewStatePosted?.screenTitle)

        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.contentViewState?.visibility)
        assertEquals(mockedList.size, viewStatePosted?.contentViewState?.movieList?.size)
    }

    @ParameterizedTest
    @MethodSource("movieListTestParams")
    fun `Should request navigation to movie details when movie item selected`(section: MovieSection, screenTitle: String) {
        val movieItem = MovieListItem(
                movieId = 10.0,
                headerImageUrl = "aHeaderImageUrl",
                title = "aTitle",
                contentImageUrl = "aContentPath",
                popularity = "aPopularity",
                voteCount = "aVoteCount"
        )
        subject.onMovieSelected(movieItem)

        verify {
            navigator.navigateToMovieDetails(
                    movieId = "10.0",
                    movieImageUrl = "aContentPath",
                    movieTitle = "aTitle"
            )
        }
    }

    private fun getMockedMovies(): List<Movie> {
        return mutableListOf<Movie>().apply {
            for (i in 0..50) {
                add(
                        Movie(
                                id = i.toDouble(),
                                title = "titleRes$i",
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

    companion object {

        @JvmStatic
        fun movieListTestParams() = listOf(
                arguments(
                        MovieSection.Playing,
                        "Playing"
                ),
                arguments(
                        MovieSection.Popular,
                        "Popular"
                ),
                arguments(
                        MovieSection.Upcoming,
                        "Upcoming"
                ),
                arguments(
                        MovieSection.TopRated,
                        "TopRated"
                )
        )
    }
}
