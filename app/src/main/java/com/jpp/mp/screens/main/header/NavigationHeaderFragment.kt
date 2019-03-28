package com.jpp.mp.screens.main.header

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.jpp.mp.R
import com.jpp.mp.ext.*
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_nav_header.*
import javax.inject.Inject

/**
 * Shows the header view in the DrawerLayout shown in the MainActivity.
 */
class NavigationHeaderFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_nav_header, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navHeaderLoginButton.setOnClickListener {
            withViewModel { onUserNavigatesToLogin() }
        }

        navHeaderAccountDetailsTv.setOnClickListener {
            withViewModel { onUserNavigatesToAccountDetails() }
        }

        withViewModel {
            viewState().observe(this@NavigationHeaderFragment.viewLifecycleOwner, Observer { viewState ->
                when (viewState) {
                    is HeaderViewState.Loading -> renderLoading()
                    is HeaderViewState.Login -> renderLogin()
                    is HeaderViewState.WithInfo -> {
                        navHeaderUserNameTv.text = viewState.accountInfo.userName
                        navHeaderAccountNameTv.text = viewState.accountInfo.accountName
                        navHeaderIv.loadImageUrlAsCircular(viewState.accountInfo.avatarUrl)
                        renderAccountInfo()
                    }
                }
            })

            navEvents().observe(this@NavigationHeaderFragment.viewLifecycleOwner, Observer { navEvent ->
                when (navEvent) {
                    is HeaderNavigationEvent.ToUserAccount -> navigateToLogin()
                    is HeaderNavigationEvent.ToLogin -> navigateToLogin()
                }
            })

            init()
        }
    }


    /**
     * Helper function to execute actions with the [NavigationHeaderViewModel].
     */
    private fun withViewModel(action: NavigationHeaderViewModel.() -> Unit) = getViewModel<NavigationHeaderViewModel>(viewModelFactory).action()

    private fun navigateToLogin() {
        activity?.let {
            findNavController(it, R.id.mainNavHostFragment).navigate(R.id.accountFragment)
        }
    }

    private fun renderLoading() {
        navHeaderUserNameTv.setInvisible()
        navHeaderAccountNameTv.setInvisible()
        navHeaderAccountDetailsTv.setInvisible()
        navHeaderLoginButton.setInvisible()

        navHeaderLoadingView.setVisible()
    }

    private fun renderLogin() {
        navHeaderUserNameTv.setInvisible()
        navHeaderAccountNameTv.setInvisible()
        navHeaderAccountDetailsTv.setInvisible()
        navHeaderLoadingView.setInvisible()

        navHeaderLoginButton.setVisible()
    }

    private fun renderAccountInfo() {
        navHeaderLoadingView.setInvisible()
        navHeaderLoginButton.setGone()

        navHeaderUserNameTv.setVisible()
        navHeaderAccountNameTv.setVisible()
        navHeaderAccountDetailsTv.setVisible()
    }
}