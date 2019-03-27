package com.jpp.mp.screens.main.account

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
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
import android.annotation.SuppressLint


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
                        accountErrorView.asUnknownError { TODO() }
                        renderError()
                    }
                    is AccountViewState.ErrorNoConnectivity -> {
                        accountErrorView.asNoConnectivityError { TODO() }
                        renderError()
                    }
                    is AccountViewState.RenderlURL -> {
                        accountWebView.apply {
                            settings.apply {
                                @SuppressLint("SetJavaScriptEnabled")
                                javaScriptEnabled = true
                                webViewClient = LoginWebViewClient(viewState.interceptUrl) { loggedIn ->
                                    if (loggedIn) onUserAuthenticated() else onUserFailedToAuthenticate()
                                }
                            }
                            loadUrl(viewState.url)
                        }
                        renderWebView()
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
        accountErrorView.setInvisible()
        accountWebView.setInvisible()
        accountLoadingView.setVisible()
    }

    private fun renderError() {
        accountLoadingView.setInvisible()
        accountWebView.setInvisible()
        accountErrorView.setVisible()
    }

    private fun renderWebView() {
        accountLoadingView.setInvisible()
        accountErrorView.setInvisible()
        accountWebView.setVisible()
    }


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
}