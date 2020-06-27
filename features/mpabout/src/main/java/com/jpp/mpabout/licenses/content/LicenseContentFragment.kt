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
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jpp.mp.common.extensions.getFragmentArgument
import com.jpp.mp.common.extensions.observeValue
import com.jpp.mp.common.viewmodel.MPGenericSavedStateViewModelFactory
import com.jpp.mpabout.R
import com.jpp.mpabout.databinding.FragmentLicenseContentBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * [BottomSheetDialogFragment] used to spawn the content of a particular license when it is selected
 * from the list.
 */
class LicenseContentFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: LicenseContentViewModelFactory

    private var viewBinding: FragmentLicenseContentBinding? = null

    private val viewModel: LicenseContentViewModel by viewModels {
        MPGenericSavedStateViewModelFactory(
            viewModelFactory,
            this
        )
    }

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
            DataBindingUtil.inflate(inflater, R.layout.fragment_license_content, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.viewState.observeValue(viewLifecycleOwner, ::renderViewState)
        viewModel.onInit(getFragmentArgument("licenseIdKey"))
    }

    override fun onDestroyView() {
        viewBinding = null
        super.onDestroyView()
    }

    private fun renderViewState(viewState: LicenseContentViewState) {
        viewBinding?.viewState = viewState
    }

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
                BottomSheetBehavior.from(it).peekHeight = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    500F,
                    resources.displayMetrics
                ).toInt()
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
