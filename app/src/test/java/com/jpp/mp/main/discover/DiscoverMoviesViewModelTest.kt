package com.jpp.mp.main.discover

import android.view.View
import androidx.lifecycle.SavedStateHandle
import com.jpp.mp.R
import com.jpp.mp.main.discover.filters.genres.GenreFilterItem
import com.jpp.mpdesign.mapped.MovieGenreItem
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MovieGenre
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.usecase.GetAllMovieGenresUseCase
import com.jpp.mpdomain.usecase.GetDiscoveredMoviePageUseCase
import com.jpp.mpdomain.usecase.Try
import com.jpp.mptestutils.CoroutineTestExtension
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(
    MockKExtension::class,
    InstantTaskExecutorExtension::class,
    CoroutineTestExtension::class
)
class DiscoverMoviesViewModelTest {

    @RelaxedMockK
    private lateinit var getDiscoveredMoviePageUseCase: GetDiscoveredMoviePageUseCase

    @RelaxedMockK
    private lateinit var getAllMovieGenresUseCase: GetAllMovieGenresUseCase

    @RelaxedMockK
    private lateinit var navigator: DiscoverMoviesNavigator

    private lateinit var subject: DiscoverMoviesViewModel


    @BeforeEach
    fun setUp() {
        subject = DiscoverMoviesViewModel(
            getDiscoveredMoviePageUseCase,
            getAllMovieGenresUseCase,
            navigator,
            CoroutineTestExtension.testDispatcher,
            SavedStateHandle()
        )
    }

    @Test
    fun `Should post no connectivity error when disconnected`() {
        var viewStatePosted: DiscoverMoviesViewState? = null

        coEvery {
            getDiscoveredMoviePageUseCase.execute(
                any(),
                any()
            )
        } returns Try.Failure(Try.FailureCause.NoConnectivity)

        subject.onInit()
        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        assertEquals(R.string.main_menu_discover, viewStatePosted?.screenTitle)

        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertTrue(viewStatePosted?.errorViewState?.isConnectivity ?: fail())
    }

    @Test
    fun `Should retry to fetch data when not connected and retry is executed`() {
        var viewStatePosted: DiscoverMoviesViewState? = null

        coEvery {
            getDiscoveredMoviePageUseCase.execute(
                any(),
                any()
            )
        } returns Try.Failure(Try.FailureCause.NoConnectivity)

        subject.onInit()
        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        viewStatePosted?.let {
            it.errorViewState.errorHandler?.invoke()
            coVerify(exactly = 2) { getDiscoveredMoviePageUseCase.execute(1, any()) }
        } ?: fail()
    }

    @Test
    fun `Should post error when failing to discover movies`() {
        var viewStatePosted: DiscoverMoviesViewState? = null

        coEvery {
            getDiscoveredMoviePageUseCase.execute(
                any(),
                any()
            )
        } returns Try.Failure(Try.FailureCause.Unknown)

        subject.onInit()
        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        assertEquals(R.string.main_menu_discover, viewStatePosted?.screenTitle)

        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertFalse(viewStatePosted?.errorViewState?.isConnectivity ?: fail())
    }

    @Test
    fun `Should retry to fetch data when error unknown and retry is executed`() {
        var viewStatePosted: DiscoverMoviesViewState? = null

        coEvery {
            getDiscoveredMoviePageUseCase.execute(
                any(),
                any()
            )
        } returns Try.Failure(Try.FailureCause.Unknown)

        subject.onInit()
        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        viewStatePosted?.let {
            it.errorViewState.errorHandler?.invoke()
            coVerify(exactly = 2) { getDiscoveredMoviePageUseCase.execute(1, any()) }
        } ?: fail()
    }

    @Test
    fun `Should discover movies, adapt to UI and show result along with genres`() {
        var viewStatePosted: DiscoverMoviesViewState? = null
        var filterViewStatePosted: DiscoverMoviesFiltersViewState? = null
        val mockedList = MOCKED_MOVIES
        val moviePage = MoviePage(
            page = 1,
            results = mockedList,
            total_pages = 100,
            total_results = 2000
        )

        coEvery {
            getDiscoveredMoviePageUseCase.execute(
                any(),
                any()
            )
        } returns Try.Success(moviePage)

        coEvery { getAllMovieGenresUseCase.execute() } returns Try.Success(MOCKED_GENRES)


        subject.onInit()
        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.filterViewState.observeWith { state -> filterViewStatePosted = state }


        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.VISIBLE, viewStatePosted?.contentViewState?.visibility)
        assertEquals(EXPECTED_UI_ITEMS, viewStatePosted?.contentViewState?.itemList)


