package com.jpp.moviespreview.screens.main.licenses.content

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jpp.moviespreview.R
import com.jpp.moviespreview.ext.getViewModel
import com.jpp.moviespreview.ext.setInvisible
import com.jpp.moviespreview.ext.setVisible
import com.jpp.moviespreview.screens.main.licenses.content.LicenseContentFragmentArgs.fromBundle
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_license_content.*
import javax.inject.Inject

class LicenseContentFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_license_content, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val args = arguments
                ?: throw IllegalStateException("You need to pass arguments to LicenseContentFragment in order to show the content")

        withViewModel {
            init(fromBundle(args).licenseId.toInt())

            viewState().observe(this@LicenseContentFragment.viewLifecycleOwner, Observer { viewState ->
                when (viewState) {
                    is LicenseViewState.Loading -> { renderLoading() }
                    is LicenseViewState.ErrorUnknown -> {
                        licenseContentErrorView.asUnknownError { TODO() }
                        renderError()
                    }
                    is LicenseViewState.Loaded -> {
                        licenseContentWV.loadUrl(viewState.contentUrl)
                        renderContent()
                    }
                }
            })
        }
    }

    /**
     * Helper function to execute actions with the [LicenseContentViewModel].
     */
    private fun withViewModel(action: LicenseContentViewModel.() -> Unit) {
        getViewModel<LicenseContentViewModel>(viewModelFactory).action()
    }

    private fun renderLoading() {
        licenseContentErrorView.setInvisible()
        licenseContentWV.setInvisible()
        licenseContentLoadingView.setVisible()
    }

    private fun renderError() {
        licenseContentWV.setInvisible()
        licenseContentLoadingView.setInvisible()
        licenseContentErrorView.setVisible()
    }

    private fun renderContent() {
        licenseContentErrorView.setInvisible()
        licenseContentLoadingView.setInvisible()
        licenseContentWV.setVisible()
    }
}