package com.jpp.mp.screens.main.licenses

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.R
import com.jpp.mp.ext.inflate
import com.jpp.mp.ext.setInvisible
import com.jpp.mp.ext.setVisible
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_licenses_deprecated.*
import javax.inject.Inject

/**
 * Shows the list of Licenses that are used by the application.
 */
//TODO delete ME
class LicensesFragmentDeprecated : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_licenses_deprecated, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }


    private fun renderLoading() {
        licensesErrorView.setInvisible()
        licensesRv.setInvisible()

        licensesLoadingView.setVisible()
    }

    private fun renderError() {
        licensesRv.setInvisible()
        licensesLoadingView.setInvisible()

        licensesErrorView.setVisible()
    }

    private fun renderLicenses() {
        licensesLoadingView.setInvisible()
        licensesErrorView.setInvisible()

        licensesRv.setVisible()
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
//                textView.setTextAppearanceCompat(R.style.MPTextViewSmall)
                textView.text = license.name
                itemView.setOnClickListener { listener(license) }
            }
        }
    }
}