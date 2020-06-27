package com.jpp.mpcredits

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.common.extensions.observeValue
import com.jpp.mp.common.extensions.setScreenTitle
import com.jpp.mp.common.viewmodel.MPGenericSavedStateViewModelFactory
import com.jpp.mpcredits.databinding.CreditsFragmentBinding
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Fragment used to show the list of credits that belongs to a particular movie selected by the user.
 *
 * When instantiated, this fragment invokes the [CreditsViewModel] methods in order to retrieve
 * and show the credits of the movie that has been selected by the user. The VM will perform the
 * fetch and will update the UI states represented by [CreditsViewState] and this Fragment will
 * render those updates.
 *
 * Pre-condition: in order to instantiate this Fragment, a movie ID must be provided in the arguments
 * of the Fragment.
 */
class CreditsFragment : Fragment() {

    @Inject
    lateinit var creditsViewModelFactory: CreditsViewModelFactory

    private var viewBinding: CreditsFragmentBinding? = null

    private val viewModel: CreditsViewModel by viewModels {
        MPGenericSavedStateViewModelFactory(
            creditsViewModelFactory,
            this
        )
    }

    private var movieCreditsRv: RecyclerView? = null

    // used to restore the position of the RecyclerView on view re-creation
    // TODO we can simplify this once RecyclerView 1.2.0 is released
    //  ==> https://medium.com/androiddevelopers/restore-recyclerview-scroll-position-a8fbdc9a9334
    private var rvState: Parcelable? = null

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.credits_fragment, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)

        rvState = savedInstanceState?.getParcelable(CREDITS_RV_STATE_KEY) ?: rvState

        viewModel.viewState.observeValue(viewLifecycleOwner, ::renderViewState)
        viewModel.onInit(CreditsInitParam.create(this@CreditsFragment))
    }

    override fun onDestroyView() {
        viewBinding = null
        movieCreditsRv = null
        super.onDestroyView()
    }

    override fun onPause() {
        rvState = movieCreditsRv?.layoutManager?.onSaveInstanceState()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            CREDITS_RV_STATE_KEY,
            movieCreditsRv?.layoutManager?.onSaveInstanceState()
        )
        super.onSaveInstanceState(outState)
    }

    private fun setupViews(view: View) {
        movieCreditsRv = view.findViewById<RecyclerView>(R.id.creditsRv).apply {
            layoutManager = LinearLayoutManager(context)
            layoutManager?.onRestoreInstanceState(rvState)
            addItemDecoration(
                DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation)
            )
        }
    }

    private fun renderViewState(viewState: CreditsViewState) {
        setScreenTitle(viewState.screenTitle)
        movieCreditsRv?.adapter = CreditsAdapter(viewState.creditsViewState.creditItems) {
            viewModel.onCreditItemSelected(it)
        }
        viewBinding?.viewState = viewState
        viewBinding?.executePendingBindings()
    }

    private companion object {
        const val CREDITS_RV_STATE_KEY = "creditsRvStateKey"
    }
}
