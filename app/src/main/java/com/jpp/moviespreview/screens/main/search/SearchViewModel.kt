package com.jpp.moviespreview.screens.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.repository.search.SearchRepository
import com.jpp.mpdomain.repository.search.SearchRepositoryState
import javax.inject.Inject

/**
 * [ViewModel] to provide search functionality.
 *
 * This is a very special ViewModel in the application, since it doesn't follows the pattern
 * defined in MPScopedViewModel. This is because this section of the application is using the
 * Paging Library to support unlimited scrolling and the [SearchRepository] is using its own
 * Executor to defer long term operations to another thread (instead of coroutines provided by
 * the MPScopedViewModel).
 */
class SearchViewModel @Inject constructor(private val repository: SearchRepository) : ViewModel() {

    //TODO JPP add retry
    lateinit var viewState: LiveData<SearchViewState>
    lateinit var pagedList: LiveData<PagedList<SearchResultItem>>
    private var targetImageSize: Int = -1
    private var currentSearch = EMPTY_SEARCH


    fun init(imageSize: Int) {
        targetImageSize = imageSize
    }


    fun search(searchText: String) {
        if (searchText == currentSearch) {
            return
        }

        currentSearch = searchText

        repository.search(currentSearch, targetImageSize) { domainSearchResult ->
            with(domainSearchResult) {
                SearchResultItem(
                        id = id,
                        imagePath = extractImagePathFromSearchResult(this),
                        name = extractTitleFromSearchResult(this),
                        icon = getIconForSearchResult(this)
                )
            }
        }.let { listing ->
            viewState = Transformations.map(listing.opState) {
                when (it) {
                    is SearchRepositoryState.Loading -> SearchViewState.Searching
                    is SearchRepositoryState.ErrorUnknown -> {
                        if (it.hasItems) SearchViewState.ErrorUnknownWithItems else SearchViewState.ErrorUnknown
                    }
                    is SearchRepositoryState.ErrorNoConnectivity -> {
                        if (it.hasItems) SearchViewState.ErrorNoConnectivityWithItems else SearchViewState.ErrorNoConnectivity
                    }
                    is SearchRepositoryState.Loaded -> SearchViewState.DoneSearching
                }
            }
        }

        pagedList = pagedList

    }


    fun clearSearch() {
        currentSearch = EMPTY_SEARCH
        //TODO JPP can i use a mediator to do this? -> viewState.postValue(SearchViewState.Idle)
    }


    private fun extractImagePathFromSearchResult(domainSearchResult: SearchResult) = when (domainSearchResult.isMovie()) {
        true -> domainSearchResult.poster_path ?: "Unknown"
        else -> domainSearchResult.profile_path ?: "Unknown" // case: TV and MOVIE
    }

    private fun extractTitleFromSearchResult(domainSearchResult: SearchResult) = when (domainSearchResult.isMovie()) {
        true -> domainSearchResult.title ?: "Unknown"
        else -> domainSearchResult.name ?: "Unknown" // case: TV and PERSON
    }

    private fun getIconForSearchResult(domainSearchResult: SearchResult) = when (domainSearchResult.isMovie()) {
        true -> SearchResultTypeIcon.MovieType
        else -> SearchResultTypeIcon.PersonType
    }

    companion object {
        const val EMPTY_SEARCH = ""
    }

}