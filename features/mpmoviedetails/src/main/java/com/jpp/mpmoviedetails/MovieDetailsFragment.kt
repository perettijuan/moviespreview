package com.jpp.mpmoviedetails

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jpp.mp.common.extensions.observeValue
import com.jpp.mp.common.extensions.setScreenTitle
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mp.common.viewmodel.MPGenericSavedStateViewModelFactory
import com.jpp.mpdesign.ext.setInvisible
import com.jpp.mpdesign.ext.setVisible
import com.jpp.mpdesign.ext.snackBar
import com.jpp.mpdesign.ext.snackBarNoAction
import com.jpp.mpdesign.views.MPFloatingActionButton
import com.jpp.mpmoviedetails.NavigationMovieDetails.movieId
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
 *   2 - The actions view state ([MovieDetailActionViewState]): this view state refers to the state
 *       of the actions that the user can perform on the particular movie being shown (rate, like, add
 *       to watchlist). It is based in the view states posted by [MovieDetailsActionViewModel] and it
 *       doesn't uses DataBinding since it is too complex to render the animations state in a static
 *       manner.
 *
 */
class MovieDetailsFragment : Fragment() {

    @Inject
    lateinit var movieDetailsViewModelFactory: MovieDetailsViewModelFactory

    //This will be removed once MovieDetailsActionViewModel is properly created
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewBinding: FragmentMovieDetailsBinding

    private val viewModel: MovieDetailsViewModel by viewModels {
        MPGenericSavedStateViewModelFactory(
            movieDetailsViewModelFactory,
            this
        )
    }