        assertEquals(View.VISIBLE, filterViewStatePosted?.visibility)
        assertFalse(filterViewStatePosted?.isExpanded ?: fail())
        assertEquals(R.string.discover_movies_filters, filterViewStatePosted?.discoverTitle)
        assertEquals(
            R.string.discover_movies_genres_filter_title,
            filterViewStatePosted?.genreTitle
        )
        assertEquals(EXPECTED_GENRES, filterViewStatePosted?.genreList)
    }

    @Test
    fun `Should discover movies, adapt to UI and show result if genres fail`() {
        var viewStatePosted: DiscoverMoviesViewState? = null
        var filterViewStatePosted: DiscoverMoviesFiltersViewState? = null
        val mockedList = MOCKED_MOVIES
        val moviePage = MoviePage(
            page = 1,
            results = mockedList,
            total_pages = 100,
            total_results = 2000
        )

        coEvery {
            getDiscoveredMoviePageUseCase.execute(
                any(),
                any()
            )
        } returns Try.Success(moviePage)

        coEvery { getAllMovieGenresUseCase.execute() } returns Try.Failure(Try.FailureCause.Unknown)


        subject.onInit()
        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.filterViewState.observeWith { state -> filterViewStatePosted = state }


        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.VISIBLE, viewStatePosted?.contentViewState?.visibility)
        assertEquals(EXPECTED_UI_ITEMS, viewStatePosted?.contentViewState?.itemList)


        assertEquals(View.GONE, filterViewStatePosted?.visibility)
    }

    @Test
    fun `Should request nex movie page and avoid to refresh genres`() {
        val mockedList = MOCKED_MOVIES
        val moviePage = MoviePage(
            page = 1,
            results = mockedList,
            total_pages = 100,
            total_results = 2000
        )

        coEvery {
            getDiscoveredMoviePageUseCase.execute(
                any(),
                any()
            )
        } returns Try.Success(moviePage)

        coEvery { getAllMovieGenresUseCase.execute() } returns Try.Success(MOCKED_GENRES)

        // pre-condition
        subject.onInit()

        subject.onNextMoviePage()

        coVerify {
            getDiscoveredMoviePageUseCase.execute(
                page = 1, genres = any()
            )
        }

        coVerify {
            getDiscoveredMoviePageUseCase.execute(
                page = 2, genres = any()
            )
        }

        coVerify(exactly = 1) {getAllMovieGenresUseCase.execute() }
    }

    @Test
    fun `Should handle rotation properly`() {
        var viewStatePosted: DiscoverMoviesViewState? = null
        var filterViewStatePosted: DiscoverMoviesFiltersViewState? = null
        val mockedList = MOCKED_MOVIES
        val moviePage = MoviePage(
            page = 1,
            results = mockedList,
            total_pages = 100,
            total_results = 2000
        )

        coEvery {
            getDiscoveredMoviePageUseCase.execute(
                any(),
                any()
            )
        } returns Try.Success(moviePage)

        coEvery { getAllMovieGenresUseCase.execute() } returns Try.Success(MOCKED_GENRES)


        subject.onInit()
        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.filterViewState.observeWith { state -> filterViewStatePosted = state }

        // 2nd call to onInit simulates rotation
        subject.onInit()

        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.VISIBLE, viewStatePosted?.contentViewState?.visibility)


        assertEquals(View.VISIBLE, filterViewStatePosted?.visibility)
        assertFalse(filterViewStatePosted?.isExpanded ?: fail())
        assertEquals(R.string.discover_movies_filters, filterViewStatePosted?.discoverTitle)
        assertEquals(
            R.string.discover_movies_genres_filter_title,
            filterViewStatePosted?.genreTitle
        )
    }

    companion object {
        private val MOCKED_MOVIES = mutableListOf<Movie>().apply {
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
        private val EXPECTED_UI_ITEMS = mutableListOf<DiscoveredMovieListItem>().apply {
            for (i in 0..50) {
                add(
                    DiscoveredMovieListItem(
                        movieId = i.toDouble(),
                        headerImageUrl = "backdropPath$i",
                        contentImageUrl = "posterPath$i",
                        title = "titleRes$i",
                        popularity = i.toFloat().toString(),
                        voteCount = i.toDouble().toString()
                    )
                )
            }
        }


        private val MOCKED_GENRES = listOf(
            MovieGenre(id = MovieGenre.ACTION_GENRE_ID, name = "Action"),
            MovieGenre(id = MovieGenre.ADVENTURE_GENRE_ID, name = "Adventure"),
            MovieGenre(id = MovieGenre.ANIMATION_GENRE_ID, name = "Animation"),
            MovieGenre(id = MovieGenre.COMEDY_GENRE_ID, name = "Comedy"),
            MovieGenre(id = MovieGenre.CRIME_GENRE_ID, name = "Crime"),
            MovieGenre(id = MovieGenre.DOCUMENTARY_GENRE_ID, name = "Documentary"),
            MovieGenre(id = MovieGenre.WESTERN_GENRE_ID, name = "Western")
        )

        private val EXPECTED_GENRES = listOf(
            GenreFilterItem(
                genreId = MovieGenre.ACTION_GENRE_ID,
                uiGenre = MovieGenreItem.Action,
                isSelected = false
            ),
            GenreFilterItem(
                genreId = MovieGenre.ADVENTURE_GENRE_ID,
                uiGenre = MovieGenreItem.Adventure,
                isSelected = false
            ),
            GenreFilterItem(
                genreId = MovieGenre.ANIMATION_GENRE_ID,
                uiGenre = MovieGenreItem.Animation,
                isSelected = false
            ),
            GenreFilterItem(
                genreId = MovieGenre.COMEDY_GENRE_ID,
                uiGenre = MovieGenreItem.Comedy,
                isSelected = false
            ),
            GenreFilterItem(
                genreId = MovieGenre.CRIME_GENRE_ID,
                uiGenre = MovieGenreItem.Crime,
                isSelected = false
            ),
            GenreFilterItem(
                genreId = MovieGenre.DOCUMENTARY_GENRE_ID,
                uiGenre = MovieGenreItem.Documentary,
                isSelected = false
            ),
            GenreFilterItem(
                genreId = MovieGenre.WESTERN_GENRE_ID,
                uiGenre = MovieGenreItem.Western,
                isSelected = false
            )
        )
    }
}