package com.jpp.mp.main.discover

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jpp.mp.R
import com.jpp.mp.common.viewmodel.MPGenericSavedStateViewModelFactory
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class DiscoverMoviesFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: DiscoverMoviesViewModelFactory

    private val viewModel: DiscoverMoviesViewModel by viewModels {
        MPGenericSavedStateViewModelFactory(
            viewModelFactory,
            this
        )
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_discover_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.onInit()
    }
}