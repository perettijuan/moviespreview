package com.jpp.mpperson

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jpp.mp.common.extensions.observeValue
import com.jpp.mp.common.extensions.setScreenTitle
import com.jpp.mp.common.viewmodel.MPGenericSavedStateViewModelFactory
import com.jpp.mpperson.databinding.FragmentPersonBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Fragment used to show details of a particular actor or cast member.
 *
 * When instantiated, this fragment invokes the [PersonViewModel] methods in order to retrieve
 * and show the details of the person. The VM will perform the
 * fetch and will update the UI states represented by [PersonViewState] and this Fragment will
 * render those updates.
 *
 * Pre-condition: in order to instantiate this Fragment, a person ID must be provided in the arguments
 * of the Fragment.
 */
class PersonFragment : Fragment() {

    @Inject
    lateinit var personViewModelFactory: PersonViewModelFactory

    private var viewBinding: FragmentPersonBinding? = null

    private val viewModel: PersonViewModel by viewModels {
        MPGenericSavedStateViewModelFactory(
            personViewModelFactory,
            this
        )
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_person, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.viewState.observeValue(viewLifecycleOwner, ::renderViewState)
        viewModel.onInit(PersonParam.fromArguments(arguments))
    }

    override fun onDestroyView() {
        viewBinding = null
        super.onDestroyView()
    }

    private fun renderViewState(viewState: PersonViewState) {
        setScreenTitle(viewState.screenTitle)
        viewBinding?.viewState = viewState
    }
}
