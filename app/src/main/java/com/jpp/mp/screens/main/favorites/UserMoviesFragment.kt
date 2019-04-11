package com.jpp.mp.screens.main.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jpp.mp.R

/**
 * TODO JPP -> paging
 * TODO JPP -> error handling with content
 * TODO JPP -> Handle RefreshAppViewModel
 */
class UserMoviesFragment : Fragment() {




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_movies, container, false)
    }
}