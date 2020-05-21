package com.jpp.mpaccount.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.jpp.mp.common.extensions.getScreenWidthInPixels
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mp.common.fragments.MPFragment
import com.jpp.mpaccount.R
import com.jpp.mpaccount.databinding.FragmentUserAccountBinding
import kotlinx.android.synthetic.main.layout_user_account_content.*

/**
 * Fragment used to show the user's account data. It provides the user the ability to logout of the
 * application and to see the list of favorite, rated and to watch movies.
 */
class UserAccountFragment : MPFragment<UserAccountViewModel>() {

    private lateinit var viewBinding: FragmentUserAccountBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_account, container, false)
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        withViewModel {
            viewState.observe(this@UserAccountFragment.viewLifecycleOwner, Observer { viewState ->
                viewBinding.viewState = viewState
            })
            onInit(UserAccountParam.create(
                    resources,
                    getScreenWidthInPixels()
            ))
        }

        userAccountLogoutBtn.setOnClickListener {
            withViewModel { onLogout() }
            CookieManager.getInstance().removeAllCookies(null)
        }

        userAccountFavoriteMovies.setOnClickListener {
            withViewModel { onFavorites() }
        }

        userAccountRatedMovies.setOnClickListener {
            withViewModel { onRated() }
        }

        userAccountWatchlist.setOnClickListener {
            withViewModel { onWatchlist() }
        }
    }

    override fun withViewModel(action: UserAccountViewModel.() -> Unit) = withViewModel<UserAccountViewModel>(viewModelFactory) { action() }
}
