package com.jpp.mpmoviedetails

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jpp.mp.common.extensions.observeValue
import com.jpp.mp.common.extensions.setScreenTitle
import com.jpp.mp.common.viewmodel.MPGenericSavedStateViewModelFactory
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
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)

        viewModel.viewState.observeValue(viewLifecycleOwner, ::renderViewState)
        viewModel.onInit(paramsFromBundle(arguments))

        actionsViewModel.viewState.observeValue(viewLifecycleOwner, ::renderActionViewState)
        actionsViewModel.onInit(movieId(arguments).toDouble())
    }

    override fun onDestroyView() {
        viewBinding = null
        detailsContent = null
        movieDetailFavoritesFab = null
        movieDetailWatchlistFab = null
        movieDetailRateFab = null
        movieDetailReloadActionFab = null
        movieDetailActionFab = null
        movieDetailActionsLoadingView = null
        super.onDestroyView()
    }

    private fun setupViews(view: View) {
        detailsContent = view.findViewById(R.id.detailsContent)
        movieDetailFavoritesFab = view.findViewById(R.id.movieDetailFavoritesFab)
        movieDetailWatchlistFab = view.findViewById(R.id.movieDetailWatchlistFab)
        movieDetailRateFab = view.findViewById(R.id.movieDetailRateFab)
        movieDetailReloadActionFab = view.findViewById(R.id.movieDetailReloadActionFab)
        movieDetailActionFab = view.findViewById(R.id.movieDetailActionFab)
        movieDetailActionsLoadingView = view.findViewById(R.id.movieDetailActionsLoadingView)

        viewBinding?.movieDetailActionFab?.setOnClickListener { actionsViewModel.onMainActionSelected() }
        viewBinding?.movieDetailFavoritesFab?.setOnClickListener { actionsViewModel.onFavoriteStateChanged() }
        viewBinding?.movieDetailWatchlistFab?.setOnClickListener { actionsViewModel.onWatchlistStateChanged() }
        viewBinding?.detailCreditsSelectionView?.setOnClickListener { viewModel.onMovieCreditsSelected() }
        viewBinding?.movieDetailContent?.detailCreditsSelectionView?.setOnClickListener { viewModel.onMovieCreditsSelected() }
        viewBinding?.movieDetailRateFab?.setOnClickListener { viewModel.onRateMovieSelected() }
        viewBinding?.movieDetailReloadActionFab?.setOnClickListener { actionsViewModel.onRetry() }
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

    @SuppressLint("RestrictedApi")
    private fun renderActionViewState(actionViewState: MovieDetailActionViewState) {
        movieDetailActionFab?.visibility = actionViewState.actionButtonVisibility
        movieDetailReloadActionFab?.visibility = actionViewState.reloadButtonVisibility
        movieDetailActionsLoadingView?.visibility = actionViewState.loadingVisibility

        movieDetailFavoritesFab?.apply {
            visibility = actionViewState.favoriteButtonState.visibility
            setAsClickable(actionViewState.favoriteButtonState.asClickable)
            setFilled(actionViewState.favoriteButtonState.asFilled)
            doAnimation(actionViewState.favoriteButtonState.animateLoading)
        }

        movieDetailWatchlistFab?.apply {
            visibility = actionViewState.watchListButtonState.visibility
            setAsClickable(actionViewState.watchListButtonState.asClickable)
            setFilled(actionViewState.watchListButtonState.asFilled)
            doAnimation(actionViewState.watchListButtonState.animateLoading)
        }

        movieDetailRateFab?.apply {
            visibility = actionViewState.rateButtonState.visibility
            setAsClickable(actionViewState.rateButtonState.asClickable)
            setFilled(actionViewState.rateButtonState.asFilled)
            doAnimation(actionViewState.rateButtonState.animateLoading)
        }

        if (actionViewState.animate) {
            when (actionViewState.expanded) {
                true -> renderExpandedActions()
                else -> renderClosedActions()
            }
        }

        when (actionViewState.errorState) {
            is ActionErrorViewState.UserNotLogged -> detailsContent?.let {
                snackBar(it, R.string.account_need_to_login, R.string.login_generic) {
                    viewModel.onUserRequestedLogin()
                }
            }
            is ActionErrorViewState.UnknownError -> {
                detailsContent?.let { snackBarNoAction(it, R.string.unexpected_action_error) }
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
}
