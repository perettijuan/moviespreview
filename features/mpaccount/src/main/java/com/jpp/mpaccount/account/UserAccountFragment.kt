package com.jpp.mpaccount.account

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.jpp.mpaccount.R
import com.jpp.mpdesign.ext.getViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_user_account.*
import javax.inject.Inject
import com.jpp.mpaccount.account.UserAccountNavigationEvent.*
import com.jpp.mpaccount.account.UserAccountViewState.*
import com.jpp.mpdesign.ext.loadImageUrlAsCircular
import com.jpp.mpdesign.ext.setInvisible
import com.jpp.mpdesign.ext.setVisible
import kotlinx.android.synthetic.main.layout_user_account_header.*

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
            navEvents.observe(this@UserAccountFragment.viewLifecycleOwner, Observer { navEvent ->  reactToNavEvent(navEvent)})
            onInit()
        }
    }

    /**
     * Helper function to execute methods over the [UserAccountViewModel].
     */
    private fun withViewModel(action: UserAccountViewModel.() -> Unit) = getViewModel<UserAccountViewModel>(viewModelFactory).action()

    /**
     * Performs the branching to render the proper views given then [viewState].
     */
    private fun renderViewState(viewState: UserAccountViewState) {
        when (viewState) {
            is Loading -> renderLoading()
            is NotConnected -> TODO()
            is ShowUserAccountData -> {
                updateHeader(viewState)
                renderAccountData()
            }
            is ShowError -> Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Reacts to the navigation event provided.
     */
    private fun reactToNavEvent(navEvent: UserAccountNavigationEvent) {
        when (navEvent) {
            is GoToLogin -> findNavController().navigate(R.id.toLoginFragment)
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

    private fun updateHeader(newContent: ShowUserAccountData) {
        with(newContent) {
            userAccountHeaderIv.loadImageUrlAsCircular(avatarUrl) {
                userAccountNameInitialTv.setVisible()
                userAccountHeaderIv.setInvisible()
            }
            userAccountHeaderUserNameTv.text = userName
            userAccountHeaderAccountNameTv.text = accountName
            userAccountNameInitialTv.text = defaultLetter.toString()
        }
    }
}