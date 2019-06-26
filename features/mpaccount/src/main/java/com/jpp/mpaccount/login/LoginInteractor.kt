package com.jpp.mpaccount.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jpp.mpdomain.AccessToken
import com.jpp.mpdomain.Connectivity.Connected
import com.jpp.mpdomain.Connectivity.Disconnected
import com.jpp.mpdomain.repository.AccessTokenRepository
import com.jpp.mpdomain.repository.ConnectivityRepository
import com.jpp.mpdomain.repository.SessionRepository
import javax.inject.Inject

/**
 * Interactor that supports the login process. This interactor takes care of accessing the
 * repository layer to perform the inner state updates needed to perform the user's login.
 */
class LoginInteractor @Inject constructor(private val connectivityRepository: ConnectivityRepository,
                                          private val accessTokenRepository: AccessTokenRepository,
                                          private val sessionRepository: SessionRepository) {

    /**
     * Represents the events related to the login process.
     */
    sealed class LoginEvent {
        object NotConnectedToNetwork : LoginEvent()
        object LoginSuccessful : LoginEvent()
        object LoginError : LoginEvent()
        object UserAlreadyLogged : LoginEvent()
        object ReadyToLogin : LoginEvent()
    }

    /**
     * Represents the events related to the Oauth process
     * needed to provide login functionality.
     */
    sealed class OauthEvent {
        data class OauthSuccessful(val url: String, val interceptUrl: String, val accessToken: AccessToken) : OauthEvent()
        object OauthError : OauthEvent()
        object NotConnectedToNetwork : OauthEvent()
    }


    private val _loginEvents by lazy { MutableLiveData<LoginEvent>() }
    private val _oauthEvents by lazy { MutableLiveData<OauthEvent>() }

    /**
     * @return a [LiveData] of [LoginEvent]. Subscribe to this [LiveData]
     * in order to be notified about login related events.
     */
    val loginEvents: LiveData<LoginEvent> get() = _loginEvents

    /**
     * @return a [LiveData] of [OauthEvent]. Subscribe to this [LiveData]
     * in order to be notified about Oauth process related event.
     */
    val oauthEvents: LiveData<OauthEvent> get() = _oauthEvents

    /**
     * Verifies if the user is logged in.
     * A new event will be pushed to [loginEvents] indicating the state of the user.
     */
    fun verifyUserLogged() {
        when (sessionRepository.getCurrentSession()) {
            null -> LoginEvent.ReadyToLogin
            else -> LoginEvent.UserAlreadyLogged
        }.let {
            _loginEvents.postValue(it)
        }
    }

    /**
     * Fetches the data needed to perform the Oauth step of the login process.
     * New events will be pushed to [_oauthEvents] to provide output to this
     * step.
     */
    fun fetchOauthData() {
        when (connectivityRepository.getCurrentConnectivity()) {
            is Disconnected -> OauthEvent.NotConnectedToNetwork
            is Connected -> {
                accessTokenRepository.getAccessToken()?.let {
                    OauthEvent.OauthSuccessful(
                            url = "$authUrl/${it.request_token}?redirect_to=$redirectUrl",
                            interceptUrl = redirectUrl,
                            accessToken = it
                    )
                } ?: OauthEvent.OauthError
            }
        }.let {
            _oauthEvents.postValue(it)
        }
    }

    /**
     * Executes the login step once an [AccessToken] is approved by the user.
     * Events will be pushed to [_loginEvents] in order to provide output for this
     * step.
     */
    fun loginUser(accessToken: AccessToken) {
        when (connectivityRepository.getCurrentConnectivity()) {
            is Disconnected -> LoginEvent.NotConnectedToNetwork
            is Connected -> {
                sessionRepository.createSession(accessToken)?.let {
                    LoginEvent.LoginSuccessful
                } ?: LoginEvent.LoginError
            }
        }.let {
            _loginEvents.postValue(it)
        }
    }

    private companion object {
        const val authUrl = "https://www.themoviedb.org/authenticate"
        const val redirectUrl = "http://www.mp.com/approved"
    }
}