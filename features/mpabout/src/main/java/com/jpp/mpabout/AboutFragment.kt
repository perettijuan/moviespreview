package com.jpp.mpabout

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.common.extensions.cleanView
import com.jpp.mp.common.extensions.send
import com.jpp.mp.common.extensions.web
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mp.common.fragments.MPFragment
import com.jpp.mpabout.databinding.FragmentAboutBinding
import com.jpp.mpabout.databinding.ListItemAboutBinding
import com.jpp.mpdesign.ext.getColor
import kotlinx.android.synthetic.main.fragment_about.*

/**
 * Fragment used to show the about section oof the application.
 *
 * When instantiated, this fragment invokes the [AboutViewModel] methods in order to retrieve
 * and show the about data. The VM will perform the fetch and will update the UI states
 * represented by [AboutViewState] and this Fragment will render those updates.
 */
class AboutFragment : MPFragment<AboutViewModel>() {
    private lateinit var viewBinding: FragmentAboutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false)
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        withViewModel {
            viewState.observe(viewLifecycleOwner, Observer { viewState ->
                viewBinding.viewState = viewState
                aboutRv.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = AboutItemsAdapter(viewState.content.aboutItems) { withViewModel { onUserSelectedAboutItem(it) } }
                    addItemDecoration(DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation))
                }
            })

            navEvents.observe(viewLifecycleOwner, Observer { it.actionIfNotHandled { navEvent -> processNavEvent(navEvent) } })

            onInit(getString(R.string.about_top_bar_title))
        }
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
            withViewModel { onFailedToOpenPlayStore() }
        }
    }

    override fun withViewModel(action: AboutViewModel.() -> Unit) = withViewModel<AboutViewModel>(viewModelFactory) { action() }

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