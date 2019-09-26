package com.jpp.mp.main.movies

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.R
import com.jpp.mp.common.extensions.getScreenWidthInPixels
import com.jpp.mp.common.extensions.withViewModel
import com.jpp.mp.common.fragments.MPFragment
import com.jpp.mp.databinding.FragmentMovieListBinding
import com.jpp.mp.databinding.ListItemMovieBinding

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
 */
abstract class MovieListFragment : MPFragment<MovieListViewModel>() {

    private lateinit var viewBinding: FragmentMovieListBinding

    // used to restore the position of the RecyclerView on view re-creation
    private var rvState: Parcelable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movie_list, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        withRecyclerView {
            layoutManager = LinearLayoutManager(context)
            adapter = MoviesAdapter { item -> withViewModel { onMovieSelected(item) } }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rvState = savedInstanceState?.getParcelable(MOVIES_RV_STATE_KEY) ?: rvState

        withViewModel {
            viewState.observe(viewLifecycleOwner, Observer { viewState ->
                viewBinding.viewState = viewState

                withRecyclerView {
                    (adapter as MoviesAdapter).submitList(viewState.contentViewState.movieList)
                    layoutManager?.onRestoreInstanceState(rvState)
                }
            })

            initViewModel(
                    getScreenWidthInPixels(),
                    getScreenWidthInPixels(),
                    this)
        }
    }

    override fun onPause() {
        withRecyclerView { rvState = layoutManager?.onSaveInstanceState() }
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        withRecyclerView { outState.putParcelable(MOVIES_RV_STATE_KEY, layoutManager?.onSaveInstanceState()) }
        super.onSaveInstanceState(outState)
    }

    override fun withViewModel(action: MovieListViewModel.() -> Unit) = withViewModel<MovieListViewModel>(viewModelFactory) { action() }


    abstract fun initViewModel(posterSize: Int, backdropSize: Int, vm: MovieListViewModel)
    private fun withRecyclerView(action: RecyclerView.() -> Unit) = view?.findViewById<RecyclerView>(R.id.movieList)?.let(action)

    /**
     * Internal [PagedListAdapter] to render the list of movies. The fact that this class is a
     * [PagedListAdapter] indicates that the paging library is being used. Another important
     * aspect of this class is that it uses Data Binding to update the UI, which differs from the
     * containing class.
     */
    class MoviesAdapter(private val movieSelectionListener: (MovieListItem) -> Unit) : PagedListAdapter<MovieListItem, MoviesAdapter.ViewHolder>(MovieDiffCallback()) {


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
            fun bindMovie(movieList: MovieListItem, movieSelectionListener: (MovieListItem) -> Unit) {
                with(itemBinding) {
                    viewState = movieList
                    executePendingBindings()
                }
                itemView.setOnClickListener { movieSelectionListener(movieList) }
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

    private companion object {
        const val MOVIES_RV_STATE_KEY = "moviesRvStateKey"
    }
}