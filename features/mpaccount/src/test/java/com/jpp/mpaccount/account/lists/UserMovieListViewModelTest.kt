package com.jpp.mpaccount.account.lists

import android.view.View
import androidx.lifecycle.SavedStateHandle
import com.jpp.mpdomain.AccountMovieType
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.usecase.GetUserAccountMoviePageUseCase
import com.jpp.mpdomain.usecase.Try
import com.jpp.mptestutils.CoroutineTestExtension
import com.jpp.mptestutils.InstantTaskExecutorExtension
import com.jpp.mptestutils.observeWith
import io.mockk.coEvery
import io.mockk.coVerify
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
class UserMovieListViewModelTest {

    @RelaxedMockK
    private lateinit var getMoviesUseCase: GetUserAccountMoviePageUseCase

    @RelaxedMockK
    private lateinit var navigator: UserMovieListNavigator

    private lateinit var subject: UserMovieListViewModel

    @BeforeEach
    fun setUp() {
        subject = UserMovieListViewModel(
            getMoviesUseCase,
            navigator,
            CoroutineTestExtension.testDispatcher,
            SavedStateHandle()
        )
    }

    @ParameterizedTest
    @MethodSource("userMovieListTestParams")
    fun `Should post no connectivity error when disconnected`(
        uiType: UserMovieListType,
        domainType: AccountMovieType
    ) {
        var viewStatePosted: UserMovieListViewState? = null

        coEvery {
            getMoviesUseCase.execute(
                any(),
                domainType
            )
        } returns Try.Failure(Try.FailureCause.NoConnectivity)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(uiType)

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(true, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @ParameterizedTest
    @MethodSource("userMovieListTestParams")
    fun `Should retry to fetch data when not connected and retry is executed`(
        uiType: UserMovieListType,
        domainType: AccountMovieType
    ) {
        var viewStatePosted: UserMovieListViewState? = null

        coEvery {
            getMoviesUseCase.execute(
                any(),
                domainType
            )
        } returns Try.Failure(Try.FailureCause.NoConnectivity)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(uiType)

        viewStatePosted?.let {
            it.errorViewState.errorHandler?.invoke()
            coVerify(exactly = 2) { getMoviesUseCase.execute(1, domainType) }
        } ?: fail()
    }

    @ParameterizedTest
    @MethodSource("userMovieListTestParams")
    fun `Should post unknown error`(
        uiType: UserMovieListType,
        domainType: AccountMovieType
    ) {
        var viewStatePosted: UserMovieListViewState? = null

        coEvery {
            getMoviesUseCase.execute(
                any(),
                domainType
            )
        } returns Try.Failure(Try.FailureCause.Unknown)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }
        subject.onInit(uiType)

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.contentViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.errorViewState?.visibility)
        assertEquals(false, viewStatePosted?.errorViewState?.isConnectivity)
    }

    @ParameterizedTest
    @MethodSource("userMovieListTestParams")
    fun `Should retry to fetch data when error unknown and retry is executed`(
        uiType: UserMovieListType,
        domainType: AccountMovieType
    ) {
        var viewStatePosted: UserMovieListViewState? = null

        coEvery {
            getMoviesUseCase.execute(
                any(),
                domainType
            )
        } returns Try.Failure(Try.FailureCause.Unknown)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(uiType)

        viewStatePosted?.let {
            it.errorViewState.errorHandler?.invoke()
            coVerify(exactly = 2) { getMoviesUseCase.execute(1, domainType) }
        } ?: fail()
    }

    @ParameterizedTest
    @MethodSource("userMovieListTestParams")
    fun `Should fetch movies, adapt result to UI and post value`(
        uiType: UserMovieListType,
        domainType: AccountMovieType
    ) {
        var viewStatePosted: UserMovieListViewState? = null
        val mockedList = getMockedMovies()
        val moviePage = MoviePage(
            page = 1,
            results = mockedList,
            total_results = 10,
            total_pages = 500
        )

        coEvery { getMoviesUseCase.execute(1, domainType) } returns Try.Success(moviePage)

        subject.viewState.observeWith { viewState -> viewStatePosted = viewState }

        subject.onInit(uiType)

        assertNotNull(viewStatePosted)
        assertEquals(View.INVISIBLE, viewStatePosted?.loadingVisibility)
        assertEquals(View.INVISIBLE, viewStatePosted?.errorViewState?.visibility)

        assertEquals(View.VISIBLE, viewStatePosted?.contentViewState?.visibility)
        assertEquals(mockedList.size, viewStatePosted?.contentViewState?.movieList?.size)
    }

    @Test
    fun `Should request navigation to movie details when movie item selected`() {
        val movieItem = UserMovieItem(
            movieId = 10.0,
            headerImageUrl = "aHeaderImageUrl",
            title = "aTitle",
            contentImageUrl = "aContentPath"
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
        fun userMovieListTestParams() = listOf(
            arguments(UserMovieListType.FAVORITE_LIST, AccountMovieType.Favorite),
            arguments(UserMovieListType.RATED_LIST, AccountMovieType.Rated),
            arguments(UserMovieListType.WATCH_LIST, AccountMovieType.Watchlist)
        )
    }
}
