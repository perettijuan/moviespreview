package com.jpp.mpabout.licenses.content

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mpabout.R
import com.jpp.mpabout.databinding.FragmentLicenseContentBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class LicenseContentFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewBinding: FragmentLicenseContentBinding

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_license_content, container, false)
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            withViewModel {
                viewStates.observe(viewLifecycleOwner, Observer {
                    it.actionIfNotHandled { viewState ->
                        viewBinding.viewState = viewState
                    }
                })
                onInit(it.getInt("licenseIdKey"))
            }
        }
    }

    /**
     * Helper function to execute actions with the [LicensesViewModel].
     */
    private fun withViewModel(action: LicenseContentViewModel.() -> Unit) = withViewModel<LicenseContentViewModel>(viewModelFactory) { action() }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        /*
         * This code helps the bottom sheet to occupy a good portion of the screen.
         * Otherwise, it shows in a small portion of it.
         */
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { shownDialog ->
            val d = shownDialog as BottomSheetDialog
            (d.findViewById(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?)?.let {
                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                BottomSheetBehavior.from(it).peekHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 500F, resources.displayMetrics).toInt()
            }
        }
        return dialog
    }


    companion object {
        fun newInstance(licenseId: Int) = LicenseContentFragment().apply {
            arguments = Bundle().apply { putInt("licenseIdKey", licenseId) }
        }
    }
}