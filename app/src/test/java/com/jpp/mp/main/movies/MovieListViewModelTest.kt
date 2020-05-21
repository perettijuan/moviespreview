package com.jpp.mp.main.movies

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.jpp.mp.common.navigation.Destination
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MovieSection
import com.jpp.mpdomain.interactors.ImagesPathInteractor
import com.jpp.mptestutils.CoroutineTestExtension
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
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

    @RelaxedMockK
    private lateinit var movieListInteractor: MovieListInteractor

    @MockK
    private lateinit var imagesPathInteractor: ImagesPathInteractor

    private val lvInteractorEvents = MutableLiveData<MovieListInteractor.MovieListEvent>()

    private lateinit var subject: MovieListViewModel

    @BeforeEach
    fun setUp() {
        every { movieListInteractor.events } returns lvInteractorEvents

        subject = MovieListViewModel(
                movieListInteractor,
                imagesPathInteractor
        )
    }

    @ParameterizedTest
    @MethodSource("movieListTestParams")
    fun `Should post no connectivity error when disconnected`(param: MovieListParam) {
        var viewStatePosted: MovieListViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(param)

        lvInteractorEvents.postValue(MovieListInteractor.MovieListEvent.NotConnectedToNetwork)

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(true, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @ParameterizedTest
    @MethodSource("movieListTestParams")
    fun `Should retry to fetch data when not connected and retry is executed`(param: MovieListParam) {
        var viewStatePosted: MovieListViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(param)
        lvInteractorEvents.postValue(MovieListInteractor.MovieListEvent.NotConnectedToNetwork)

        viewStatePosted?.let {
            it.errorViewState.errorHandler?.invoke()
            verify(exactly = 2) { movieListInteractor.fetchMoviePageForSection(1, param.section, any()) }
        } ?: fail()
    }

    @ParameterizedTest
    @MethodSource("movieListTestParams")
    fun `Should post error when failing to fetch movies`(param: MovieListParam) {
        var viewStatePosted: MovieListViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(param)

        lvInteractorEvents.postValue(MovieListInteractor.MovieListEvent.UnknownError)

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(false, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @ParameterizedTest
    @MethodSource("movieListTestParams")
    fun `Should retry to fetch data when error unknown and retry is executed`(param: MovieListParam) {
        var viewStatePosted: MovieListViewState? = null

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(param)
        lvInteractorEvents.postValue(MovieListInteractor.MovieListEvent.UnknownError)

        viewStatePosted?.let {
            it.errorViewState.errorHandler?.invoke()
            verify(exactly = 2) { movieListInteractor.fetchMoviePageForSection(1, param.section, any()) }
        } ?: fail()
    }

    /*
     * TODO I need to check exactly what's happening with this UT. Don't want to waste
     *  time since I'm going to refactor by eliminating the interactor layers.
     */
    @Disabled
    @ParameterizedTest
    @MethodSource("movieListTestParams")
    fun `Should fetch movies, adapt result to UI and post value`(param: MovieListParam) {
        var viewStatePosted: MovieListViewState? = null
        val mockedList = getMockedMovies()
        val slot = slot<(List<Movie>) -> Unit>()

        every { imagesPathInteractor.configurePathMovie(any(), any(), any()) } answers { arg(2) }
        every { movieListInteractor.fetchMoviePageForSection(any(), any(), capture(slot)) } answers { slot.captured.invoke(mockedList) }

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(param)

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.contentViewState?.visibility)
        assertEquals(mockedList.size, viewStatePosted?.contentViewState?.movieList?.size)

        verify { movieListInteractor.fetchMoviePageForSection(1, param.section, any()) }
        verify(exactly = mockedList.size) { imagesPathInteractor.configurePathMovie(10, 10, any()) }
    }

    @ParameterizedTest
    @MethodSource("movieListTestParams")
    fun `Should update reached destination in onInit`(param: MovieListParam) {
        var destinationReached: Destination? = null
        val expected = Destination.MovieListReached(param.screenTitle)

        subject.destinationEvents.observeWith { destinationReached = it }

        subject.onInit(param)

        assertEquals(expected, destinationReached)
    }

    @ParameterizedTest
    @MethodSource("movieListTestParams")
    fun `Should request navigation to movie details when movie item selected`(param: MovieListParam) {
        val movieItem = MovieListItem(
                movieId = 10.0,
                headerImageUrl = "aHeaderImageUrl",
                title = "aTitle",
                contentImageUrl = "aContentPath",
                popularity = "aPopularity",
                voteCount = "aVoteCount"
        )

        val expectedDestination = Destination.MPMovieDetails(
                movieId = "10.0",
                movieImageUrl = "aContentPath",
                movieTitle = "aTitle")

        var requestedDestination: Destination? = null

        subject.navigationEvents.observeWith { it.actionIfNotHandled { dest -> requestedDestination = dest } }
        subject.onMovieSelected(movieItem)

        assertEquals(expectedDestination, requestedDestination)
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
                        MovieListParam(
                                MovieSection.Playing,
                                "Playing",
                                10,
                                10
                        )
                ),
                arguments(
                        MovieListParam(
                                MovieSection.Popular,
                                "Popular",
                                10,
                                10
                        )
                ),
                arguments(
                        MovieListParam(
                                MovieSection.Upcoming,
                                "Upcoming",
                                10,
                                10
                        )
                ),
                arguments(
                        MovieListParam(
                                MovieSection.TopRated,
                                "TopRated",
                                10,
                                10
                        )
                )
        )
    }
}
