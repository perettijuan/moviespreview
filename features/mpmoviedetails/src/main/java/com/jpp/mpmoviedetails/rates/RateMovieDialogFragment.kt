package com.jpp.mpmoviedetails.rates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mp.common.fragments.MPDialogFragment
import com.jpp.mpmoviedetails.R
import com.jpp.mpmoviedetails.databinding.FragmentRateMovieBinding
import kotlinx.android.synthetic.main.fragment_rate_movie.*

class RateMovieDialogFragment : MPDialogFragment<RateMovieViewModel>() {

    private lateinit var viewBinding: FragmentRateMovieBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_rate_movie, container, false)
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        withViewModel {
            viewState.observe(viewLifecycleOwner, Observer { viewState ->
                viewBinding.viewState = viewState
            })

            userMessages.observe(viewLifecycleOwner, Observer {
                it.actionIfNotHandled { message ->
                    Toast.makeText(requireContext(), message.messageRes, Toast.LENGTH_SHORT).show()
                }
            })

            onInit(RateMovieParam.fromArguments(arguments))

            rateBtn.setOnClickListener {
                onRateMovie(movieRatingBar.rating)
            }

            deleteRateBtn.setOnClickListener {
                onDeleteMovieRating()
            }
        }
    }

    override fun withViewModel(action: RateMovieViewModel.() -> Unit): RateMovieViewModel = withViewModel(viewModelFactory) { action() }
}