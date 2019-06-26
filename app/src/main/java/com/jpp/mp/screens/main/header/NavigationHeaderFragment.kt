package com.jpp.mp.screens.main.header

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jpp.mp.R
import com.jpp.mp.common.navigation.NavigationViewModel
import com.jpp.mpdesign.ext.*
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
        return inflater.inflate(R.layout.fragment_nav_header, container, false)
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
            viewStates.observe(this@NavigationHeaderFragment.viewLifecycleOwner, Observer { viewState -> viewState.actionIfNotHandled { renderViewState(it) } })
            navEvents.observe(this@NavigationHeaderFragment.viewLifecycleOwner, Observer { withNavigationViewModel { navigateToUserAccount() } })
            onInit()
        }
    }

    /**
     * Performs the branching to render the proper views given then [viewState].
     */
    private fun renderViewState(viewState: HeaderViewState) {
        when (viewState) {
            is HeaderViewState.ShowLoading -> renderLoading()
            is HeaderViewState.ShowLogin -> {
                updateLoginState()
                renderLogin()
            }
            is HeaderViewState.ShowAccount -> {
                updateWithAccountData(viewState)
                renderAccountInfo()
            }
        }
    }


    private fun withViewModel(action: NavigationHeaderViewModel.() -> Unit) = getViewModel<NavigationHeaderViewModel>(viewModelFactory).action()
    private fun withNavigationViewModel(action: NavigationViewModel.() -> Unit) = getViewModel<NavigationViewModel>(viewModelFactory).action()

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

    private fun updateLoginState() {
        navHeaderIv.apply {
            setImageResource(R.drawable.ic_person_black)
            setVisible()
        }
        navHeaderNameInitialTv.apply {
            text = ""
            setInvisible()
        }
        navHeaderUserNameTv.text = ""
        navHeaderAccountNameTv.text = ""
        view?.setBackgroundResource(R.drawable.bg_nav_header)
    }

    private fun updateWithAccountData(newContent: HeaderViewState.ShowAccount) {
        with(newContent) {
            navHeaderIv.loadImageUrlAsCircular(avatarUrl,
                    {
                        navHeaderNameInitialTv.setVisible()
                        tintBackgroundFromColor(R.color.accentColor)
                        navHeaderIv.setInvisible()
                    },
                    {
                        tintBackgroundWithBitmap(it)
                    }
            )
            navHeaderUserNameTv.text = userName
            navHeaderAccountNameTv.text = accountName
            navHeaderNameInitialTv.text = defaultLetter.toString()
        }
    }
}