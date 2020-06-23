package com.jpp.mpaccount.login

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.BindingAdapter

/**
 * Contains the data binding adapters needed for the login section of the application.
 */
internal object LoginBinding {

    /**
     * Custom Binding Adapter used to load a [url] into a [WebView] and call
     * the [redirectListener] when the [interceptPrefix] is redirected internally by
     * [loginWebView].
     */
    @JvmStatic
    @BindingAdapter("bind:url", "bind:interceptPrefix", "bind:redirectListener")
    fun loadUrlWithIntercept(
        loginWebView: WebView,
        url: String?,
        interceptPrefix: String?,
        redirectListener: ((String) -> Unit)?
    ) {
        if (url == null ||
                interceptPrefix == null ||
                redirectListener == null) {
            return
        }

        with((loginWebView)) {
            settings.apply {
                @SuppressLint("SetJavaScriptEnabled")
                javaScriptEnabled = true
                webViewClient = LoginWebViewClient(interceptPrefix) { redirectedUrl -> redirectListener(redirectedUrl) }
            }
            loadUrl(url)
        }
    }

    /**
     * A [WebViewClient] used to listen for changes in the WebView
     * being used to load the login page used in the Oauth flow.
     */
    private class LoginWebViewClient(
        private val redirectUrl: String,
        private val callback: (String) -> Unit
    ) : WebViewClient() {

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
}
