package com.jpp.mp.screens.main.person

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jpp.mp.R
import com.jpp.mp.ext.*
import com.jpp.mp.screens.main.RefreshAppViewModel
import com.jpp.mp.screens.main.person.PersonFragmentArgs.fromBundle
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_person.*
import kotlinx.android.synthetic.main.layout_person_header.*
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

        val args = arguments
                ?: throw IllegalStateException("You need to pass arguments to MovieDetailsFragment in order to show the content")

        withViewModel {
            init(fromBundle(args).personId.toDouble(),
                    fromBundle(args).personImageUrl,
                    fromBundle(args).personName)

            viewState().observe(this@PersonFragment.viewLifecycleOwner, Observer { viewState ->
                when (viewState) {
                    is PersonViewState.Loading -> {
                        personImageView.loadImageUrlAsCircular(viewState.imageUrl)
                        renderLoading()
                    }
                    is PersonViewState.ErrorUnknown -> {
                        personErrorView.asUnknownError { retry() }
                        renderError()
                    }
                    is PersonViewState.ErrorNoConnectivity -> {
                        personErrorView.asNoConnectivityError { retry() }
                        renderError()
                    }
                    is PersonViewState.LoadedEmpty -> {
                        renderNoPersonData()
                    }
                    is PersonViewState.Loaded -> {
                        with(viewState.person) {
                            personBirthdayRow.setValue(birthday)
                            personPlaceOfBirthRow.setValue(placeOfBirth)
                            personDeathDayRow.setValue(deathday)
                            personBioBodyTextView.text = biography
                        }
                        renderContent(
                                viewState.showBirthday,
                                viewState.showDeathDay,
                                viewState.showPlaceOfBirth
                        )
                    }
                }
            })
        }

        /*
        * Get notified if the app being shown to the user needs to be refreshed for some reason
        * and do it.
        */
        withRefreshAppViewModel {
            refreshState().observe(this@PersonFragment.viewLifecycleOwner, Observer {
                if (it) {
                    withViewModel {
                        refresh(fromBundle(args).personId.toDouble(),
                                fromBundle(args).personImageUrl,
                                fromBundle(args).personName)
                    }
                }
            })
        }
    }

    /**
     * Helper function to execute actions with the [PersonViewModel].
     */
    private fun withViewModel(action: PersonViewModel.() -> Unit) = withViewModel<PersonViewModel>(viewModelFactory) { action() }

    /**
     * Helper function to execute actions with [RefreshAppViewModel] backed by the MainActivity.
     */
    private fun withRefreshAppViewModel(action: RefreshAppViewModel.() -> Unit) = withViewModel<RefreshAppViewModel>(viewModelFactory) { action() }

    private fun renderLoading() {
        personBirthdayRow.setInvisible()
        personPlaceOfBirthRow.setInvisible()
        personDeathDayRow.setInvisible()
        personBioTitleTextView.setInvisible()
        personBioBodyTextView.setInvisible()
        personErrorView.setInvisible()
        personDetailNoInfoTextView.setInvisible()

        personLoadingView.setVisible()
    }

    private fun renderError() {
        personBirthdayRow.setInvisible()
        personPlaceOfBirthRow.setInvisible()
        personDeathDayRow.setInvisible()
        personBioTitleTextView.setInvisible()
        personBioBodyTextView.setInvisible()
        personLoadingView.setInvisible()
        personDetailNoInfoTextView.setInvisible()

        personErrorView.setVisible()
    }

    private fun renderContent(withBirthday: Boolean, withDeathDay: Boolean, withPlaceOfBirth: Boolean) {
        personLoadingView.setInvisible()
        personErrorView.setInvisible()
        personDetailNoInfoTextView.setInvisible()

        personBirthdayRow.setVisibleWhen(withBirthday)
        personPlaceOfBirthRow.setVisibleWhen(withPlaceOfBirth)
        personDeathDayRow.setVisibleWhen(withDeathDay)
        personBioTitleTextView.setVisible()
        personBioBodyTextView.setVisible()
    }

    private fun renderNoPersonData() {
        personBirthdayRow.setInvisible()
        personPlaceOfBirthRow.setInvisible()
        personDeathDayRow.setInvisible()
        personBioTitleTextView.setInvisible()
        personBioBodyTextView.setInvisible()
        personLoadingView.setInvisible()
        personErrorView.setInvisible()

        personDetailNoInfoTextView.setVisible()
    }
}