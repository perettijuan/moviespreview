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
import com.jpp.mp.common.extensions.withNavigationViewModel
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mp.common.navigation.Destination
import com.jpp.mpdesign.ext.loadImageUrlAsCircular
import com.jpp.mpperson.databinding.FragmentPersonBinding
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.layout_person_header.*
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

        withNavigationViewModel(viewModelFactory) { destinationReached(Destination.ReachedDestination(NavigationPerson.personName(arguments))) }
        /*
         * To be absolutely pure with the approach of data binding, this
         * should be implemented in a different way.
         * Probably would be nice to have a custom view that receives a String url
         * and performs the circular image URL loading, but I'm not that puristic :P.
         */
        personImageView.loadImageUrlAsCircular(NavigationPerson.personImageUrl(arguments))
    }

    /**
     * Helper function to execute actions with the [PersonViewModel].
     */
    private fun withViewModel(action: PersonViewModel.() -> Unit) = withViewModel<PersonViewModel>(viewModelFactory) { action() }
}