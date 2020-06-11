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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.common.viewmodel.MPGenericSavedStateViewModelFactory
import com.jpp.mpcredits.databinding.CreditsFragmentBinding
import com.jpp.mpcredits.databinding.ListItemCreditsBinding
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

    private lateinit var viewBinding: CreditsFragmentBinding

    private val viewModel: CreditsViewModel by viewModels {
        MPGenericSavedStateViewModelFactory(
            creditsViewModelFactory,
            this
        )
    }

    // used to restore the position of the RecyclerView on view re-creation
    private var rvState: Parcelable? = null

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.credits_fragment, container, false)
        return viewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rvState = savedInstanceState?.getParcelable(CREDITS_RV_STATE_KEY) ?: rvState


            viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
                viewBinding.viewState = viewState
                viewBinding.executePendingBindings()

                withRecyclerView {
                    layoutManager = LinearLayoutManager(context)
                    adapter = CreditsAdapter(viewState.creditsViewState.creditItems) { viewModel.onCreditItemSelected(it) }
                    layoutManager?.onRestoreInstanceState(rvState)
                    addItemDecoration(DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation))
                }
            })
            viewModel.onInit(CreditsInitParam.create(this@CreditsFragment))
    }

    override fun onPause() {
        withRecyclerView { rvState = layoutManager?.onSaveInstanceState() }
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        withRecyclerView { outState.putParcelable(CREDITS_RV_STATE_KEY, layoutManager?.onSaveInstanceState()) }
        super.onSaveInstanceState(outState)
    }

    private fun withRecyclerView(action: RecyclerView.() -> Unit) = view?.findViewById<RecyclerView>(R.id.creditsRv)?.let(action)

    /**
     * Internal [RecyclerView.Adapter] to render the credit list.
     */
    class CreditsAdapter(private val credits: List<CreditPerson>, private val selectionListener: (CreditPerson) -> Unit) : RecyclerView.Adapter<CreditsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                    DataBindingUtil.inflate(
                            LayoutInflater.from(parent.context),
                            R.layout.list_item_credits,
                            parent,
                            false
                    )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bindCredit(credits[position], selectionListener)
        }

        override fun getItemCount(): Int = credits.size

        class ViewHolder(private val itemBinding: ListItemCreditsBinding) : RecyclerView.ViewHolder(itemBinding.root) {
            fun bindCredit(credit: CreditPerson, selectionListener: (CreditPerson) -> Unit) {
                itemBinding.viewState = credit
                itemBinding.executePendingBindings()
                itemView.setOnClickListener { selectionListener(credit) }
            }
        }
    }

    private companion object {
        const val CREDITS_RV_STATE_KEY = "creditsRvStateKey"
    }
}
