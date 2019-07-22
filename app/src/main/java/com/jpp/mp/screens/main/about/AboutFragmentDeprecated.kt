package com.jpp.mp.screens.main.about

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.R
import com.jpp.mp.common.extensions.send
import com.jpp.mp.common.extensions.web
import com.jpp.mp.ext.getText
import com.jpp.mp.ext.inflate
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_about_depreacted.*
import kotlinx.android.synthetic.main.list_item_about_deprecated.view.*
import javax.inject.Inject

/**
 * Shows the about section in the application.
 */
//TODO DELETE ME
class AboutFragmentDeprecated : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val aboutItemSelectionListener: (AboutItem) -> Unit = {

    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about_depreacted, container, false)
    }


    private fun renderContent(appVersion: String, aboutItems: List<AboutItem>) {
        aboutVersion.text = appVersion
        aboutRv.apply {
            layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = AboutItemsAdapter(aboutItems, aboutItemSelectionListener)
        }
    }


    private fun goToShareAppScreen(uriString: String) {
        startActivity(Intent().send(getString(R.string.share_app_text, uriString)))
    }

    private fun goToLicensesScreen() {
        //findNavController().navigate(AboutFragmentDirections.actionAboutFragmentToLicensesFragment())
    }

    private fun goToWebBrowser(url: String) {
        startActivity(Intent().web(url))
    }

    private fun navigateInnerBrowser(uriString: String) {
//        activity?.let {
//            CustomTabsIntent.Builder().apply {
//                setToolbarColor(resources.getColor(R.color.primaryColor))
//                setStartAnimations(it, R.anim.activity_enter_transition, R.anim.activity_exit_transition)
//                setExitAnimations(it, R.anim.activity_enter_transition, R.anim.activity_exit_transition)
//            }.build().launchUrl(it, Uri.parse(uriString))
//        }
    }

    class AboutItemsAdapter(private val items: List<AboutItem>, private val itemSelectionListener: (AboutItem) -> Unit) : RecyclerView.Adapter<AboutItemsAdapter.ViewHolder>() {

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], itemSelectionListener)

        override fun getItemCount() = items.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.list_item_about_deprecated))

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
}