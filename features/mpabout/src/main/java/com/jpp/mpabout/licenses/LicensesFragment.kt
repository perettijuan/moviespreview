package com.jpp.mpabout.licenses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mp.common.fragments.MPFragment
import com.jpp.mpabout.R
import com.jpp.mpabout.databinding.FragmentLicensesBinding
import com.jpp.mpabout.licenses.content.LicenseContentFragment
import com.jpp.mpdesign.ext.inflate
import com.jpp.mpdesign.ext.setTextAppearanceCompat
import kotlinx.android.synthetic.main.fragment_licenses.*

/**
 * Fragment used to show the the list of licenses that the application is using.
 *
 * The Fragment interacts with [LicensesViewModel] in order to render the [LicensesViewState]
 * that the VM detects that is needed.
 */
class LicensesFragment : MPFragment<LicensesViewModel>() {

    private lateinit var viewBinding: FragmentLicensesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_licenses, container, false)
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        withViewModel {
            viewState.observe(viewLifecycleOwner, Observer { viewState ->
                viewBinding.viewState = viewState
                licensesRv.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = LicensesAdapter(viewState.content.licenseItems) { withViewModel { onLicenseSelected(it) } }
                    addItemDecoration(DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation))
                }
            })

            navEvents.observe(viewLifecycleOwner, Observer { it.actionIfNotHandled { event -> showLicenseContent(event.licenseId) } })

            onInit(getString(R.string.about_open_source_action))
        }
    }

    override fun withViewModel(action: LicensesViewModel.() -> Unit) = withViewModel<LicensesViewModel>(viewModelFactory) { action() }

    private fun showLicenseContent(licenseId: Int) {
        LicenseContentFragment.newInstance(licenseId).show(requireFragmentManager(), "tag")
    }
}
