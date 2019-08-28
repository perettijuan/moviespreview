package com.jpp.mpaccount.account

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jpp.mp.common.extensions.clearAllCookies
import com.jpp.mp.common.extensions.getScreenWidthInPixels
import com.jpp.mp.common.extensions.getViewModel
import com.jpp.mp.common.extensions.withNavigationViewModel
import com.jpp.mp.common.navigation.NavigationViewModel
import com.jpp.mp.common.navigation.Destination.ReachedDestination
import com.jpp.mpaccount.R
import com.jpp.mpaccount.account.UserAccountFragmentDirections.userMovieListFragment
import com.jpp.mpaccount.account.UserAccountNavigationEvent.*
import com.jpp.mpaccount.account.UserAccountViewState.*
import com.jpp.mpaccount.account.lists.UserMovieListFragment
import com.jpp.mpaccount.account.lists.UserMovieListType
import com.jpp.mpdesign.ext.*
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_user_account.*
import kotlinx.android.synthetic.main.layout_user_account_content.*
import kotlinx.android.synthetic.main.layout_user_account_header.*
import javax.inject.Inject

/**
 * Fragment used to show the details of the user's account.
 */
class UserAccountFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_account, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        withViewModel {
            viewStates.observe(this@UserAccountFragment.viewLifecycleOwner, Observer { viewState -> viewState.actionIfNotHandled { renderViewState(it) } })
            navEvents.observe(this@UserAccountFragment.viewLifecycleOwner, Observer { navEvent -> reactToNavEvent(navEvent) })
            onInit(getScreenWidthInPixels())
        }

        userAccountLogoutBtn.setOnClickListener {
            withViewModel { onLogout() }
            CookieManager.getInstance().clearAllCookies()
        }

        // sync app bar title
        withNavigationViewModel(viewModelFactory) { destinationReached(ReachedDestination(getString(R.string.account_title))) }
    }

    /**
     * Helper function to execute methods over the [UserAccountViewModel].
     */
    private fun withViewModel(action: UserAccountViewModel.() -> Unit) = getViewModel<UserAccountViewModel>(viewModelFactory).action()
    private fun withNavigationViewModel(action: NavigationViewModel.() -> Unit) = withNavigationViewModel(viewModelFactory) { action() }

    /**
     * Performs the branching to render the proper views given then [viewState].
     */
    private fun renderViewState(viewState: UserAccountViewState) {
        when (viewState) {
            is ShowLoading -> renderLoading()
            is ShowNotConnected -> renderNotConnectedToNetwork()
            is ShowUserAccountData -> {
                updateHeader(viewState)
                renderFavoriteMoviesViewState(viewState.favoriteMovieState)
                renderRatedMoviesViewState(viewState.ratedMovieState)
                renderWatchlistViewState(viewState.watchListState)
                renderAccountData()
            }
            is ShowError -> renderUnknownError()
        }
    }

    private fun renderFavoriteMoviesViewState(viewState: UserMoviesViewState) {
        when (viewState) {
            is UserMoviesViewState.ShowNoMovies -> userAccountFavoriteMovies.showErrorMessage(getString(R.string.user_account_no_favorite_movies))
            is UserMoviesViewState.ShowError -> userAccountFavoriteMovies.showErrorMessage(getString(R.string.user_account_favorite_movies_error))
            is UserMoviesViewState.ShowUserMovies -> userAccountFavoriteMovies.showMovies(viewState.items) { withViewModel { onFavorites() } }
        }
    }

    private fun renderRatedMoviesViewState(viewState: UserMoviesViewState) {
        when (viewState) {
            is UserMoviesViewState.ShowNoMovies -> userAccountRatedMovies.showErrorMessage(getString(R.string.user_account_no_rated_movies))
            is UserMoviesViewState.ShowError -> userAccountRatedMovies.showErrorMessage(getString(R.string.user_account_rated_movies_error))
            is UserMoviesViewState.ShowUserMovies -> userAccountRatedMovies.showMovies(viewState.items) { withViewModel { onRated() } }
        }
    }

    private fun renderWatchlistViewState(viewState: UserMoviesViewState) {
        when (viewState) {
            is UserMoviesViewState.ShowNoMovies -> userAccountWatchlist.showErrorMessage(getString(R.string.user_account_no_watchlist_movies))
            is UserMoviesViewState.ShowError -> userAccountWatchlist.showErrorMessage(getString(R.string.user_account_watchlist_movies_error))
            is UserMoviesViewState.ShowUserMovies -> userAccountWatchlist.showMovies(viewState.items) { withViewModel { onWatchlist() } }
        }
    }

    /**
     * Reacts to the navigation event provided.
     */
    private fun reactToNavEvent(navEvent: UserAccountNavigationEvent) {
        when (navEvent) {
            is GoToPrevious -> withNavigationViewModel { toPrevious() }
            is GoToFavorites -> withNavigationViewModel { performInnerNavigation(userMovieListFragment(UserMovieListType.FAVORITE_LIST)) }
            is GoToRated -> withNavigationViewModel { performInnerNavigation(userMovieListFragment(UserMovieListType.RATED_LIST)) }
            is GoToWatchlist -> withNavigationViewModel { performInnerNavigation(userMovieListFragment(UserMovieListType.WATCH_LIST)) }
        }
    }

    private fun renderLoading() {
        userAccountErrorView.setInvisible()
        userAccountContentView.setInvisible()

        userAccountLoadingView.setVisible()
    }

    private fun renderAccountData() {
        userAccountErrorView.setInvisible()
        userAccountLoadingView.setInvisible()

        userAccountContentView.setVisible()
    }

    private fun renderNotConnectedToNetwork() {
        userAccountLoadingView.setInvisible()
        userAccountContentView.setInvisible()

        userAccountErrorView.asNoConnectivityError { withViewModel { onRetry(getScreenWidthInPixels()) } }
        userAccountErrorView.setVisible()
    }

    private fun renderUnknownError() {
        userAccountLoadingView.setInvisible()
        userAccountContentView.setInvisible()

        userAccountErrorView.asUnknownError { withViewModel { onRetry(getScreenWidthInPixels()) } }
        userAccountErrorView.setVisible()
    }

    private fun updateHeader(newContent: ShowUserAccountData) {
        with(newContent) {
            userAccountHeaderIv.loadImageUrlAsCircular(avatarUrl,
                    {
                        userAccountNameInitialTv.setVisible()
                        accountContent.tintBackgroundFromColor(R.color.accentColor)
                        userAccountHeaderIv.setInvisible()
                    },
                    {
                        accountContent.tintBackgroundWithBitmap(it)
                    }
            )
            userAccountHeaderUserNameTv.text = userName
            userAccountHeaderAccountNameTv.text = accountName
            userAccountNameInitialTv.text = defaultLetter.toString()
        }
    }
}