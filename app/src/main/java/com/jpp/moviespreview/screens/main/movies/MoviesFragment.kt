package com.jpp.moviespreview.screens.main.movies

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.jpp.moviespreview.R
import com.jpp.moviespreview.ext.setInvisible
import com.jpp.moviespreview.ext.setVisible
import kotlinx.android.synthetic.main.fragment_movies.*
import javax.inject.Inject
import dagger.android.support.AndroidSupportInjection



abstract class MoviesFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MoviesFragmentViewModel


    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_movies, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activity?.run {
            ViewModelProviders.of(this, viewModelFactory).get(MoviesFragmentViewModel::class.java)
        } ?: throw RuntimeException("Invalid Activity")


        if (savedInstanceState == null) {
            viewModel.onIntent(MoviesFragmentIntent.ConfigureApplication)
        }

        viewModel.bindViewState().observe(this, Observer {
            when (it) {
                MoviesFragmentViewState.Loading -> {
                    moviesLoadingErrorView.setVisible()
                    moviesLoadingErrorView.animateToLoading()
                }
                MoviesFragmentViewState.ErrorUnknown -> {
                    moviesLoadingErrorView.setVisible()
                    moviesLoadingErrorView.animateToUnknownError {}
                }
                MoviesFragmentViewState.ErrorNoConnectivity -> {
                    moviesLoadingErrorView.setVisible()
                    moviesLoadingErrorView.animateToNoConnectivityError {}
                }
                MoviesFragmentViewState.Configured -> {
                    moviesLoadingErrorView.hideWithAnimation {
                        moviesLoadingErrorView.setInvisible()
                        Toast.makeText(activity, "It Works!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }
}