package com.jpp.mpaccount.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.jpp.mp.common.extensions.observeValue
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mp.common.fragments.MPFragment
import com.jpp.mp.common.viewmodel.MPGenericSavedStateViewModelFactory
import com.jpp.mpaccount.R
import com.jpp.mpaccount.databinding.FragmentLoginBinding
import com.jpp.mpdesign.ext.snackBarNoAction
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

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
class LoginFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: LoginViewModelFactory

    private lateinit var viewBinding: FragmentLoginBinding

    private val viewModel: LoginViewModel by viewModels {
        MPGenericSavedStateViewModelFactory(
            viewModelFactory,
            this
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        return viewBinding.root
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.viewState.observeValue(viewLifecycleOwner, ::renderViewState)
        viewModel.onInit(getString(R.string.login_generic))
    }

    private fun renderViewState(viewState: LoginViewState) {
        viewBinding.viewState = viewState
        if (viewState.oauthViewState.reminder) {
            snackBarNoAction(loginContent, R.string.user_account_approve_reminder)
        }
    }
}
