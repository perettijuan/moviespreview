package com.jpp.mpperson

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mp.common.fragments.MPFragment
import com.jpp.mpperson.databinding.FragmentPersonBinding

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
class PersonFragment : MPFragment() {

    lateinit var viewBinding: FragmentPersonBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_person, container, false)
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        withViewModel<PersonViewModel>(viewModelFactory) {
            viewState.observe(this@PersonFragment.viewLifecycleOwner, Observer { viewState ->
                viewBinding.viewState = viewState
                updateScreenTitle(viewState.screenTitle)
            })

            onInit(PersonParam.fromArguments(arguments))
        }
    }
}