package com.jpp.mpmoviedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mp.common.fragments.MPFragment
import com.jpp.mpdesign.ext.setInvisible
import com.jpp.mpdesign.ext.setVisible
import com.jpp.mpdesign.ext.snackBar
import com.jpp.mpdesign.ext.snackBarNoAction
import com.jpp.mpmoviedetails.NavigationMovieDetails.movieId
import com.jpp.mpmoviedetails.databinding.FragmentMovieDetailsBinding
import com.jpp.mpmoviedetails.databinding.ListItemMovieDetailGenreBinding
import kotlinx.android.synthetic.main.fragment_movie_details.*
import kotlinx.android.synthetic.main.layout_movie_detail_content.*

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
class MovieDetailsFragment : MPFragment<MovieDetailsViewModel>() {

    private lateinit var viewBinding: FragmentMovieDetailsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_details, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        withViewModel {
            viewState.observe(viewLifecycleOwner, Observer { viewState ->
                viewBinding.viewState = viewState
                viewBinding.executePendingBindings()

                detailGenresRv.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
                detailGenresRv.adapter = MovieDetailsGenreAdapter(viewState.contentViewState.genres)
            })

            onInit(MovieDetailsParam.fromArguments(arguments))
        }

        withActionsViewModel {
            viewState.observe(viewLifecycleOwner, Observer { viewState -> renderActionViewState(viewState) })
            onInit(movieId(arguments).toDouble())
        }

        movieDetailActionFab.setOnClickListener { withActionsViewModel { onMainActionSelected() } }
        movieDetailFavoritesFab.setOnClickListener { withActionsViewModel { onFavoriteStateChanged() } }
        movieDetailWatchlistFab.setOnClickListener { withActionsViewModel { onWatchlistStateChanged() } }
        detailCreditsSelectionView.setOnClickListener { withViewModel { onMovieCreditsSelected() } }
    }


    override fun withViewModel(action: MovieDetailsViewModel.() -> Unit) = withViewModel<MovieDetailsViewModel>(viewModelFactory) { action() }

    private fun withActionsViewModel(action: MovieDetailsActionViewModel.() -> Unit) = withViewModel<MovieDetailsActionViewModel>(viewModelFactory) { action() }


    private fun renderActionViewState(actionViewState: MovieDetailActionViewState) {
        when (actionViewState) {
            is MovieDetailActionViewState.ShowLoading -> renderLoadingActions()
            is MovieDetailActionViewState.ShowError -> snackBarNoAction(detailsContent, R.string.unexpected_action_error)
            is MovieDetailActionViewState.ShowMovieState -> renderMovieState(actionViewState)
            is MovieDetailActionViewState.ShowNoMovieState -> renderVisibleActions()
            is MovieDetailActionViewState.ShowUserNotLogged -> snackBar(detailsContent, R.string.account_need_to_login, R.string.login_generic) {
                withViewModel { onUserRequestedLogin() }
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
        movieDetailActionFab.animate().rotation(180F)
        movieDetailFavoritesFab.animate().translationY(resources.getDimension(R.dimen.standard_55)).alpha(1F)
        movieDetailWatchlistFab.animate().translationY(resources.getDimension(R.dimen.standard_105)).alpha(1F)
        movieDetailRateFab.animate().translationY(resources.getDimension(R.dimen.standard_155)).alpha(1F)
    }

    private fun renderClosedActions() {
        movieDetailActionFab.animate().rotation(0F)
        movieDetailFavoritesFab.animate().translationY(0F).alpha(0F)
        movieDetailWatchlistFab.animate().translationY(0F).alpha(0F)
        movieDetailRateFab.animate().translationY(0F).alpha(0F)
    }

    private fun renderVisibleActions() {
        movieDetailActionsLoadingView.setInvisible()

        movieDetailFavoritesFab.setVisible()
        movieDetailWatchlistFab.setVisible()
        movieDetailRateFab.setVisible()
        movieDetailActionFab.setVisible()
    }

    private fun renderLoadingActions() {
        movieDetailActionFab.setInvisible()
        movieDetailFavoritesFab.setInvisible()
        movieDetailWatchlistFab.setInvisible()
        movieDetailRateFab.setInvisible()

        movieDetailActionsLoadingView.setVisible()
    }

    private fun renderMovieState(movieState: MovieDetailActionViewState.ShowMovieState) {
        when (movieState.favorite) {
            is ActionButtonState.ShowAsEmpty -> {
                movieDetailFavoritesFab.asClickable()
                movieDetailFavoritesFab.asEmpty()
            }
            is ActionButtonState.ShowAsFilled -> {
                movieDetailFavoritesFab.asClickable()
                movieDetailFavoritesFab.asFilled()
            }
            is ActionButtonState.ShowAsLoading -> {
                movieDetailFavoritesFab.asNonClickable()
                movieDetailFavoritesFab.doAnimation()
            }
        }

        when (movieState.isInWatchlist) {
            is ActionButtonState.ShowAsEmpty -> {
                movieDetailWatchlistFab.asClickable()
                movieDetailWatchlistFab.asEmpty()
            }
            is ActionButtonState.ShowAsFilled -> {
                movieDetailWatchlistFab.asClickable()
                movieDetailWatchlistFab.asFilled()
            }
            is ActionButtonState.ShowAsLoading -> {
                movieDetailWatchlistFab.asNonClickable()
                movieDetailWatchlistFab.doAnimation()
            }
        }

        when (movieState.isRated) {
            true -> movieDetailRateFab.asFilled()
            false -> movieDetailRateFab.asEmpty()
        }.also {
            movieDetailRateFab.asClickable()
        }


        movieDetailActionsLoadingView.setInvisible()

        movieDetailActionFab.setVisible()
        movieDetailFavoritesFab.setVisible()
        movieDetailWatchlistFab.setVisible()
        movieDetailRateFab.setVisible()
    }

    class MovieDetailsGenreAdapter(private val genres: List<MovieGenreItem>) : RecyclerView.Adapter<MovieDetailsGenreAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                    DataBindingUtil.inflate(
                            LayoutInflater.from(parent.context),
                            R.layout.list_item_movie_detail_genre,
                            parent,
                            false
                    )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(genres[position])

        override fun getItemCount() = genres.size


        class ViewHolder(private val itemBinding: ListItemMovieDetailGenreBinding) : RecyclerView.ViewHolder(itemBinding.root) {

            fun bind(genre: MovieGenreItem) {
                itemBinding.viewState = genre
                itemBinding.executePendingBindings()
            }

        }
    }
}