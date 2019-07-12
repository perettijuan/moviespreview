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
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_person_deprecated.*
import javax.inject.Inject

// TODO JPP delete me
class PersonFragmentDeprecated : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_person_deprecated, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val args = arguments
                ?: throw IllegalStateException("You need to pass arguments to MovieDetailsFragment in order to show the contentViewState")

//        withViewModel {
//            init(fromBundle(args).personId.toDouble(),
//                    fromBundle(args).personImageUrl,
//                    fromBundle(args).personName)

//            viewState().observe(this@PersonFragmentDeprecated.viewLifecycleOwner, Observer { viewState ->
//                when (viewState) {
//                    is PersonViewState.Loading -> {
//                        //personImageView.loadImageUrlAsCircular(viewState.imageUrl)
//                        renderLoading()
//                    }
//                    is PersonViewState.ErrorUnknown -> {
//                        personErrorView.asUnknownError { retry() }
//                        renderError()
//                    }
//                    is PersonViewState.ErrorNoConnectivity -> {
//                        personErrorView.asNoConnectivityError { retry() }
//                        renderError()
//                    }
//                    is PersonViewState.LoadedEmpty -> {
//                        renderNoPersonData()
//                    }
//                    is PersonViewState.Loaded -> {
//                        with(viewState.person) {
//                            personBirthdayRow.setValueText(birthday)
//                            personPlaceOfBirthRow.setValueText(placeOfBirth)
//                            personDeathDayRow.setValueText(deathday)
//                            personBioBodyTextView.text = biography
//                        }
//                        renderContent(
//                                viewState.showBirthday,
//                                viewState.showDeathDay,
//                                viewState.showPlaceOfBirth
//                        )
//                    }
//                }
//            })
//        }
    }

    /**
     * Helper function to execute actions with the [PersonViewModelDeprecated].
     */
//    private fun withViewModel(action: PersonViewModelDeprecated.() -> Unit) = withViewModel<PersonViewModelDeprecated>(viewModelFactory) { action() }

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