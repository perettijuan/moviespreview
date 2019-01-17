package com.jpp.moviespreview.screens.main.details

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionManager
import com.jpp.moviespreview.R
import com.jpp.moviespreview.ext.withViewModel
import com.jpp.moviespreview.screens.main.MainActivityAction
import com.jpp.moviespreview.screens.main.MainActivityViewModel
import com.jpp.moviespreview.screens.main.details.MovieDetailsFragmentArgs.fromBundle
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_details.*
import javax.inject.Inject

class MovieDetailsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val args = arguments ?: throw IllegalStateException("TODO JPP -> fix this")

        with(fromBundle(args)) {
            /* Enable extended action bar in main activity */
            withViewModel<MainActivityViewModel>(viewModelFactory) {
                onAction(MainActivityAction.UserSelectedMovieDetails(movieImageUrl, movieTitle))
            }
        }

        when (savedInstanceState) {
            null -> {
                withViewModel<MovieDetailsViewModel>(viewModelFactory) {
                    init(fromBundle(args).movieId.toDouble())
                }
            }
        }

        withViewModel<MovieDetailsViewModel>(viewModelFactory) {
            viewState().observe(this@MovieDetailsFragment.viewLifecycleOwner, Observer { viewState ->
                when (viewState) {
                    MovieDetailsFragmentViewState.Loading -> R.layout.fragment_details_loading
                    MovieDetailsFragmentViewState.ErrorUnknown -> R.layout.fragment_details_error_unknown
                    MovieDetailsFragmentViewState.ErrorNoConnectivity -> R.layout.fragment_details_error_connectivity
                    is MovieDetailsFragmentViewState.ShowDetail -> {
                        detailsId.text = viewState.detail.overview
                        R.layout.fragment_details_end
                    }
                }.let {
                    val constraint = ConstraintSet()
                    constraint.clone(this@MovieDetailsFragment.context, it)
                    TransitionManager.beginDelayedTransition(fragmentDetailsRoot)
                    constraint.applyTo(fragmentDetailsRoot)
                }
            })
        }
    }

}