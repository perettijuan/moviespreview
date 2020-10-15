package com.jpp.mp.main.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jpp.mp.common.livedata.HandledEvent
import com.jpp.mp.common.livedata.HandledEvent.Companion.of
import com.jpp.mp.main.discover.filters.genres.GenreFilterItem
import com.jpp.mpdesign.mapped.MovieGenreItem
import com.jpp.mpdomain.Movie
import com.jpp.mpdomain.MovieGenre
import com.jpp.mpdomain.MoviePage
import com.jpp.mpdomain.usecase.GetAllMovieGenresUseCase
import com.jpp.mpdomain.usecase.GetDiscoveredMoviePageUseCase
import com.jpp.mpdomain.usecase.Try
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [ViewModel] that supports the logic needed for the discover movies section.
 * Produces two different view states, each one mapping the state of the whole view:
 * [DiscoverMoviesViewState] represents the state of the section that shows the list of movies
 * being discovered.
 * [DiscoverMoviesFiltersViewState] represents the state of the section that shows the filters
 * to select and apply.
 */
class DiscoverMoviesViewModel(
    private val getDiscoveredMoviePageUseCase: GetDiscoveredMoviePageUseCase,
    private val getAllMovieGenresUseCase: GetAllMovieGenresUseCase,
    private val navigator: DiscoverMoviesNavigator,
    private val ioDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    /*
     * Not stored in SavedStateHandle b/c we want to recreate
     * the whole paging when app killed.
     */
    private var currentPage: Int = 0

    private var maxPage: Int
        set(value) = savedStateHandle.set(MAX_MOVIE_PAGE_KEY, value)
        get() = savedStateHandle.get(MAX_MOVIE_PAGE_KEY) ?: 0

    private val _viewState = MutableLiveData<DiscoverMoviesViewState>()
    val viewState: LiveData<DiscoverMoviesViewState> = _viewState

    private val _filtersViewState = MutableLiveData<DiscoverMoviesFiltersViewState>()
    val filterViewState: LiveData<DiscoverMoviesFiltersViewState> = _filtersViewState

    private val _events = MutableLiveData<HandledEvent<DiscoverMovieClearResultsEvent>>()
    val events: LiveData<HandledEvent<DiscoverMovieClearResultsEvent>> = _events

    /**
     * Called on VM initialization. The View (Fragment) calls this method to
     * indicate that the view is ready to render any view states. When the method
     * is called, the VM verifies the application's internal state and updates
     * the view state based on that state.
     */
    fun onInit() {
        if (_viewState.value == null) {
            _viewState.value = DiscoverMoviesViewState.showLoading()
            _filtersViewState.value = DiscoverMoviesFiltersViewState.showLoading()
            fetchMoviePage(FIRST_PAGE)
        }
    }

    /**
     * Called when the next movie page should be retrieved.
     */
    fun onNextMoviePage() {
        val nextPage = currentPage + 1
        fetchMoviePage(nextPage)
    }

    /**
     * Called when an item is selected in the list of movies.
     */
    fun onMovieSelected(item: DiscoveredMovieListItem) {
        navigator.navigateToMovieDetails(
            item.movieId.toString(),
            item.contentImageUrl,
            item.title
        )
    }

    /**
     * Called when the expand/collapse section is clicked in the filters
     * section.
     */
    fun onFilterExpandClicked(isExpanded: Boolean) {
        _filtersViewState.value = _filtersViewState.value?.copy(isExpanded = !isExpanded)
    }

    /**
     * Called when an item has been selected in the list of [GenreFilterItem].
     */
    fun onGenreFilterItemSelected(genreFilterItem: GenreFilterItem) {
        _filtersViewState.value = _filtersViewState.value?.updateSelectedState(genreFilterItem)
    }

    /**
     * Called when the selected filters are applied.
     */
    fun onApplyFiltersSelected() {
        _filtersViewState.value = _filtersViewState.value?.updateToLoading()

        // reset state
        _events.value = of(DiscoverMovieClearResultsEvent)
        _viewState.value = DiscoverMoviesViewState.showLoading()
        currentPage = FIRST_PAGE

        fetchMoviePage(currentPage)
    }

    /**
     * Called when the clear all filters option is selected.
     */
    fun onClearAllFiltersSelected() {
        _filtersViewState.value = _filtersViewState.value?.updateToClearingAllFilters()

        // reset state
        _events.value = of(DiscoverMovieClearResultsEvent)
        _viewState.value = DiscoverMoviesViewState.showLoading()
        currentPage = FIRST_PAGE

        fetchMoviePage(currentPage)
    }

    private fun fetchMoviePage(page: Int) {
        // page is higher (or lower) than maxPage
        if (maxPage in 1..page) return

        val genreList =
            _filtersViewState.value?.genreList
                ?.filter { uiGenre -> uiGenre.isSelected }
                ?.map { uiGenre -> uiGenre.mapToMovieGenre() }

        viewModelScope.launch {
            val result = withContext(ioDispatcher) {
                getDiscoveredMoviePageUseCase.execute(page, genreList)
            }
            when (result) {
                is Try.Success -> {
                    fetchFiltersIfNeeded() // fetch filters only if the discover has been successful
                    processMoviePage(result.value)
                }
                is Try.Failure -> processFailure(result.cause)
            }
        }
    }

    private fun fetchFiltersIfNeeded() {
        if (_filtersViewState.value?.genreList?.isNotEmpty() == true) {
            // avoid fetching filters if they are already present
            _filtersViewState.value = _filtersViewState.value?.refreshAfterLoading()
            return
        }

        viewModelScope.launch {
            val genresResult = withContext(ioDispatcher) {
                getAllMovieGenresUseCase.execute()
            }

            if (genresResult is Try.Success) {
                _filtersViewState.value =
                    _filtersViewState.value?.showVisible(genresResult.value.map { domainGenre -> domainGenre.mapToGenreItem() })
            }
        }
    }

    private fun processMoviePage(moviePage: MoviePage) {
        currentPage = moviePage.page
        maxPage = moviePage.total_pages
        viewModelScope.launch {
            val movieList = withContext(ioDispatcher) {
                moviePage.results
                    .map { movie -> movie.toDiscoveredItem() }
            }
            _viewState.value = _viewState.value?.showMovieList(movieList)
        }
    }

    private fun processFailure(failure: Try.FailureCause) {
        _viewState.value = when (failure) {
            is Try.FailureCause.NoConnectivity -> _viewState.value?.showNoConnectivityError { onNextMoviePage() }
            else -> _viewState.value?.showUnknownError { onNextMoviePage() }
        }
    }

    private fun Movie.toDiscoveredItem() =
        DiscoveredMovieListItem(
            movieId = id,
            headerImageUrl = backdrop_path ?: "emptyPath",
            title = title,
            contentImageUrl = poster_path ?: "emptyPath",
            popularity = popularity.toString(),
            voteCount = vote_count.toString()
        )

    private fun MovieGenre.mapToGenreItem(): GenreFilterItem {
        return when (id) {
            MovieGenre.ACTION_GENRE_ID -> MovieGenreItem.Action
            MovieGenre.ADVENTURE_GENRE_ID -> MovieGenreItem.Adventure
            MovieGenre.ANIMATION_GENRE_ID -> MovieGenreItem.Animation
            MovieGenre.COMEDY_GENRE_ID -> MovieGenreItem.Comedy
            MovieGenre.CRIME_GENRE_ID -> MovieGenreItem.Crime
            MovieGenre.DOCUMENTARY_GENRE_ID -> MovieGenreItem.Documentary
            MovieGenre.DRAMA_GENRE_ID -> MovieGenreItem.Drama
            MovieGenre.FAMILY_GENRE_ID -> MovieGenreItem.Family
            MovieGenre.FANTASY_GENRE_ID -> MovieGenreItem.Fantasy
            MovieGenre.HISTORY_GENRE_ID -> MovieGenreItem.History
            MovieGenre.HORROR_GENRE_ID -> MovieGenreItem.Horror
            MovieGenre.MUSIC_GENRE_ID -> MovieGenreItem.Music
            MovieGenre.MYSTERY_GENRE_ID -> MovieGenreItem.Mystery
            MovieGenre.SCI_FY_GENRE_ID -> MovieGenreItem.SciFi
            MovieGenre.TV_MOVIE_GENRE_ID -> MovieGenreItem.TvMovie
            MovieGenre.THRILLER_GENRE_ID -> MovieGenreItem.Thriller
            MovieGenre.WAR_GENRE_ID -> MovieGenreItem.War
            MovieGenre.WESTERN_GENRE_ID -> MovieGenreItem.Western
            else -> MovieGenreItem.Generic
        }.let { uiGenre -> GenreFilterItem(id, uiGenre, isSelected = false) }
    }

    private fun GenreFilterItem.mapToMovieGenre(): MovieGenre {
        return MovieGenre(genreId, uiGenre.name)
    }

    private companion object {
        const val MAX_MOVIE_PAGE_KEY = "MAX_MOVIE_PAGE_KEY"
        const val FIRST_PAGE = 1
    }
}
