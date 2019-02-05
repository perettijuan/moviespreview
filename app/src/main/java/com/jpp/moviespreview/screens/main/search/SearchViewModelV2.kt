package com.jpp.moviespreview.screens.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.paging.MPPagingDataSourceFactory
import com.jpp.mpdomain.usecase.ConfigSearchResultUseCase
import com.jpp.mpdomain.usecase.SearchUseCase
import com.jpp.mpdomain.usecase.SearchUseCaseResult
import com.jpp.mpdomain.usecase.search.ConfigSearchResultUseCase
import com.jpp.mpdomain.usecase.search.SearchUseCase
import com.jpp.mpdomain.usecase.search.SearchUseCaseResult
import java.util.concurrent.Executor
import javax.inject.Inject

class SearchViewModelV2 @Inject constructor(
        private val searchUseCase: SearchUseCase,
        private val configSearchResultUseCase: ConfigSearchResultUseCase,
        private val networkExecutor: Executor) {


    private var targetImageSize: Int = -1
    private val viewState = MutableLiveData<SearchViewStateV2>()


    fun init(imageSize: Int) {
        targetImageSize = imageSize
    }


    fun search(query: String) {
        viewState.postValue(SearchViewStateV2.Searching)
        searchUseCase
                .search(query)
                .let { result ->
                    when (result) {
                        is SearchUseCaseResult.ErrorNoConnectivity -> SearchViewStateV2.ErrorNoConnectivity
                        is SearchUseCaseResult.Success -> SearchViewStateV2.DoneSearching(createSearchPagedList(result.dsFactory))
                    }
                }
                .also {
                    viewState.postValue(it)
                }
    }


    private fun createSearchPagedList(dsFactory: MPPagingDataSourceFactory<SearchResult>): LiveData<PagedList<SearchResultItem>> {
        return dsFactory
                .map { configSearchResultUseCase.configure(targetImageSize, it) }
                .map { mapSearchResult(it) }
                .let {
                    // build the PagedList
                    val config = PagedList.Config.Builder()
                            .setPrefetchDistance(3)
                            .build()
                    LivePagedListBuilder(it, config)
                            .setFetchExecutor(networkExecutor)
                            .build()
                }
    }


    private fun mapSearchResult(searchResult: SearchResult) = with(searchResult) {
        with(searchResult) {
            SearchResultItem(
                    id = id,
                    imagePath = extractImagePathFromSearchResult(this),
                    name = extractTitleFromSearchResult(this),
                    icon = getIconForSearchResult(this)
            )
        }
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