package com.jpp.moviespreview.screens.main.details

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.jpp.moviespreview.R
import com.jpp.moviespreview.ext.getStringFromResources
import com.jpp.moviespreview.ext.inflate
import com.jpp.moviespreview.ext.withViewModel
import com.jpp.moviespreview.screens.main.details.MovieDetailsFragmentArgs.fromBundle
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.list_item_details_genre.view.*
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.fragment_details_content.*
import javax.inject.Inject

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

        val args = arguments ?: throw IllegalStateException("You need to pass arguments to MovieDetailsFragment in order to show the content")


        withViewModel<MovieDetailsViewModel>(viewModelFactory) {
            init(fromBundle(args).movieId.toDouble())

            viewState().observe(this@MovieDetailsFragment.viewLifecycleOwner, Observer { viewState ->
                when (viewState) {
                    MovieDetailsViewState.Loading -> R.layout.fragment_details_loading
                    MovieDetailsViewState.ErrorUnknown -> {
                        detailsErrorView.asUnknownError { init(fromBundle(args).movieId.toDouble()) }
                        R.layout.fragment_details_error
                    }
                    MovieDetailsViewState.ErrorNoConnectivity -> {
                        detailsErrorView.asNoConnectivityError { init(fromBundle(args).movieId.toDouble()) }
                        R.layout.fragment_details_error
                    }
                    is MovieDetailsViewState.ShowDetail -> {
                        with(viewState.detail) {
                            detailsOverviewContentTxt.text = overview
                            detailsPopularityContentTxt.text = popularity.toString()
                            detailsVoteCountContentTxt.text = voteCount.toString()
                            detailsReleaseDateContentTxt.text = releaseDate
                            showMovieGenres(genres)
                        }
                        R.layout.fragment_details_final
                    }
                }.let {
                    val constraint = ConstraintSet()
                    constraint.clone(this@MovieDetailsFragment.context, it)
                    TransitionManager.beginDelayedTransition(fragmentDetailsRoot)
                    constraint.applyTo(fragmentDetailsRoot)
                }
            })
        }
    }


    private fun showMovieGenres(genres: List<MovieGenreItem>) {
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