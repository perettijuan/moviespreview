package com.jpp.mpaccount.login

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jpp.mp.common.extensions.getViewModel
import com.jpp.mp.common.extensions.setInvisible
import com.jpp.mp.common.extensions.setVisible
import com.jpp.mpaccount.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

class LoginFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        withViewModel {
            viewStates.observe(this@LoginFragment.viewLifecycleOwner, Observer { viewState -> viewState.actionIfNotHandled { renderViewState(it) } })
            navEvents.observe(this@LoginFragment.viewLifecycleOwner, Observer {  })
            initialize()
        }
    }

    private fun withViewModel(action: LoginViewModel.() -> Unit) = getViewModel<LoginViewModel>(viewModelFactory).action()

    private fun renderViewState(viewState: LoginViewState) {
        when (viewState) {
            is LoginViewState.Loading -> loginLoadingView.setVisible()
            is LoginViewState.UnableToLogin -> TODO()
            is LoginViewState.ShowOauth -> {
                renderOauthState(viewState)
                renderWebContent()
            }
        }
    }


    private fun renderOauthState(oauthState: LoginViewState.ShowOauth) {
        accountWebView.apply {
            settings.apply {
                @SuppressLint("SetJavaScriptEnabled")
                javaScriptEnabled = true
                webViewClient = LoginWebViewClient(oauthState.interceptUrl) { redirectedUrl -> withViewModel { onUserRedirectedToUrl(redirectedUrl, oauthState.accessToken) } }
            }
            webChromeClient = LoginWebChromeClient(accountWebPg)
            loadUrl(oauthState.url)
        }

        //TODO JPP
//        if (oauthState.reminder) {
//            snackBar(accountContent, R.string.account_approve_reminder, R.string.error_retry) {
//                withViewModel { retry() }
//            }
//        }
    }


    private fun renderWebContent() {
        loginLoadingView.setInvisible()

        accountWebPg.setVisible()
        accountWebView.setVisible()
    }


    /**
     * A [WebViewClient] used to listen for changes in the WebView
     * being used to load the login page used in the Oauth flow.
     */
    private class LoginWebViewClient(private val redirectUrl: String,
                                     private val callback: (String) -> Unit) : WebViewClient() {

        @SuppressWarnings("deprecation")
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return handleUrl(url)
        }

        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest): Boolean {
            return handleUrl(request.url.toString())
        }

        private fun handleUrl(url: String): Boolean {
            if (url.startsWith(redirectUrl)) {
                callback(url)
                return true
            }
            return false
        }
    }

    /**
     * A [WebChromeClient] implementation used to listen for process
     * updates when the WebView is loading a URL.
     */
    private class LoginWebChromeClient(private val pgBar: ProgressBar) : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            when (newProgress) {
                100 -> pgBar.setInvisible()
                else -> pgBar.progress = newProgress;
            }
        }
    }
}