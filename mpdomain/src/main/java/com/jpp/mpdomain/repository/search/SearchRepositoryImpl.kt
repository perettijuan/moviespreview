package com.jpp.mpdomain.repository.search

import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.jpp.mpdomain.AppConfiguration
import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.handlers.ConnectivityHandler
import com.jpp.mpdomain.handlers.configuration.ConfigurationHandler
import com.jpp.mpdomain.repository.configuration.ConfigurationApi
import com.jpp.mpdomain.repository.configuration.ConfigurationDb
import com.jpp.mpdomain.paging.MPPagingDataSourceFactory
import java.util.concurrent.Executor

///**
// * [SearchRepository] implementation to perform a search.
// * This class delegates the paging mechanism to the DataSource created by [MPPagingDataSourceFactory],
// * but has the responsibility of actually looking for the data and updating any local storage.
// */
//class SearchRepositoryImpl(private val searchApi: SearchApi,
//                           private val configurationApi: ConfigurationApi,
//                           private val configurationDb: ConfigurationDb,
//                           private val connectivityHandler: ConnectivityHandler,
//                           private val configurationHandler: ConfigurationHandler,
//                           private val networkExecutor: Executor) : SearchRepository {
//
//    private val operationState by lazy { MutableLiveData<SearchRepositoryState>() }
//
//    override fun <T> search(query: String, imageSizeTarget: Int, mapper: (SearchResult) -> T): SearchListing<T> {
//        val dsFactory = MPPagingDataSourceFactory<SearchResult> { page, callback ->
//            searchMoviePage(query, page, callback)
//        }
//
//        val retryFunc = { networkExecutor.execute { dsFactory.retryLast() } }
//
//        val pagedList = dsFactory
//                .map { configureResultImagesPath(it, imageSizeTarget) } // map the datasource to provide the configured results
//                .map { mapper(it) } // map the ds again to return the result of the mapping function provided.
//                .let {
//                    // build the PagedList
//                    val config = PagedList.Config.Builder()
//                            .setPrefetchDistance(3)
//                            .build()
//                    LivePagedListBuilder(it, config)
//                            .setFetchExecutor(networkExecutor)
//                            .build()
//                }
//        return SearchListing(
//                pagedList = pagedList,
//                opState = operationState,
//                retry = retryFunc
//        )
//    }
//
//
//    /**
//     * This is the method that does the actual work of the repository (and it is called each
//     * time that the DataSource detects that a new page is needed).
//     * It retrieves the data from the API and post the result as needed.
//     * Note that that the results of a search are not stored in the database for the moment.
//     */
//    private fun searchMoviePage(queryString: String, page: Int, callback: (List<SearchResult>, Int) -> Unit) {
//        if (page == 1) {
//            operationState.postValue(SearchRepositoryState.Loading)
//        }
//
//        searchApi.performSearch(queryString, page)?.let { searchPage ->
//            searchPage
//                    .results
//                    .filter { it.isMovie() || it.isPerson() } // for the moment, only person and movie are valid searches
//                    .let {
//                        operationState.postValue(SearchRepositoryState.Loaded)
//                        callback(it, page + 1)
//                    }
//        } ?: run {
//            when (connectivityHandler.isConnectedToNetwork()) {
//                true -> operationState.postValue(SearchRepositoryState.ErrorUnknown(page > 1))
//                false -> operationState.postValue(SearchRepositoryState.ErrorNoConnectivity(page > 1))
//            }
//        }
//    }
//
//    /**
//     * Configure the images path of the provided [searchResult] adding the paths
//     * for the [targetImageSize].
//     */
//    private fun configureResultImagesPath(searchResult: SearchResult, targetImageSize: Int): SearchResult {
//        return getAppConfiguration()?.let {
//            configurationHandler.configureSearchResult(searchResult, it.images, targetImageSize)
//        } ?: run {
//            searchResult
//        }
//    }
//
//
//    /**
//     * Retrieve the [AppConfiguration] from the data layer when possible. When one is retrieved
//     * from the API, the repository updates the local database with it.
//     */
//    private fun getAppConfiguration(): AppConfiguration? {
//        return configurationDb.getAppConfiguration() ?: run {
//            configurationApi.getAppConfiguration()?.also { configurationDb.saveAppConfiguration(it) }
//        }
//    }
//}