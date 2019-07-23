package com.jpp.mpabout.licenses

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.common.extensions.withNavigationViewModel
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mp.common.navigation.Destination
import com.jpp.mpabout.R
import com.jpp.mpabout.databinding.FragmentLicensesBinding
import com.jpp.mpabout.licenses.content.LicenseContentFragment
import com.jpp.mpdesign.ext.inflate
import com.jpp.mpdesign.ext.setTextAppearanceCompat
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_licenses.*
import javax.inject.Inject

class LicensesFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewBinding: FragmentLicensesBinding


    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_licenses, container, false)
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        withViewModel {
            viewStates.observe(viewLifecycleOwner, Observer {
                it.actionIfNotHandled { viewState ->
                    viewBinding.viewState = viewState
                    licensesRv.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = LicensesAdapter(viewState.content.licenseItems) { withViewModel { onLicenseSelected(it) } }
                        addItemDecoration(DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation))
                    }
                }
            })

            navEvents.observe(viewLifecycleOwner, Observer { showLicenseContent(it.licenseId) })

            onInit()
        }

        // sync app bar title
        withNavigationViewModel(viewModelFactory) { destinationReached(Destination.ReachedDestination(getString(R.string.about_open_source_action))) }
    }

    /**
     * Helper function to execute actions with the [LicensesViewModel].
     */
    private fun withViewModel(action: LicensesViewModel.() -> Unit) = withViewModel<LicensesViewModel>(viewModelFactory) { action() }


    private fun showLicenseContent(licenseId: Int) {
        LicenseContentFragment().show(fragmentManager, "tag")
    }

    class LicensesAdapter(private val items: List<LicenseItem>, private val selectionListener: (LicenseItem) -> Unit) : RecyclerView.Adapter<LicensesAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent.inflate(android.R.layout.simple_list_item_1))
        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindLicense(items[position], selectionListener)
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bindLicense(license: LicenseItem, listener: (LicenseItem) -> Unit) {
                val textView = itemView.findViewById<TextView>(android.R.id.text1)
                textView.setTextAppearanceCompat(R.style.MPTextViewSmall)
                textView.text = license.name
                itemView.setOnClickListener { listener(license) }
            }
        }
    }
}