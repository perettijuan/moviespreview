package com.jpp.mp.screens.main.credits

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mp.R
import com.jpp.mp.ext.inflate
import com.jpp.mp.ext.loadImageUrlAsCircular
import com.jpp.mp.ext.setInvisible
import com.jpp.mp.ext.setVisible
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_credits_deprecated.*
import kotlinx.android.synthetic.main.list_item_credits_deprecated.view.*
import javax.inject.Inject

//TODO JPP delete ME
class CreditsFragmentDeprecated : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_credits_deprecated, container, false)
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

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent.inflate(R.layout.list_item_credits_deprecated))
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