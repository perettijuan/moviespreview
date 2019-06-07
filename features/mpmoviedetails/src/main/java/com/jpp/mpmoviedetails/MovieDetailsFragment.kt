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
import com.jpp.mpmoviedetails.NavigationMovieDetails.imageUrl
import com.jpp.mpmoviedetails.NavigationMovieDetails.movieId
import com.jpp.mpmoviedetails.NavigationMovieDetails.title
import com.jpp.mpmoviedetails.NavigationMovieDetails.transition
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject
import com.jpp.mpmoviedetails.MovieDetailViewState.ShowLoading
import com.jpp.mpmoviedetails.MovieDetailViewState.ShowDetail
import com.jpp.mpmoviedetails.MovieDetailViewState.ShowError
import com.jpp.mpmoviedetails.MovieDetailViewState.ShowNotConnected
import kotlinx.android.synthetic.main.fragment_movie_details.*
import kotlinx.android.synthetic.main.layout_movie_detail_content.*
import kotlinx.android.synthetic.main.list_item_movie_detail_genre.view.*

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
    }

    /**
     * Helper function to execute actions with the [MovieDetailsViewModel].
     */
    private fun withViewModel(action: MovieDetailsViewModel.() -> Unit) = withViewModel<MovieDetailsViewModel>(viewModelFactory) { action() }

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