package com.jpp.mpaccount.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.jpp.mp.common.extensions.getScreenWidthInPixels
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mp.common.fragments.MPFragment
import com.jpp.mpaccount.R
import com.jpp.mpaccount.databinding.FragmentUserAccountBinding

/**
 * Fragment used to show the user's account data. It provides the user the ability to logout of the
 * application and to see the list of favorite, rated and to watch movies.
 */
class UserAccountFragment : MPFragment<UserAccountViewModel>() {

    private lateinit var viewBinding: FragmentUserAccountBinding

    private var userAccountLogoutBtn: Button? = null
    private var userAccountFavoriteMovies: UserAccountMoviesView? = null
    private var userAccountRatedMovies: UserAccountMoviesView? = null
    private var userAccountWatchlist: UserAccountMoviesView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_account, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userAccountLogoutBtn = view.findViewById(R.id.userAccountLogoutBtn)
        userAccountFavoriteMovies = view.findViewById(R.id.userAccountFavoriteMovies)
        userAccountRatedMovies = view.findViewById(R.id.userAccountRatedMovies)
        userAccountWatchlist = view.findViewById(R.id.userAccountWatchlist)

        withViewModel {
            viewState.observe(this@UserAccountFragment.viewLifecycleOwner, Observer { viewState ->
                viewBinding.viewState = viewState
            })
            onInit(UserAccountParam.create(
                resources,
                getScreenWidthInPixels()
            ))
        }

        userAccountLogoutBtn?.setOnClickListener {
            withViewModel { onLogout() }
            CookieManager.getInstance().removeAllCookies(null)
        }

        userAccountFavoriteMovies?.setOnClickListener {
            withViewModel { onFavorites() }
        }

        userAccountRatedMovies?.setOnClickListener {
            withViewModel { onRated() }
        }

        userAccountWatchlist?.setOnClickListener {
            withViewModel { onWatchlist() }
        }
    }

    override fun onDestroyView() {
        userAccountLogoutBtn = null
        userAccountFavoriteMovies = null
        userAccountRatedMovies = null
        userAccountWatchlist = null
        super.onDestroyView()
    }

    override fun withViewModel(action: UserAccountViewModel.() -> Unit) = withViewModel<UserAccountViewModel>(viewModelFactory) { action() }
}
