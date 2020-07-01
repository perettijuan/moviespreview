package com.jpp.mpmoviedetails.rates

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.jpp.mp.common.extensions.observeHandledEvent
import com.jpp.mp.common.extensions.observeValue
import com.jpp.mp.common.viewmodel.MPGenericSavedStateViewModelFactory
import com.jpp.mpdesign.ext.mpToast
import com.jpp.mpmoviedetails.R
import com.jpp.mpmoviedetails.databinding.FragmentRateMovieBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * [DialogFragment] that shows the rating information to the user and allows to update
 * and delete that rating.
 */
class RateMovieDialogFragment : DialogFragment() {

    @Inject
    lateinit var rateMovieViewModelFactory: RateMovieViewModelFactory

    private var viewBinding: FragmentRateMovieBinding? = null

    private val viewModel: RateMovieViewModel by viewModels {
        MPGenericSavedStateViewModelFactory(
            rateMovieViewModelFactory,
            this
        )
    }

    private var rateBtn: Button? = null
    private var deleteRateBtn: Button? = null
    private var movieRatingBar: RatingBar? = null

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
            DataBindingUtil.inflate(inflater, R.layout.fragment_rate_movie, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)

        dialog?.window?.attributes?.windowAnimations = R.style.RateMovieDialogAnim

        viewModel.viewState.observeValue(viewLifecycleOwner, ::renderViewState)
        viewModel.event.observeHandledEvent(viewLifecycleOwner, ::handleUserMessage)
        viewModel.onInit(RateMovieParam.fromArguments(arguments))
    }

    override fun onDestroyView() {
        viewBinding = null
        rateBtn = null
        deleteRateBtn = null
        movieRatingBar = null
        super.onDestroyView()
    }

    private fun renderViewState(viewState: RateMovieViewState) {
        viewBinding?.viewState = viewState
    }

    private fun handleUserMessage(message: RateMovieEvent) {
        mpToast(message.messageRes)
        dismiss()
    }

    private fun setupViews(view: View) {
        rateBtn = view.findViewById(R.id.rateBtn)
        deleteRateBtn = view.findViewById(R.id.deleteRateBtn)
        movieRatingBar = view.findViewById(R.id.movieRatingBar)

        rateBtn?.setOnClickListener {
            val value = movieRatingBar?.rating ?: 0F
            viewModel.onRateMovie(value)
        }

        deleteRateBtn?.setOnClickListener {
            viewModel.onDeleteMovieRating()
        }
    }
}
