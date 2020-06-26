package com.jpp.mpaccount.account

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jpp.mp.common.extensions.observeValue
import com.jpp.mp.common.extensions.setScreenTitle
import com.jpp.mp.common.viewmodel.MPGenericSavedStateViewModelFactory
import com.jpp.mpaccount.R
import com.jpp.mpaccount.databinding.FragmentUserAccountBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Fragment used to show the user's account data. It provides the user the ability to logout of the
 * application and to see the list of favorite, rated and to watch movies.
 */
class UserAccountFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: UserAccountViewModelFactory

    private var viewBinding: FragmentUserAccountBinding? = null

    private val viewModel: UserAccountViewModel by viewModels {
        MPGenericSavedStateViewModelFactory(
            viewModelFactory,
            this
        )
    }

    private var userAccountLogoutBtn: Button? = null
    private var userAccountFavoriteMovies: UserAccountMoviesView? = null
    private var userAccountRatedMovies: UserAccountMoviesView? = null
    private var userAccountWatchlist: UserAccountMoviesView? = null

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_account, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews(view)
        viewModel.headerViewState.observeValue(viewLifecycleOwner, ::renderHeaderViewState)
        viewModel.bodyViewState.observeValue(viewLifecycleOwner, ::renderBodyViewState)
        viewModel.onInit()
    }

    override fun onDestroyView() {
        viewBinding = null
        userAccountLogoutBtn = null
        userAccountFavoriteMovies = null
        userAccountRatedMovies = null
        userAccountWatchlist = null
        super.onDestroyView()
    }

    private fun setUpViews(view: View) {
        userAccountLogoutBtn = view.findViewById(R.id.userAccountLogoutBtn)
        userAccountFavoriteMovies = view.findViewById(R.id.userAccountFavoriteMovies)
        userAccountRatedMovies = view.findViewById(R.id.userAccountRatedMovies)
        userAccountWatchlist = view.findViewById(R.id.userAccountWatchlist)

        userAccountLogoutBtn?.setOnClickListener {
            viewModel.onLogout()
            CookieManager.getInstance().removeAllCookies(null)
        }

        userAccountFavoriteMovies?.setOnClickListener {
            viewModel.onFavorites()
        }

        userAccountRatedMovies?.setOnClickListener {
            viewModel.onRated()
        }

        userAccountWatchlist?.setOnClickListener {
            viewModel.onWatchlist()
        }
    }

    private fun renderHeaderViewState(headerViewState: UserAccountHeaderState) {
        setScreenTitle(getString(headerViewState.screenTitle))
        viewBinding?.headerViewState = headerViewState
    }

    private fun renderBodyViewState(bodyViewState: UserAccountBodyViewState) {
        viewBinding?.bodyViewState = bodyViewState
    }
}
