package com.jpp.moviespreview.screens.main.about

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.moviespreview.R
import com.jpp.moviespreview.ext.getText
import com.jpp.moviespreview.ext.getViewModel
import com.jpp.moviespreview.ext.inflate
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_about.*
import kotlinx.android.synthetic.main.list_item_about.view.*
import javax.inject.Inject

/**
 * Shows the about section in the application.
 */
class AboutFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val aboutItemSelectionListener: (AboutItem) -> Unit = {
        withViewModel { onUserSelectedAboutItem(it) }
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        withViewModel {
            init()

            viewState().observe(this@AboutFragment.viewLifecycleOwner, Observer { viewState ->
                when (viewState) {
                    is AboutViewState.InitialContent -> renderContent(viewState.appVersion, viewState.aboutItems)
                }
            })

            navEvents().observe(this@AboutFragment.viewLifecycleOwner, Observer { navEvent ->
                when (navEvent) {
                    is AboutNavEvent.InnerNavigation -> navigateInnerBrowser(navEvent.url)
                    is AboutNavEvent.RateApp -> goToRateAppScreen()
                    is AboutNavEvent.ShareApp -> goToShareAppScreen()
                }
            })
        }
    }

    /**
     * Helper function to execute actions with the [AboutViewModel].
     */
    private fun withViewModel(action: AboutViewModel.() -> Unit) {
        getViewModel<AboutViewModel>(viewModelFactory).action()
    }

    private fun renderContent(appVersion: String, aboutItems: List<AboutItem>) {
        aboutVersion.text = appVersion
        aboutRv.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = AboutItemsAdapter(aboutItems, aboutItemSelectionListener)
        }
    }



    private fun goToRateAppScreen() {
        activity?.run {
            try {
                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                    var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                    flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                    } else {
                        flags or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
                    }
                    addFlags(flags)
                }.run {
                    startActivity(this)
                }
            } catch (e: ActivityNotFoundException) {
                Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=$packageName"))
                        .run { startActivity(this) }
            }
        }
    }

    private fun goToShareAppScreen() {
        activity?.run {
            Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT,
                        getString(R.string.share_app_text, Uri.parse(String.format("%s?personId=%s", APP_WEB_URL, packageName))))
                type = "text/plain"
            }.run {
                startActivity(this)
            }
        }
    }

    private fun navigateInnerBrowser(uriString: String) {
        activity?.let {
            CustomTabsIntent.Builder().apply {
                setToolbarColor(resources.getColor(R.color.primaryColor))
                setStartAnimations(it, R.anim.activity_enter_transition, R.anim.activity_exit_transition)
                setExitAnimations(it, R.anim.activity_enter_transition, R.anim.activity_exit_transition)
            }.build().launchUrl(it, Uri.parse(uriString))
        }
    }

    class AboutItemsAdapter(private val items: List<AboutItem>, private val itemSelectionListener: (AboutItem) -> Unit) : RecyclerView.Adapter<AboutItemsAdapter.ViewHolder>() {

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], itemSelectionListener)

        override fun getItemCount() = items.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.list_item_about))

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            fun bind(item: AboutItem, itemSelectionListener: (AboutItem) -> Unit) {
                with(item) {
                    itemView.aboutItemIcon.setImageResource(icon)
                    itemView.aboutItemTitle.text = itemView.getText(title)
                    itemView.setOnClickListener { itemSelectionListener(this) }
                }
            }
        }
    }

    companion object {
        const val APP_WEB_URL = "https://play.google.com/store/apps/details"
    }

}