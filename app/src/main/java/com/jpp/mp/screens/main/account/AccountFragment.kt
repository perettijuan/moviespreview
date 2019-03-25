package com.jpp.mp.screens.main.account

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jpp.mp.R
import com.jpp.mp.ext.getViewModel
import com.jpp.mp.ext.setInvisible
import com.jpp.mp.ext.setVisible
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_account.*
import javax.inject.Inject

class AccountFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        withViewModel {
            init()

            viewState().observe(this@AccountFragment.viewLifecycleOwner, Observer { viewState ->
                when (viewState) {
                    is AccountViewState.Loading -> renderLoading()
                    is AccountViewState.ErrorUnknown -> {
                        accountErrorView.asUnknownError { TODO() }
                        renderError()
                    }
                    is AccountViewState.ErrorNoConnectivity -> {
                        accountErrorView.asNoConnectivityError { TODO() }
                        renderError()
                    }
                    is AccountViewState.RenderlURL -> Toast.makeText(activity!!, viewState.url, Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    /**
     * Helper function to execute actions with the [AccountViewModel].
     */
    private fun withViewModel(action: AccountViewModel.() -> Unit) = getViewModel<AccountViewModel>(viewModelFactory).action()

    private fun renderLoading() {
        accountErrorView.setInvisible()
        accountLoadingView.setVisible()
    }

    private fun renderError() {
        accountLoadingView.setInvisible()
        accountErrorView.setVisible()
    }
}