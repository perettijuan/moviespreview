package com.jpp.mp.common.paging


import androidx.paging.PageKeyedDataSource
import com.jpp.mp.common.extensions.logYourThread

/**
 * [PageKeyedDataSource] implementation for the paging library.
 *
 * This class provides only the skeleton to fetch each page when needed, the responsibility
 * to actually fetch the data from the proper place is delegated to the lambda provided
 * in the constructor ([fetchItems]).
 *
 * Having this class to define only the skeleton of the data fetching allows us to keep the
 * separation of concerns principle: the repository takes care of looking the data in the
 * right place (either local database or remote server) and updating the local data when needed,
 * and this class takes care of knowing when a new page is needed.
 */
class MPPagingDataSource<T>(private val fetchItems: (Int, (List<T>) -> Unit) -> Unit)
    : PageKeyedDataSource<Int, T>() {

    // keep a function reference for the retry event
    private var retry: (() -> Any)? = null

    /*
     * This method is responsible to load the data initially
     * when app screen is launched for the first time.
     * We are fetching the first page data from the api
     * and passing it via the callback method to the UI.
     */
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, T>) {
        retry = { loadInitial(params, callback) }
        logYourThread()
        fetchItems(1) { itemList ->
            callback.onResult(itemList, null, 2)
        }
    }

    /*
     * This method it is responsible for the subsequent call to load the data page wise.
     * This method is executed in the background thread
     * We are fetching the next page data from the api
     * and passing it via the callback method to the UI.
     * The "params.key" variable will have the updated value.
     */
    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
        retry = { loadAfter(params, callback) }
        logYourThread()
        fetchItems(params.key) { itemList ->
            callback.onResult(itemList, params.key + 1)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, T>) {
        //no-op
    }

    /*
     * Ref => https://github.com/googlesamples/android-architecture-components/blob/master/PagingWithNetworkSample/app/src/main/java/com/android/example/paging/pagingwithnetwork/reddit/repository/inMemory/byPage/PageKeyedSubredditDataSource.kt#L50
     */
    fun retryLastCall() {
        val prevRetry = retry
        retry = null
        prevRetry?.invoke()
    }
}