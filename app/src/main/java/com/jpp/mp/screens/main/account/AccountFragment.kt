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
import androidx.navigation.fragment.findNavController
import com.jpp.mp.R
import com.jpp.mp.ext.*
import com.jpp.mp.screens.main.account.AccountFragmentDirections.toFavoriteMoviesFragment
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.layout_account_content.*
import kotlinx.android.synthetic.main.layout_account_header.*
import javax.inject.Inject

/**
 * Fragment that shows the account data of the user, the favorite movies that a user has,
 * the watchlist, etc.
 */
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
                        renderOauthState(viewState)
                        renderWebView()
                    }
                    is AccountViewState.AccountContent -> {
                        withFavoriteMoviesViewModel { init(getScreenSizeInPixels().x) }
                        updateAccountInfo(viewState)
                        renderAccountInfo()
                    }
                }
            })

            navEvents().observe(this@AccountFragment.viewLifecycleOwner, Observer { navEvent ->
                when (navEvent) {
                    is AccountNavigationEvent.ToFavoriteMovies -> navigateToFavorites()
                }
            })
        }

        withFavoriteMoviesViewModel {
            viewState().observe(this@AccountFragment.viewLifecycleOwner, Observer { favViewState -> renderFavoriteMoviesViewState(favViewState) })
        }
    }

    private fun withViewModel(action: AccountViewModel.() -> Unit) = getViewModel<AccountViewModel>(viewModelFactory).action()
    private fun withFavoriteMoviesViewModel(action: AccountFavoriteMoviesViewModel.() -> Unit) = getViewModel<AccountFavoriteMoviesViewModel>(viewModelFactory).action()


    private fun renderOauthState(oauthState: AccountViewState.Oauth) {
        accountWebView.apply {
            settings.apply {
                @SuppressLint("SetJavaScriptEnabled")
                javaScriptEnabled = true
                webViewClient = LoginWebViewClient(oauthState.interceptUrl) { redirectedUrl -> withViewModel { onUserRedirectedToUrl(redirectedUrl, oauthState) } }
            }
            webChromeClient = LoginWebChromeClient(accountWebPg)
            loadUrl(oauthState.url)
        }

        if (oauthState.reminder) {
            snackBar(accountContent, R.string.account_approve_reminder, R.string.error_retry) {
                withViewModel { retry() }
            }
        }
    }

    private fun renderFavoriteMoviesViewState(viewState: FavoriteMoviesViewState) {
        when (viewState) {
            is FavoriteMoviesViewState.Loading -> accountFavoriteMovies.showLoading()
            is FavoriteMoviesViewState.NoFavoriteMovies -> accountFavoriteMovies.showNoContent(R.string.account_no_favorite_movies)
            is FavoriteMoviesViewState.UnableToLoad -> accountFavoriteMovies.showError(R.string.account_favorite_movies_error) { withFavoriteMoviesViewModel { retry() } }
            is FavoriteMoviesViewState.FavoriteMovies -> accountFavoriteMovies.showMovies(viewState.movies) { withViewModel { onUserSelectedFavorites() } }
        }
    }

    private fun navigateToFavorites() {
        findNavController().navigate(toFavoriteMoviesFragment())
    }

    private fun updateAccountInfo(newContent: AccountViewState.AccountContent) {
        with(newContent.headerItem) {
            accountHeaderIv.loadImageUrlAsCircular(avatarUrl) {
                accountNameInitialTv.setVisible()
                accountHeaderIv.setInvisible()
            }
            accountHeaderUserNameTv.text = userName
            accountHeaderAccountNameTv.text = accountName
            accountNameInitialTv.text = defaultLetter.toString()
        }
    }

    private fun renderLoading() {
        accountContentView.setInvisible()
        accountErrorView.setInvisible()
        accountWebView.setInvisible()
        accountWebPg.setInvisible()

        accountLoadingView.setVisible()
    }

    private fun renderError() {
        accountContentView.setInvisible()
        accountLoadingView.setInvisible()
        accountWebView.setInvisible()
        accountWebPg.setInvisible()

        accountErrorView.setVisible()
    }

    private fun renderWebView() {
        accountContentView.setInvisible()
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
        accountNameInitialTv.setInvisible()

        accountContentView.setVisible()
        accountHeaderIv.setVisible()
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