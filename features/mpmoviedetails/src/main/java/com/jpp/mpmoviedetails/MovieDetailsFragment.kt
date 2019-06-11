package com.jpp.mpmoviedetails

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.jpp.mpdesign.ext.*
import com.jpp.mpmoviedetails.MovieDetailViewState.*
import com.jpp.mpmoviedetails.NavigationMovieDetails.imageUrl
import com.jpp.mpmoviedetails.NavigationMovieDetails.movieId
import com.jpp.mpmoviedetails.NavigationMovieDetails.title
import com.jpp.mpmoviedetails.NavigationMovieDetails.transition
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_movie_details.*
import kotlinx.android.synthetic.main.layout_movie_detail_content.*
import kotlinx.android.synthetic.main.list_item_movie_detail_genre.view.*
import javax.inject.Inject

/**
 * Fragment used to show the details of a particular movie selected by the user.
 */
class MovieDetailsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_movie_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(movieDetailImageView) {
            transitionName = transition(arguments)
            loadImageUrl(imageUrl(arguments))
        }

        withViewModel {
            viewStates.observe(this@MovieDetailsFragment.viewLifecycleOwner, Observer { it.actionIfNotHandled { viewState -> renderViewState(viewState) } })
            onInit(movieId(arguments).toDouble(), title(arguments))
        }

        withActionsViewModel {
            viewStates.observe(this@MovieDetailsFragment, Observer { it.actionIfNotHandled { viewState -> renderActionViewState(viewState) } })
            onInit(movieId(arguments).toDouble())
        }

        movieDetailActionFab.setOnClickListener { withActionsViewModel { onMainActionSelected() } }
        movieDetailFavoritesFab.setOnClickListener { withActionsViewModel { onFavoriteStateChanged() } }
    }

    /**
     * Helper function to execute actions with the [MovieDetailsViewModel].
     */
    private fun withViewModel(action: MovieDetailsViewModel.() -> Unit) = withViewModel<MovieDetailsViewModel>(viewModelFactory) { action() }

    private fun withActionsViewModel(action: MovieDetailsActionViewModel.() -> Unit) = withViewModel<MovieDetailsActionViewModel>(viewModelFactory) { action() }

    private fun renderViewState(viewState: MovieDetailViewState) {
        when (viewState) {
            is ShowLoading -> renderLoading()
            is ShowError -> renderUnknownError()
            is ShowNotConnected -> renderConnectivityError()
            is ShowDetail -> renderDetail(viewState)
        }
    }

    private fun renderDetail(detail: ShowDetail) {
        with(detail) {
            detailOverviewContentTxt.text = overview
            detailPopularityContentTxt.text = popularity
            detailVoteCountContentTxt.text = voteCount
            detailReleaseDateContentTxt.text = releaseDate

            val layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
            detailGenresRv.layoutManager = layoutManager
            detailGenresRv.adapter = MovieDetailsGenreAdapter(genres)
        }

        movieDetailErrorView.setInvisible()
        movieDetailLoadingView.setInvisible()
        movieDetailContent.setVisible()
    }

    private fun renderLoading() {
        movieDetailErrorView.setInvisible()
        movieDetailContent.setInvisible()
        movieDetailLoadingView.setVisible()
    }

    private fun renderUnknownError() {
        movieDetailContent.setInvisible()
        movieDetailLoadingView.setInvisible()

        movieDetailErrorView.asUnknownError { withViewModel { onRetry() } }
        movieDetailErrorView.setVisible()
    }

    private fun renderConnectivityError() {
        movieDetailContent.setInvisible()
        movieDetailLoadingView.setInvisible()

        movieDetailErrorView.asNoConnectivityError { withViewModel { onRetry() } }
        movieDetailErrorView.setVisible()
    }

    private fun renderActionViewState(actionViewState: MovieDetailActionViewState) {
        when (actionViewState) {
            is MovieDetailActionViewState.ShowLoading -> renderLoadingActions()
            is MovieDetailActionViewState.ShowError -> TODO()
            is MovieDetailActionViewState.ShowState -> renderActionsState(actionViewState)
        }


        if (actionViewState.animate) {
            when (actionViewState.open) {
                true -> renderExpandedActions()
                false -> renderClosedActions()
            }
        }
    }

    private fun renderExpandedActions() {
        movieDetailActionFab.animate().rotation(180F)
        movieDetailFavoritesFab.animate().translationY(resources.getDimension(R.dimen.standard_55)).alpha(1F)
        movieDetailWatchlistFab.animate().translationY(resources.getDimension(R.dimen.standard_105)).alpha(1F)
    }

    private fun renderClosedActions() {
        movieDetailActionFab.animate().rotation(0F)
        movieDetailFavoritesFab.animate().translationY(0F).alpha(0F)
        movieDetailWatchlistFab.animate().translationY(0F).alpha(0F)
    }

    private fun renderLoadingActions() {
        movieDetailActionFab.setInvisible()
        movieDetailFavoritesFab.setInvisible()
        movieDetailWatchlistFab.setInvisible()

        movieDetailActionsLoadingView.setVisible()
    }

    private fun renderActionsState(movieState: MovieDetailActionViewState.ShowState) {
        when(movieState.favorite) {
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


        movieDetailActionsLoadingView.setInvisible()

        movieDetailActionFab.setVisible()
        movieDetailFavoritesFab.setVisible()
        movieDetailWatchlistFab.setVisible()
    }

    class MovieDetailsGenreAdapter(private val genres: List<MovieGenreItem>) : RecyclerView.Adapter<MovieDetailsGenreAdapter.ViewHolder>() {


        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(genres[position])

        override fun getItemCount() = genres.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.list_item_movie_detail_genre))


        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            fun bind(genre: MovieGenreItem) {
                with(genre) {
                    itemView.genreListItemIv.setImageResource(icon)
                    itemView.genreListItemTxt.text = itemView.getStringFromResources(name)
                }
            }

        }
    }
}