    private var detailsContent: CoordinatorLayout? = null
    private var movieDetailFavoritesFab: MPFloatingActionButton? = null
    private var movieDetailWatchlistFab: MPFloatingActionButton? = null
    private var movieDetailRateFab: MPFloatingActionButton? = null
    private var movieDetailReloadActionFab: FloatingActionButton? = null
    private var movieDetailActionFab: FloatingActionButton? = null
    private var movieDetailActionsLoadingView: ProgressBar? = null

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
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)

        viewModel.viewState.observeValue(viewLifecycleOwner, ::renderViewState)
        viewModel.onInit(paramsFromBundle(arguments))

        withActionsViewModel {
            viewState.observeValue(viewLifecycleOwner, ::renderActionViewState)
            onInit(movieId(arguments).toDouble())
        }
    }

    override fun onDestroyView() {
        detailsContent = null
        movieDetailFavoritesFab = null
        movieDetailWatchlistFab = null
        movieDetailRateFab = null
        movieDetailReloadActionFab = null
        movieDetailActionFab = null
        movieDetailActionsLoadingView = null
        super.onDestroyView()
    }

    private fun withActionsViewModel(action: MovieDetailsActionViewModel.() -> Unit) =
        withViewModel<MovieDetailsActionViewModel>(viewModelFactory) { action() }


    private fun setupViews(view: View) {
        detailsContent = view.findViewById(R.id.detailsContent)
        movieDetailFavoritesFab = view.findViewById(R.id.movieDetailFavoritesFab)
        movieDetailWatchlistFab = view.findViewById(R.id.movieDetailWatchlistFab)
        movieDetailRateFab = view.findViewById(R.id.movieDetailRateFab)
        movieDetailReloadActionFab = view.findViewById(R.id.movieDetailReloadActionFab)
        movieDetailActionFab = view.findViewById(R.id.movieDetailActionFab)
        movieDetailActionsLoadingView = view.findViewById(R.id.movieDetailActionsLoadingView)

        viewBinding.movieDetailActionFab.setOnClickListener { withActionsViewModel { onMainActionSelected() } }
        viewBinding.movieDetailFavoritesFab.setOnClickListener { withActionsViewModel { onFavoriteStateChanged() } }
        viewBinding.movieDetailWatchlistFab.setOnClickListener { withActionsViewModel { onWatchlistStateChanged() } }
        viewBinding.detailCreditsSelectionView?.setOnClickListener { viewModel.onMovieCreditsSelected() }
        viewBinding.movieDetailContent?.detailCreditsSelectionView?.setOnClickListener { viewModel.onMovieCreditsSelected() }
        viewBinding.movieDetailRateFab.setOnClickListener { viewModel.onRateMovieSelected() }
        viewBinding.movieDetailReloadActionFab.setOnClickListener { withActionsViewModel { onRetry() } }
    }

    private fun renderViewState(viewState: MovieDetailViewState) {
        setScreenTitle(viewState.screenTitle)

        viewBinding.viewState = viewState
        viewBinding.executePendingBindings()

        // horizontal
        viewBinding.detailGenresRv?.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        viewBinding.detailGenresRv?.adapter =
            MovieDetailsGenreAdapter(viewState.contentViewState.genres)

        // vertical
        viewBinding.movieDetailContent?.detailGenresRv?.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        viewBinding.movieDetailContent?.detailGenresRv?.adapter =
            MovieDetailsGenreAdapter(viewState.contentViewState.genres)
    }


    private fun renderActionViewState(actionViewState: MovieDetailActionViewState) {
        when (actionViewState) {
            is MovieDetailActionViewState.ShowLoading -> renderLoadingActions()
            is MovieDetailActionViewState.ShowReloadState -> {
                disableActions()
                movieDetailReloadActionFab?.setVisible()
                movieDetailActionFab?.setInvisible()
                view?.let { snackBarNoAction(it, R.string.unexpected_action_error) }
            }
            is MovieDetailActionViewState.ShowNoMovieState -> renderVisibleActions()
            is MovieDetailActionViewState.ShowMovieState -> {
                movieDetailReloadActionFab?.setInvisible()
                movieDetailActionFab?.setVisible()
                renderMovieState(actionViewState)
            }
            is MovieDetailActionViewState.ShowUserNotLogged -> {
                detailsContent?.let {
                    snackBar(
                        it,
                        R.string.account_need_to_login,
                        R.string.login_generic
                    ) {
                        viewModel.onUserRequestedLogin()
                    }
                }
            }
        }

        if (actionViewState.animate) {
            when (actionViewState.expanded) {
                true -> renderExpandedActions()
                false -> renderClosedActions()
            }
        }
    }

    private fun renderExpandedActions() {
        movieDetailActionFab?.animate()
            ?.rotation(180F)
        movieDetailFavoritesFab?.animate()
            ?.translationY(resources.getDimension(R.dimen.standard_55))
            ?.alpha(1F)
        movieDetailWatchlistFab?.animate()
            ?.translationY(resources.getDimension(R.dimen.standard_105))
            ?.alpha(1F)
        movieDetailRateFab?.animate()
            ?.translationY(resources.getDimension(R.dimen.standard_155))
            ?.alpha(1F)
    }

    private fun renderClosedActions() {
        movieDetailActionFab?.animate()?.rotation(0F)
        movieDetailFavoritesFab?.animate()?.translationY(0F)?.alpha(0F)
        movieDetailWatchlistFab?.animate()?.translationY(0F)?.alpha(0F)
        movieDetailRateFab?.animate()?.translationY(0F)?.alpha(0F)
    }

    private fun renderVisibleActions() {
        movieDetailActionsLoadingView?.setInvisible()

        movieDetailFavoritesFab?.setVisible()
        movieDetailWatchlistFab?.setVisible()
        movieDetailRateFab?.setVisible()
        movieDetailActionFab?.setVisible()
    }

    private fun renderLoadingActions() {
        movieDetailActionFab?.setInvisible()
        movieDetailFavoritesFab?.setInvisible()
        movieDetailWatchlistFab?.setInvisible()
        movieDetailRateFab?.setInvisible()
        movieDetailReloadActionFab?.setInvisible()

        movieDetailActionsLoadingView?.setVisible()
    }

    private fun disableActions() {
        movieDetailFavoritesFab?.asNonClickable()
        movieDetailWatchlistFab?.asNonClickable()
        movieDetailRateFab?.asNonClickable()
    }

    private fun renderMovieState(movieState: MovieDetailActionViewState.ShowMovieState) {
        when (movieState.favorite) {
            is ActionButtonState.ShowAsEmpty -> {
                movieDetailFavoritesFab?.asClickable()
                movieDetailFavoritesFab?.asEmpty()
            }
            is ActionButtonState.ShowAsFilled -> {
                movieDetailFavoritesFab?.asClickable()
                movieDetailFavoritesFab?.asFilled()
            }
            is ActionButtonState.ShowAsLoading -> {
                movieDetailFavoritesFab?.asNonClickable()
                movieDetailFavoritesFab?.doAnimation()
            }
        }

        when (movieState.isInWatchlist) {
            is ActionButtonState.ShowAsEmpty -> {
                movieDetailWatchlistFab?.asClickable()
                movieDetailWatchlistFab?.asEmpty()
            }
            is ActionButtonState.ShowAsFilled -> {
                movieDetailWatchlistFab?.asClickable()
                movieDetailWatchlistFab?.asFilled()
            }
            is ActionButtonState.ShowAsLoading -> {
                movieDetailWatchlistFab?.asNonClickable()
                movieDetailWatchlistFab?.doAnimation()
            }
        }

        when (movieState.isRated) {
            true -> movieDetailRateFab?.asFilled()
            false -> movieDetailRateFab?.asEmpty()
        }.also {
            movieDetailRateFab?.asClickable()
        }

        movieDetailActionsLoadingView?.setInvisible()

        movieDetailActionFab?.setVisible()
        movieDetailFavoritesFab?.setVisible()
        movieDetailWatchlistFab?.setVisible()
        movieDetailRateFab?.setVisible()
    }
}
