package com.jpp.moviespreview.screens.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jpp.moviespreview.R
import kotlinx.android.synthetic.main.fragment_movies.*

abstract class MoviesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        moviesLoadingErrorView.showLoadingImmediate()
        moviesLoadingErrorView.postDelayed({
            moviesLoadingErrorView.animateToNoConnectivityError {
                moviesLoadingErrorView.animateToLoading()
            }
        }, 1000)
    }
}