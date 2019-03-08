package com.jpp.moviespreview.screens.main.about

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class AboutFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val aboutItemSelectionListener: (AboutItem) -> Unit = {

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
}