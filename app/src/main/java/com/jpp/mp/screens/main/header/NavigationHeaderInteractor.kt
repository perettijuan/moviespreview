package com.jpp.mp.screens.main.header

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jpp.mp.screens.main.header.NavigationHeaderInteractor.HeaderDataEvent.*
import com.jpp.mpdomain.Session
import com.jpp.mpdomain.UserAccount
import com.jpp.mpdomain.repository.AccountRepository
import com.jpp.mpdomain.repository.SessionRepository
import javax.inject.Inject

class NavigationHeaderInteractor @Inject constructor(private val sessionRepository: SessionRepository,
                                                     private val accountRepository: AccountRepository) {


    /**
     * Represents the events that this interactor can route to the upper layers.
     */
    sealed class HeaderDataEvent {
        object UserNotLogged : HeaderDataEvent()
        object UnknownError : HeaderDataEvent()
        data class Success(val data: UserAccount) : HeaderDataEvent()
    }

    private val _events by lazy { MediatorLiveData<HeaderDataEvent>() }

    init {
        _events.addSource(sessionRepository.sessionStateUpdates()) { session ->
            when (session) {
                null -> UserNotLogged
                else -> getUserAccount(session)
            }.let {
                _events.postValue(it)
            }
        }
    }

    /**
     * @return a [LiveData] of [HeaderDataEvent]. Subscribe to this [LiveData]
     * in order to be notified about interactor related events.
     */
    val userAccountEvents: LiveData<HeaderDataEvent> get() = _events

    fun fetchUserData() {
        when (val session = sessionRepository.getCurrentSession()) {
            null -> UserNotLogged
            else -> getUserAccount(session)
        }.let {
            _events.postValue(it)
        }
    }

    private fun getUserAccount(session: Session): HeaderDataEvent {
        /*
         * //TODO JPP esto esta tirando un error cuando el usuario se loguea.
         * Podria hacer lo siguiente: que AccountRepository tenga un LiveData,
         * mapeo eso a un evento del interactor.
         */
        return accountRepository.getUserAccount(session)?.let {
            Success(data = it)
        } ?: UnknownError
    }
}