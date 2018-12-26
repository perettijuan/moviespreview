package com.jpp.moviespreview.screens.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jpp.moviespreview.R
import com.jpp.moviespreview.screens.details.MovieDetailsFragmentArgs.fromBundle
import kotlinx.android.synthetic.main.fragment_details.*

class MovieDetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movieId = fromBundle(arguments).movieId
        detailsId.text = movieId
    }
}