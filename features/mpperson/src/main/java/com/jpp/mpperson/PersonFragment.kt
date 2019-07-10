package com.jpp.mpperson

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jpp.mp.common.extensions.withViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class PersonFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_person, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        withViewModel {
            viewStates.observe(this@PersonFragment.viewLifecycleOwner, Observer { it.actionIfNotHandled { viewState -> Log.d("JPPLOG", "RenderViewState $viewState") } })
            onInit(NavigationPerson.personId(arguments).toDouble())
        }
    }

    /**
     * Helper function to execute actions with the [PersonViewModel].
     */
    private fun withViewModel(action: PersonViewModel.() -> Unit) = withViewModel<PersonViewModel>(viewModelFactory) { action() }
}