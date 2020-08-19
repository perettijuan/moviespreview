package com.jpp.mpmoviedetails

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.jpp.mp.common.extensions.observeHandledEvent
import com.jpp.mp.common.extensions.observeValue
import com.jpp.mp.common.extensions.setScreenTitle
import com.jpp.mp.common.viewmodel.MPGenericSavedStateViewModelFactory
import com.jpp.mpdesign.ext.mpToast
import com.jpp.mpdesign.ext.snackBar
import com.jpp.mpmoviedetails.NavigationMovieDetails.movieId
import com.jpp.mpmoviedetails.NavigationMovieDetails.movieImageTransitionName
import com.jpp.mpmoviedetails.NavigationMovieDetails.paramsFromBundle
import com.jpp.mpmoviedetails.databinding.FragmentMovieDetailsBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Fragment used to show the details of a particular movie selected by the user.
 *
 * VIEW RENDERING:
 * This Fragment render two different view states in the same hierarchy:
 *   1 - Movie Details View State ([MovieDetailViewState]): this view state its referred to the content
 *       of the movie details (image, title and details of the content). It is rendered using Data Binding
 *       based on the states posted by [MovieDetailsViewModel]. This might be considered as the static
 *       portion of the view.
 *   2 - The actions view state ([MovieActionsViewState]): this view state refers to the state
 *       of the actions that the user can perform on the particular movie being shown (rate, like, add
 *       to watchlist). It is based in the view states posted by [MovieDetailsActionViewModel] and it
 *       doesn't uses DataBinding since it is too complex to render the animations state in a static
 *       manner.
 *
 */
class MovieDetailsFragment : Fragment() {

    @Inject
    lateinit var movieDetailsViewModelFactory: MovieDetailsViewModelFactory

    @Inject
    lateinit var movieDetailsActionViewModelFactory: MovieDetailsActionViewModelFactory

    private var viewBinding: FragmentMovieDetailsBinding? = null

    private val viewModel: MovieDetailsViewModel by viewModels {
        MPGenericSavedStateViewModelFactory(
            movieDetailsViewModelFactory,
            this
        )
    }

    private val actionsViewModel: MovieDetailsActionViewModel by viewModels {
        MPGenericSavedStateViewModelFactory(
            movieDetailsActionViewModelFactory,
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
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
        viewBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_movie_details, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()

        viewBinding?.movieDetailImageView?.transitionName = movieImageTransitionName(arguments)

        viewModel.viewState.observeValue(viewLifecycleOwner, ::renderViewState)
        viewModel.onInit(paramsFromBundle(arguments))

        actionsViewModel.viewState.observeValue(viewLifecycleOwner, ::renderActionViewState)
        actionsViewModel.events.observeHandledEvent(viewLifecycleOwner, ::handleActionEvent)
        actionsViewModel.onInit(movieId(arguments).toDouble())
    }

    override fun onDestroyView() {
        viewBinding = null
        super.onDestroyView()
    }

    private fun setupViews() {
        viewBinding?.movieDetailBottomBar?.favoriteImageView?.setOnClickListener { actionsViewModel.onFavoriteStateChanged() }
        viewBinding?.movieDetailBottomBar?.watchlistImageView?.setOnClickListener { actionsViewModel.onWatchlistStateChanged() }

        viewBinding?.movieDetailBottomBar?.rateMovieButton?.setOnClickListener { viewModel.onRateMovieSelected() }
        viewBinding?.movieDetailBottomBar?.detailCreditsSelectionView?.setOnClickListener { viewModel.onMovieCreditsSelected() }
    }

    private fun renderViewState(viewState: MovieDetailViewState) {
        setScreenTitle(viewState.screenTitle)

        viewBinding?.viewState = viewState
        viewBinding?.executePendingBindings()

        // horizontal
        viewBinding?.detailGenresRv?.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        viewBinding?.detailGenresRv?.adapter =
            MovieDetailsGenreAdapter(viewState.contentViewState.genres)

        // vertical
        viewBinding?.movieDetailContent?.detailGenresRv?.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        viewBinding?.movieDetailContent?.detailGenresRv?.adapter =
            MovieDetailsGenreAdapter(viewState.contentViewState.genres)
    }

    private fun renderActionViewState(actionViewState: MovieActionsViewState) {
        viewBinding?.actionsViewState = actionViewState
        viewBinding?.executePendingBindings()
    }

    private fun handleActionEvent(event: MovieActionsEvent) {
        when (event) {
            is MovieActionsEvent.ShowUserNotLogged ->
                snackBar(
                    viewBinding?.detailsContent,
                    event.error,
                    event.action
                ) {
                    viewModel.onUserRequestedLogin()
                }
            is MovieActionsEvent.ShowUnexpectedError -> mpToast(event.error)
        }
    }
}
