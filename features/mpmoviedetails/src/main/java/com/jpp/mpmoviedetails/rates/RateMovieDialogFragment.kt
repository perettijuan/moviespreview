package com.jpp.mpmoviedetails.rates

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mpmoviedetails.R
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class RateMovieDialogFragment : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_rate_movie, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        withViewModel<RateMovieViewModel>(viewModelFactory) {
            viewState.observe(viewLifecycleOwner, Observer { viewState ->
                Log.d("JPPLOG", "viewstate has -> " + viewState.movieTitle)
            })
            onInit(RateMovieParam.fromArguments(arguments))
        }
    }
}