package com.jpp.mpperson

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mpperson.databinding.FragmentPersonBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class PersonFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewBinding: FragmentPersonBinding

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_person, container, false)
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        withViewModel {
            viewStates.observe(this@PersonFragment.viewLifecycleOwner, Observer { it.actionIfNotHandled { viewState -> viewBinding.viewState = viewState } })
            onInit(NavigationPerson.personId(arguments).toDouble())
        }
    }

    /**
     * Helper function to execute actions with the [PersonViewModel].
     */
    private fun withViewModel(action: PersonViewModel.() -> Unit) = withViewModel<PersonViewModel>(viewModelFactory) { action() }
}