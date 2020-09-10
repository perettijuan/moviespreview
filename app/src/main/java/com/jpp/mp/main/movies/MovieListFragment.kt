package com.jpp.mp.main.movies

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.R
import com.jpp.mp.common.extensions.observeValue
import com.jpp.mp.common.extensions.setScreenTitle
import com.jpp.mp.common.paging.MPVerticalPagingHandler
import com.jpp.mp.common.viewmodel.MPGenericSavedStateViewModelFactory
import com.jpp.mp.databinding.FragmentMovieListBinding
import com.jpp.mpdomain.MovieSection
import dagger.android.support.AndroidSupportInjection
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
 */
abstract class MovieListFragment : Fragment() {

    @Inject
    lateinit var movieListViewModelFactory: MovieListViewModelFactory

    private var viewBinding: FragmentMovieListBinding? = null

    private val viewModel: MovieListViewModel by viewModels {
        MPGenericSavedStateViewModelFactory(
            movieListViewModelFactory,
            this
        )
    }

    private var movieListRv: RecyclerView? = null

    protected abstract val movieSection: MovieSection
    protected abstract val screenTitle: String

    // used to restore the position of the RecyclerView on view re-creation
    // TODO we can simplify this once RecyclerView 1.2.0 is released
    //  ==> https://medium.com/androiddevelopers/restore-recyclerview-scroll-position-a8fbdc9a9334
    private var rvState: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
        viewBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_movie_list,
            container,
            false
        )
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews(view)

        rvState = savedInstanceState?.getParcelable(MOVIES_RV_STATE_KEY) ?: rvState

        viewModel.viewState.observeValue(viewLifecycleOwner, ::renderViewState)
        viewModel.onInit(movieSection, screenTitle)
    }

    override fun onDestroyView() {
        viewBinding = null
        movieListRv = null
        super.onDestroyView()
    }

    override fun onPause() {
        rvState = movieListRv?.layoutManager?.onSaveInstanceState()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            MOVIES_RV_STATE_KEY,
            movieListRv?.layoutManager?.onSaveInstanceState()
        )
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.movie_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_menu -> {
                viewModel.onSearchOptionSelected()
                return true
            }
            R.id.about_menu -> {
                viewModel.onAboutOptionSelected()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpViews(view: View) {
        movieListRv = view.findViewById<RecyclerView>(R.id.movieList).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MoviesAdapter { item -> viewModel.onMovieSelected(item) }

            val pagingHandler = MPVerticalPagingHandler(layoutManager as LinearLayoutManager) {
                viewModel.onNextMoviePage()
            }
            addOnScrollListener(pagingHandler)
        }
    }

    private fun renderViewState(viewState: MovieListViewState) {
        setScreenTitle(viewState.screenTitle)
        viewBinding?.viewState = viewState
        (movieListRv?.adapter as MoviesAdapter).updateDataList(viewState.contentViewState.movieList)
        movieListRv?.layoutManager?.onRestoreInstanceState(rvState)
    }

    private companion object {
        const val MOVIES_RV_STATE_KEY = "moviesRvStateKey"
    }
}
