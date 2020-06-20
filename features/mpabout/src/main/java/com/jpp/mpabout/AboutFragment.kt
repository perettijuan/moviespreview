package com.jpp.mpabout

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jpp.mp.common.extensions.cleanView
import com.jpp.mp.common.extensions.send
import com.jpp.mp.common.extensions.setScreenTitle
import com.jpp.mp.common.extensions.web
import com.jpp.mp.common.viewmodel.MPGenericSavedStateViewModelFactory
import com.jpp.mpabout.databinding.FragmentAboutBinding
import com.jpp.mpdesign.ext.getColor
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_about.*
import javax.inject.Inject

/**
 * Fragment used to show the about section oof the application.
 *
 * When instantiated, this fragment invokes the [AboutViewModel] methods in order to retrieve
 * and show the about data. The VM will perform the fetch and will update the UI states
 * represented by [AboutViewState] and this Fragment will render those updates.
 */
class AboutFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: AboutViewModelFactory

    private lateinit var viewBinding: FragmentAboutBinding

    private val viewModel: AboutViewModel by viewModels {
        MPGenericSavedStateViewModelFactory(
            viewModelFactory,
            this
        )
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false)
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

            viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
                setScreenTitle(getString(viewState.screenTitle))
                viewBinding.viewState = viewState
                aboutRv.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = AboutItemsAdapter(viewState.content.aboutItems) {viewModel.onUserSelectedAboutItem(it) }
                    addItemDecoration(DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation))
                }
            })

            viewModel.navEvents.observe(viewLifecycleOwner, Observer { it.actionIfNotHandled { navEvent -> processNavEvent(navEvent) } })

            viewModel.onInit()
    }

    private fun processNavEvent(navEvent: AboutNavEvent) {
        when (navEvent) {
            is AboutNavEvent.InnerNavigation -> navigateInnerBrowser(navEvent.url)
            is AboutNavEvent.OpenGooglePlay -> goToRateAppScreen(navEvent.url)
            is AboutNavEvent.OpenSharing -> goToShareAppScreen(navEvent.url)
            is AboutNavEvent.OuterNavigation -> goToWebBrowser(navEvent.url)
        }
    }

    private fun goToRateAppScreen(uriString: String) {
        try {
            startActivity(Intent().cleanView(uriString))
        } catch (e: ActivityNotFoundException) {
            viewModel.onFailedToOpenPlayStore()
        }
    }

    private fun goToShareAppScreen(uriString: String) {
        startActivity(Intent().send(getString(R.string.share_app_text, uriString)))
    }

    private fun goToWebBrowser(url: String) {
        startActivity(Intent().web(url))
    }

    private fun navigateInnerBrowser(uriString: String) {
        activity?.let {
            CustomTabsIntent.Builder().apply {
                setToolbarColor(getColor(R.color.primaryColor))
                setStartAnimations(it, R.anim.activity_enter_transition, R.anim.activity_exit_transition)
                setExitAnimations(it, R.anim.activity_enter_transition, R.anim.activity_exit_transition)
            }.build().launchUrl(it, Uri.parse(uriString))
        }
    }
}
