package com.jpp.moviespreview.screens.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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
    private val viewState = MediatorLiveData<SearchViewState>()
    private var targetImageSize: Int = -1


    fun init(imageSize: Int) {
        targetImageSize = imageSize
    }


    fun search(searchText: String): LiveData<PagedList<SearchResultItem>> {
        repository.search(searchText, targetImageSize) { domainSearchResult ->
            with(domainSearchResult) {
                SearchResultItem(
                        id = id,
                        imagePath = extractImagePathFromSearchResult(this),
                        name = extractTitleFromSearchResult(this),
                        icon = getIconForSearchResult(this)
                )
            }
        }.let { listing ->
            viewState.addSource(listing.opState) {
                when (it) {
                    is SearchRepositoryState.Loading -> SearchViewState.Searching
                    is SearchRepositoryState.ErrorUnknown -> {
                        if (it.hasItems) SearchViewState.ErrorUnknownWithItems else SearchViewState.ErrorUnknown
                    }
                    is SearchRepositoryState.ErrorNoConnectivity -> {
                        if (it.hasItems) SearchViewState.ErrorNoConnectivityWithItems else SearchViewState.ErrorNoConnectivity
                    }
                    is SearchRepositoryState.Loaded -> SearchViewState.DoneSearching
                }.let {
                    viewState.postValue(it)
                }
            }
            return listing.pagedList
        }

    }

    fun viewState(): LiveData<SearchViewState> = viewState


    fun clearSearch() {
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
}