package com.jpp.moviespreview.screens.main.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.jpp.moviespreview.R
import kotlinx.android.synthetic.main.fragment_movies.*

abstract class MoviesFragment : Fragment() {

    private lateinit var viewModel: MoviesFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this).get(MoviesFragmentViewModel::class.java)
        } ?: throw RuntimeException("Invalid Activity")

        if (savedInstanceState == null) {
            viewModel.onIntent(MoviesFragmentIntent.ConfigureApplication)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_movies, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.bindViewState().observe(this, Observer {
            when (it) {
                MoviesFragmentViewState.Loading -> moviesLoadingErrorView.animateToLoading()
                MoviesFragmentViewState.ErrorUnknown -> moviesLoadingErrorView.animateToUnknownError {}
                MoviesFragmentViewState.ErrorNoConnectivity -> moviesLoadingErrorView.animateToNoConnectivityError {}
                MoviesFragmentViewState.Configured -> {
                    moviesLoadingErrorView.hideWithAnimation { Toast.makeText(activity, "It Works!", Toast.LENGTH_LONG).show() }
                }
            }
        })
    }
}