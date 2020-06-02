package com.jpp.mpmoviedetails.rates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import androidx.databinding.DataBindingUtil
import com.jpp.mp.common.extensions.observeHandledEvent
import com.jpp.mp.common.extensions.observeValue
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mp.common.fragments.MPDialogFragment
import com.jpp.mpdesign.ext.mpToast
import com.jpp.mpmoviedetails.R
import com.jpp.mpmoviedetails.databinding.FragmentRateMovieBinding

class RateMovieDialogFragment : MPDialogFragment<RateMovieViewModel>() {

    private lateinit var viewBinding: FragmentRateMovieBinding

    private var rateBtn: Button? = null
    private var deleteRateBtn: Button? = null
    private var movieRatingBar: RatingBar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_rate_movie, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        rateBtn = null
        deleteRateBtn = null
        movieRatingBar = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog?.window?.attributes?.windowAnimations = R.style.RateMovieDialogAnim

        withViewModel {
            viewState.observeValue(viewLifecycleOwner, ::renderViewState)
            userMessages.observeHandledEvent(viewLifecycleOwner, ::handleUserMessage)
            onInit(RateMovieParam.fromArguments(arguments))
        }
    }

    private fun renderViewState(viewState: RateMovieViewState) {
        viewBinding.viewState = viewState
    }

    private fun handleUserMessage(message: RateMovieUserMessages) {
        mpToast(message.messageRes)
    }

    private fun setupViews(view: View) {
        rateBtn = view.findViewById(R.id.rateBtn)
        deleteRateBtn = view.findViewById(R.id.deleteRateBtn)
        movieRatingBar = view.findViewById(R.id.movieRatingBar)

        rateBtn?.setOnClickListener {
            withViewModel {
                val value = movieRatingBar?.rating ?: 0F
                onRateMovie(value)
            }
        }

        deleteRateBtn?.setOnClickListener {
            withViewModel { onDeleteMovieRating() }
        }
    }

    override fun withViewModel(action: RateMovieViewModel.() -> Unit): RateMovieViewModel =
        withViewModel(viewModelFactory) { action() }
}
