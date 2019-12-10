package com.jpp.mpsearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mpdomain.Connectivity
import com.jpp.mpdomain.SearchResult
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.LanguageRepository
import com.jpp.mpdomain.repository.SearchRepository
import com.jpp.mpsearch.SearchInteractor.SearchEvent.AppLanguageChanged
import com.jpp.mpsearch.SearchInteractor.SearchEvent.NotConnectedToNetwork
import com.jpp.mpsearch.SearchInteractor.SearchEvent.UnknownError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchInteractor @Inject constructor(
    private val connectivityRepository: ConnectivityRepository,
    private val searchRepository: SearchRepository,
    private val languageRepository: LanguageRepository
) {

    sealed class SearchEvent {
        object AppLanguageChanged : SearchEvent()
        object NotConnectedToNetwork : SearchEvent()
        object UnknownError : SearchEvent()
    }

    private val _searchEvents = MediatorLiveData<SearchEvent>()

    init {
        _searchEvents.addSource(languageRepository.updates()) { _searchEvents.postValue(AppLanguageChanged) }
    }

    /**
     * @return a [LiveData] of [SearchEvent]. Subscribe to this [LiveData]
     * in order to be notified about interactor related events.
     */
    val searchEvents: LiveData<SearchEvent> get() = _searchEvents

    /**
     * Performs the onSearch for the provided [query].
     * If a result is found, [callback] will be executed with that result.
     * If an error is detected, it will be posted to [searchEvents].
     */
    fun performSearchForPage(query: String, page: Int, callback: (List<SearchResult>) -> Unit) {
        when (connectivityRepository.getCurrentConnectivity()) {
            Connectivity.Disconnected -> _searchEvents.postValue(NotConnectedToNetwork)
            Connectivity.Connected -> {
                searchRepository.searchPage(query, page, languageRepository.getCurrentAppLanguage())
                        ?.let { callback(it.results) }
                        ?: _searchEvents.postValue(UnknownError)
            }
        }
    }

    /**
     * Clears any inner state related to a search un progress.
     */
    fun flushCurrentSearch() {
        searchRepository.flushSearch()
    }
}
