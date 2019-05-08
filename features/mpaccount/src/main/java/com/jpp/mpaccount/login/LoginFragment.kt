package com.jpp.mpaccount.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jpp.mp.common.extensions.getViewModel
import com.jpp.mp.common.extensions.setVisible
import com.jpp.mpaccount.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

class LoginFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        withViewModel {
            viewStates.observe(this@LoginFragment.viewLifecycleOwner, Observer { viewState -> renderViewState(viewState) })
            initialize()
        }
    }

    private fun withViewModel(action: LoginViewModel.() -> Unit) = getViewModel<LoginViewModel>(viewModelFactory).action()

    private fun renderViewState(viewState: LoginViewState) {
        when (viewState) {
            is LoginViewState.Loading -> loginLoadingView.setVisible()
            is LoginViewState.UnableToLogin -> TODO()
        }
    }
}