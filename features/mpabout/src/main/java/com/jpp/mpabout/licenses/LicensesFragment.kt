package com.jpp.mpabout.licenses

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.common.extensions.observeHandledEvent
import com.jpp.mp.common.extensions.observeValue
import com.jpp.mp.common.extensions.setScreenTitle
import com.jpp.mp.common.viewmodel.MPGenericSavedStateViewModelFactory
import com.jpp.mpabout.R
import com.jpp.mpabout.databinding.FragmentLicensesBinding
import com.jpp.mpabout.licenses.content.LicenseContentFragment
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Fragment used to show the the list of licenses that the application is using.
 *
 * The Fragment interacts with [LicensesViewModel] in order to render the [LicensesViewState]
 * that the VM detects that is needed.
 */
class LicensesFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: LicensesViewModelFactory

    private var viewBinding: FragmentLicensesBinding? = null

    private val viewModel: LicensesViewModel by viewModels {
        MPGenericSavedStateViewModelFactory(
            viewModelFactory,
            this
        )
    }

    private var licensesRv: RecyclerView? = null

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_licenses, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        licensesRv = view.findViewById(R.id.licensesRv)
        viewModel.viewState.observeValue(viewLifecycleOwner, ::renderViewState)
        viewModel.navEvents.observeHandledEvent(viewLifecycleOwner, ::handleEvent)
        viewModel.onInit()
    }

    override fun onDestroyView() {
        viewBinding = null
        licensesRv = null
        super.onDestroyView()
    }

    private fun renderViewState(viewState: LicensesViewState) {
        setScreenTitle(getString(viewState.screenTitle))
        viewBinding?.viewState = viewState
        licensesRv?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter =
                LicensesAdapter(viewState.content.licenseItems) { viewModel.onLicenseSelected(it) }
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    (layoutManager as LinearLayoutManager).orientation
                )
            )
        }
    }

    private fun handleEvent(event: GoToLicenseContentEvent) {
        showLicenseContent(event.licenseId)
    }

    private fun showLicenseContent(licenseId: Int) {
        LicenseContentFragment.newInstance(licenseId)
            .show(parentFragmentManager, LicenseContentFragment::javaClass.name)
    }
}
