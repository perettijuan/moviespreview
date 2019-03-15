package com.jpp.moviespreview.screens.main.details

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.moviespreview.R
import com.jpp.moviespreview.ext.*
import com.jpp.moviespreview.screens.main.RefreshAppViewModel
import com.jpp.moviespreview.screens.main.details.MovieDetailsFragmentArgs.fromBundle
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.list_item_details_genre.view.*
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.fragment_details_content.*
import javax.inject.Inject

/**
 * Fragment that shows the details of a given movie.
 * Note that this Fragment renders only the content of the details, not the image provided.
 * The ImageView that shows the poster of the movie belongs to MainActivity.
 */
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val args = arguments
                ?: throw IllegalStateException("You need to pass arguments to MovieDetailsFragment in order to show the content")

        withViewModel {
            init(fromBundle(args).movieId.toDouble())

            viewState().observe(this@MovieDetailsFragment.viewLifecycleOwner, Observer { viewState ->
                when (viewState) {
                    is MovieDetailsViewState.Loading -> renderLoading()
                    MovieDetailsViewState.ErrorUnknown -> {
                        detailsErrorView.asUnknownError { retry() }
                        renderError()
                    }
                    is MovieDetailsViewState.ErrorNoConnectivity -> {
                        detailsErrorView.asNoConnectivityError { retry() }
                        renderError()
                    }
                    is MovieDetailsViewState.ShowDetail -> {
                        with(viewState.detail) {
                            detailsOverviewContentTxt.text = overview
                            detailsPopularityContentTxt.text = popularity.toString()
                            detailsVoteCountContentTxt.text = voteCount.toString()
                            detailsReleaseDateContentTxt.text = releaseDate
                            renderMovieGenres(genres)
                        }
                        renderContent()
                    }
                }
            })

            navEvents().observe(this@MovieDetailsFragment.viewLifecycleOwner, Observer { navEvent ->
                when (navEvent) {
                    is MovieDetailsNavigationEvent.ToCredits -> {
                        findNavController().navigate(
                                MovieDetailsFragmentDirections.actionMovieDetailsFragmentToCreditsFragment(
                                        navEvent.movieId.toString(),
                                        navEvent.movieTitle
                                )
                        )
                    }
                }
            })
        }

        /*
         * Get notified if the app being shown to the user needs to be refreshed for some reason
         * and do it.
         */
        withRefreshAppViewModel {
            refreshState().observe(this@MovieDetailsFragment.viewLifecycleOwner, Observer {
                if (it) {
                    withViewModel {
                        refresh(fromBundle(args).movieId.toDouble())
                    }
                }
            })
        }

        detailsCreditsSelectionView.setOnClickListener {
            withViewModel {
                onCreditsSelected(fromBundle(args).movieId.toDouble(), fromBundle(args).movieTitle)
            }
        }
    }


    /**
     * Helper function to execute actions with the [MovieDetailsViewModel].
     */
    private fun withViewModel(action: MovieDetailsViewModel.() -> Unit) = withViewModel<MovieDetailsViewModel>(viewModelFactory) { action() }

    /**
     * Helper function to execute actions with [RefreshAppViewModel] backed by the MainActivity.
     */
    private fun withRefreshAppViewModel(action: RefreshAppViewModel.() -> Unit) = withViewModel<RefreshAppViewModel>(viewModelFactory) { action() }

    private fun renderLoading() {
        detailsErrorView.setInvisible()
        fragmentDetailsContent.setInvisible()

        detailsLoadingView.setVisible()
    }

    private fun renderError() {
        detailsLoadingView.setInvisible()
        fragmentDetailsContent.setInvisible()

        detailsErrorView.setVisible()
    }

    private fun renderContent() {
        detailsLoadingView.setInvisible()
        detailsErrorView.setInvisible()

        fragmentDetailsContent.setVisible()
    }


    private fun renderMovieGenres(genres: List<MovieGenreItem>) {
        val layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        detailsGenresRv.layoutManager = layoutManager
        detailsGenresRv.adapter = MovieDetailsGenreAdapter(genres)
    }

    class MovieDetailsGenreAdapter(private val genres: List<MovieGenreItem>) : RecyclerView.Adapter<MovieDetailsGenreAdapter.ViewHolder>() {


        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(genres[position])

        override fun getItemCount() = genres.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.list_item_details_genre))


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