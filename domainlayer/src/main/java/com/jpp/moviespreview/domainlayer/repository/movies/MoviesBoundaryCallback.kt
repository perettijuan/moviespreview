package com.jpp.moviespreview.domainlayer.repository.movies

import androidx.annotation.MainThread
import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import com.jpp.moviespreview.domainlayer.MoviePage
import com.jpp.moviespreview.domainlayer.MovieSection
import java.util.concurrent.Executor

/**
 * This boundary callback gets notified when user reaches to the edges of the list such that the
 * database cannot provide any more data.
 * <p>
 * The boundary callback might be called multiple times for the same direction so it does its own
 * rate limiting using the PagingRequestHelper class.
 */
class MoviesBoundaryCallback(
        private val section: MovieSection,
        private val handleResponse: (MovieSection, MoviePage) -> Unit,
        private val ioExecutor: Executor,
        private val moviesApi: MoviesApi)
    : PagedList.BoundaryCallback<MoviePage>() {

    private val helper = PagingRequestHelper(ioExecutor)

    /**
     * Database returned 0 items. We should query the backend for more items.
     */
    @MainThread
    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            moviesApi.getMoviePageForSection(1, section)?.let { moviePage ->
                insertItemsIntoDb(moviePage, it)
            } ?: it.run {
                //TODO JPP here you should check connectivity problems maybe
                recordFailure(RuntimeException("WTF JPP"))
            }
        }
    }

    /**
     * User reached to the end of the list.
     */
    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd: MoviePage) {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            moviesApi.getMoviePageForSection(itemAtEnd.page + 1, section)?.let { moviePage ->
                insertItemsIntoDb(moviePage, it)
            } ?: it.run {
                //TODO JPP here you should check connectivity problems maybe
                recordFailure(RuntimeException("WTF JPP"))
            }
        }
    }

    /**
     * every time it gets new items, boundary callback simply inserts them into the database and
     * paging library takes care of refreshing the list if necessary.
     */
    private fun insertItemsIntoDb(moviePage: MoviePage, it: PagingRequestHelper.Request.Callback) {
        ioExecutor.execute {
            handleResponse(section, moviePage)
            it.recordSuccess()
        }
    }


}