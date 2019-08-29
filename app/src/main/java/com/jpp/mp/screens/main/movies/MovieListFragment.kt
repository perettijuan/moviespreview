package com.jpp.mp.screens.main.movies

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.R
import com.jpp.mp.common.extensions.getScreenWidthInPixels
import com.jpp.mp.common.extensions.withNavigationViewModel
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mp.common.navigation.Destination
import com.jpp.mp.databinding.ListItemMovieBinding
import com.jpp.mp.screens.main.movies.MovieListFragment.MoviesAdapter
import com.jpp.mpdesign.ext.findViewInPositionWithId
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_movie_list.*
import kotlinx.android.synthetic.main.list_item_movie.view.*
import javax.inject.Inject

/**
 * Base fragment used to show the list of movies that are present in a particular section.
 * The application can show movies in four different sections:
 * - Playing
 * - Popular
 * - Upcoming
 * - TopRated
 *
 * This Fragment is the basic glue to render the view state provided by the ViewModel. There is an
 * implementation of this Fragment per each section listed before. This Fragment contains the base
 * code to update the UI and the child classes are providing the initialization method over the
 * SINGLE [MovieListViewModel] instance used.
 *
 * It is important the highlight made in the previous section: there's a single [MovieListViewModel]
 * instance that is shared between the instances of the Fragment. The single instance is provided
 * by the framework (The ViewModelProvider plus the Factory) and the decision of using a single
 * VM instead of a VM per Fragment is based only in the simplification over the complication that
 * could represent having a hierarchy of VM to provide the functionality to this view.
 *
 * Another important aspect of this Fragment is that it doesn't uses Data Binding to render the view
 * state ([MoviesAdapter] on the other hand is using DB). This is because there is an issue with the
 * approach taken in which the state of the views is not updated immediately when the VM performs an
 * action. I honestly didn't have the time to verify if this is an issue in my approach or there's a
 * deeper reason for it. But I think that the approach taken in this Fragment is pretty similar to
 * using DB, since the ViewState rendering code is entirely declarative and it has no imperative code.
 * Take a look to [onActivityCreated] to have a clear understanding.
 */
abstract class MovieListFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_movie_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        movieList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MoviesAdapter { item, position -> withViewModel { onMovieSelected(item, position) } }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        withViewModel {
            viewStates.observe(viewLifecycleOwner, Observer { viewState ->
                movieListErrorView.visibility = viewState.errorViewState.visibility
                movieListErrorView.asConnectivity(viewState.errorViewState.isConnectivity)
                movieListErrorView.onRetry(viewState.errorViewState.errorHandler)

                moviesLoadingView.visibility = viewState.loadingVisibility

                movieList.visibility = viewState.contentViewState.visibility
                withRecyclerViewAdapter { submitList(viewState.contentViewState.movieList) }
            })

            screenTitle.observe(viewLifecycleOwner, Observer { sectionTitle ->
                withNavigationViewModel(viewModelFactory) {
                    destinationReached(Destination.MovieListReached(getString(sectionTitle.titleRes)))
                }
            })

            navEvents.observe(viewLifecycleOwner, Observer { event ->
                with(event) {
                    val view = movieList.findViewInPositionWithId(positionInList, R.id.movieItemImage)
                    withNavigationViewModel(viewModelFactory) { navigateToMovieDetails(movieId, movieImageUrl, movieTitle, view) }
                }
            })

            initViewModel(
                    getScreenWidthInPixels(),
                    getScreenWidthInPixels(),
                    this)
        }
    }

    abstract fun initViewModel(posterSize: Int, backdropSize: Int, vm: MovieListViewModel)
    private fun withViewModel(action: MovieListViewModel.() -> Unit) = withViewModel<MovieListViewModel>(viewModelFactory) { action() }
    private fun withRecyclerViewAdapter(action: MoviesAdapter.() -> Unit) {
        (movieList.adapter as MoviesAdapter).action()
    }

    /**
     * Internal [PagedListAdapter] to render the list of movies. The fact that this class is a
     * [PagedListAdapter] indicates that the paging library is being used. Another important
     * aspect of this class is that it uses Data Binding to update the UI, which differs from the
     * containing class.
     */
    class MoviesAdapter(private val movieSelectionListener: (MovieListItem, Int) -> Unit) : PagedListAdapter<MovieListItem, MoviesAdapter.ViewHolder>(MovieDiffCallback()) {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                    DataBindingUtil.inflate(
                            LayoutInflater.from(parent.context),
                            R.layout.list_item_movie,
                            parent,
                            false
                    )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            getItem(position)?.let {
                holder.bindMovie(it, movieSelectionListener)
            }
        }

        class ViewHolder(private val itemBinding: ListItemMovieBinding) : RecyclerView.ViewHolder(itemBinding.root) {
            fun bindMovie(movieList: MovieListItem, movieSelectionListener: (MovieListItem, Int) -> Unit) {
                with(itemBinding) {
                    viewState = movieList
                    executePendingBindings()
                }
                with(itemView) {
                    movieItemImage.transitionName = "MovieImageAt$adapterPosition"
                    setOnClickListener { movieSelectionListener(movieList, adapterPosition) }
                }
            }
        }
    }


    class MovieDiffCallback : DiffUtil.ItemCallback<MovieListItem>() {

        override fun areItemsTheSame(oldItem: MovieListItem, newItem: MovieListItem): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: MovieListItem, newItem: MovieListItem): Boolean {
            return oldItem.title == newItem.title
        }
    }
}