package com.jpp.moviespreview.screens.main.details

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(fromBundle(arguments)) {
            detailsId.text = movieId

            /* Enable extended action bar in main activity */
            withViewModel<MainActivityViewModel>(viewModelFactory) {
                onAction(MainActivityAction.UserSelectedMovieDetails(movieImageUrl))
            }
        }

    }
}