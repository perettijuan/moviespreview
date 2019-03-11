package com.jpp.moviespreview.screens.main.credits

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.moviespreview.R
import com.jpp.moviespreview.ext.*
import com.jpp.moviespreview.screens.main.credits.CreditsFragmentArgs.fromBundle
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_credits.*
import kotlinx.android.synthetic.main.list_item_credits.view.*
import javax.inject.Inject

class CreditsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_credits, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val args = arguments
                ?: throw IllegalStateException("You need to pass arguments to MovieDetailsFragment in order to show the content")

        withViewModel {
            init(movieId = fromBundle(args).movieId.toDouble(),
                    targetImageSize = resources.getDimensionPixelSize(getResIdFromAttribute(R.attr.mpCreditItemImageSize)))

            viewState().observe(this@CreditsFragment.viewLifecycleOwner, Observer { viewState ->
                when (viewState) {
                    is CreditsViewState.Loading -> renderLoading()
                    is CreditsViewState.ErrorUnknown -> {
                        creditsErrorView.asUnknownError { retry() }
                        renderError()
                    }
                    is CreditsViewState.ErrorNoConnectivity -> {
                        creditsErrorView.asNoConnectivityError { retry() }
                        renderError()
                    }
                    is CreditsViewState.ShowCredits -> {
                        creditsRv.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = CreditsAdapter(viewState.credits) { onCreditItemSelected(it) }
                            addItemDecoration(DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation))
                        }
                        renderCredits()
                    }
                }
            })

            navEvents().observe(this@CreditsFragment.viewLifecycleOwner, Observer { navEvent ->
                when (navEvent) {
                    is CreditsNavigationEvent.ToPerson -> {
                        findNavController().navigate(
                                CreditsFragmentDirections.actionCreditsFragmentToPersonFragment(
                                        navEvent.personId,
                                        navEvent.personImageUrl,
                                        navEvent.personName
                                )
                        )
                    }
                }
            })
        }
    }

    /**
     * Helper function to execute actions with the [CreditsViewModel].
     */
    private fun withViewModel(action: CreditsViewModel.() -> Unit) {
        getViewModel<CreditsViewModel>(viewModelFactory).action()
    }

    private fun renderLoading() {
        creditsErrorView.setInvisible()
        creditsRv.setInvisible()

        creditsLoadingView.setVisible()
    }

    private fun renderError() {
        creditsRv.setInvisible()
        creditsLoadingView.setInvisible()

        creditsErrorView.setVisible()
    }

    private fun renderCredits() {
        creditsLoadingView.setInvisible()
        creditsErrorView.setInvisible()

        creditsRv.setVisible()
    }


    class CreditsAdapter(private val credits: List<CreditPerson>, private val selectionListener: (CreditPerson) -> Unit) : RecyclerView.Adapter<CreditsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_credits, parent, false))
        override fun getItemCount() = credits.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindData(credits[position], selectionListener)
        }


        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bindData(credit: CreditPerson, selectionListener: (CreditPerson) -> Unit) {
                with(itemView) {
                    creditsItemImageView.loadImageUrlAsCircular(credit.profilePath)
                    creditsItemTitle.text = credit.title
                    creditsItemSubTitle.text = credit.subTitle
                    setOnClickListener { selectionListener(credit) }
                }
            }
        }
    }
}