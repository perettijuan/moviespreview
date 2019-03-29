package com.jpp.mp.screens.main.account

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
import com.jpp.mp.R
import com.jpp.mp.ext.getViewModel
import com.jpp.mp.ext.setInvisible
import com.jpp.mp.ext.setVisible
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_account.*
import javax.inject.Inject


class AccountFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        accountWebView.apply {
            isHorizontalScrollBarEnabled = false
            isVerticalScrollBarEnabled = false
            isFocusable = true
            isFocusableInTouchMode = true
            setOnTouchListener { v, _ ->
                if (!v.hasFocus()) {
                    v.requestFocus()
                }
                false
            }
        }

        withViewModel {
            init()

            viewState().observe(this@AccountFragment.viewLifecycleOwner, Observer { viewState ->
                when (viewState) {
                    is AccountViewState.Loading -> renderLoading()
                    is AccountViewState.ErrorUnknown -> {
                        accountErrorView.asUnknownError { withViewModel { retry() } }
                        renderError()
                    }
                    is AccountViewState.ErrorNoConnectivity -> {
                        accountErrorView.asNoConnectivityError { withViewModel { retry() } }
                        renderError()
                    }
                    is AccountViewState.Oauth -> {
                        accountWebView.apply {
                            settings.apply {
                                @SuppressLint("SetJavaScriptEnabled")
                                javaScriptEnabled = true
                                webViewClient = LoginWebViewClient(viewState.interceptUrl) { loggedIn ->
                                    if (loggedIn) onUserAuthenticated(viewState.accessToken) else onUserFailedToAuthenticate()
                                }
                            }
                            webChromeClient = LoginWebChromeClient(accountWebPg)
                            loadUrl(viewState.url)
                        }
                        renderWebView()
                    }
                    is AccountViewState.AccountInfo -> {
                        accountUserNameTv.text = viewState.accountItem.userName
                        accountNameTv.text = viewState.accountItem.accountName
                        renderAccountInfo()
                    }
                }
            })
        }
    }

    /**
     * Helper function to execute actions with the [AccountViewModel].
     */
    private fun withViewModel(action: AccountViewModel.() -> Unit) = getViewModel<AccountViewModel>(viewModelFactory).action()

    private fun renderLoading() {
        accountUserNameTv.setInvisible()
        accountNameTv.setInvisible()

        accountErrorView.setInvisible()
        accountWebView.setInvisible()
        accountWebPg.setInvisible()

        accountLoadingView.setVisible()
    }

    private fun renderError() {
        accountUserNameTv.setInvisible()
        accountNameTv.setInvisible()

        accountLoadingView.setInvisible()
        accountWebView.setInvisible()
        accountWebPg.setInvisible()

        accountErrorView.setVisible()
    }

    private fun renderWebView() {
        accountUserNameTv.setInvisible()
        accountNameTv.setInvisible()

        accountLoadingView.setInvisible()
        accountErrorView.setInvisible()

        accountWebPg.setVisible()
        accountWebView.setVisible()
    }

    private fun renderAccountInfo() {
        accountLoadingView.setInvisible()
        accountErrorView.setInvisible()
        accountWebView.setInvisible()
        accountWebPg.setInvisible()

        accountUserNameTv.setVisible()
        accountNameTv.setVisible()
    }


    /**
     * A [WebViewClient] used to listen for changes in the WebView
     * being used to load the login page used in the Oauth flow.
     */
    private class LoginWebViewClient(private val redirectUrl: String,
                                     private val callback: (Boolean) -> Unit) : WebViewClient() {

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
                callback(true)
                return true
            } else if (url.contains("error")) {
                callback(false)
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