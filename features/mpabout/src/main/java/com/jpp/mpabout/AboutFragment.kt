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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.common.extensions.*
import com.jpp.mp.common.navigation.Destination
import com.jpp.mpabout.databinding.FragmentAboutBinding
import com.jpp.mpabout.databinding.ListItemAboutBinding
import com.jpp.mpdesign.ext.getColor
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_about.*
import javax.inject.Inject

/**
 * Fragment used to show details of a particular actor or cast member.
 *
 * When instantiated, this fragment invokes the [AboutViewModel] methods in order to retrieve
 * and show the about section. The VM will perform the
 * fetch and will update the UI states represented by [AboutViewState] and this Fragment will
 * render those updates.
 */
class AboutFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewBinding: FragmentAboutBinding


    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false)
        return viewBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        withViewModel {
            viewStates.observe(viewLifecycleOwner, Observer {
                it.actionIfNotHandled { viewState ->
                    viewBinding.viewState = viewState
                    aboutRv.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = AboutItemsAdapter(viewState.content.aboutItems) { withViewModel { onUserSelectedAboutItem(it) } }
                        addItemDecoration(DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation))
                    }

                    withNavigationViewModel(viewModelFactory) { destinationReached(Destination.ReachedDestination(getString(viewState.screenTitle))) }
                }
            })

            navEvents.observe(viewLifecycleOwner, Observer { processNavEvent(it) })

            onInit()
        }
    }

    /**
     * Helper function to execute actions with the [AboutViewModel].
     */
    private fun withViewModel(action: AboutViewModel.() -> Unit) = withViewModel<AboutViewModel>(viewModelFactory) { action() }

    private fun processNavEvent(navEvent: AboutNavEvent) {
        when (navEvent) {
            is AboutNavEvent.InnerNavigation -> navigateInnerBrowser(navEvent.url)
            is AboutNavEvent.OpenGooglePlay -> goToRateAppScreen(navEvent.url)
            is AboutNavEvent.OpenSharing -> goToShareAppScreen(navEvent.url)
            is AboutNavEvent.GoToLicenses -> goToLicensesScreen()
            is AboutNavEvent.OuterNavigation -> goToWebBrowser(navEvent.url)
        }
    }

    private fun goToRateAppScreen(uriString: String) {
        try {
            startActivity(Intent().cleanView(uriString))
        } catch (e: ActivityNotFoundException) {
            withViewModel { onFailedToOpenPlayStore() }
        }
    }

    private fun goToShareAppScreen(uriString: String) {
        startActivity(Intent().send(getString(R.string.share_app_text, uriString)))
    }

    private fun goToLicensesScreen() {
        withNavigationViewModel(viewModelFactory) { performInnerNavigation(AboutFragmentDirections.licensesFragment()) }
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


    class AboutItemsAdapter(private val items: List<AboutItem>, private val itemSelectionListener: (AboutItem) -> Unit) : RecyclerView.Adapter<AboutItemsAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                    DataBindingUtil.inflate(
                            LayoutInflater.from(parent.context),
                            R.layout.list_item_about,
                            parent,
                            false
                    )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindItem(items[position], itemSelectionListener)
        }

        override fun getItemCount(): Int = items.size


        class ViewHolder(private val itemBinding: ListItemAboutBinding) : RecyclerView.ViewHolder(itemBinding.root) {
            fun bindItem(item: AboutItem, selectionListener: (AboutItem) -> Unit) {
                itemBinding.viewState = item
                itemBinding.executePendingBindings()
                itemView.setOnClickListener { selectionListener(item) }
            }
        }
    }
}