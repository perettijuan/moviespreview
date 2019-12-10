package com.jpp.mpaccount.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mp.common.fragments.MPFragment
import com.jpp.mpaccount.R
import com.jpp.mpaccount.databinding.FragmentLoginBinding
import com.jpp.mpdesign.ext.snackBarNoAction
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * Fragment used to provide the user a login experience.
 * The login model supported by the application is based on Oauth2 (because the API supports that model).
 * Following that model, this Fragment provides a WebView that renders the web content needed to perform
 * the login and captures any redirection performed by the web site, delegating the responsibility
 * of performing the actual login to a ViewModel that supports this Fragment.
 *
 * The Fragment does not uses DataBinding in order to simplify the URL loading and interception
 * when the login page needs to be shown.
 */
class LoginFragment : MPFragment<LoginViewModel>() {

    private lateinit var viewBinding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        withViewModel {
            viewState.observe(this@LoginFragment.viewLifecycleOwner, Observer { viewState ->
                viewBinding.viewState = viewState

                if (viewState.oauthViewState.reminder) {
                    snackBarNoAction(loginContent, R.string.user_account_approve_reminder)
                }
            })
            onInit(getString(R.string.login_generic))
        }
    }

    override fun withViewModel(action: LoginViewModel.() -> Unit) = withViewModel<LoginViewModel>(viewModelFactory) { action() }
}
