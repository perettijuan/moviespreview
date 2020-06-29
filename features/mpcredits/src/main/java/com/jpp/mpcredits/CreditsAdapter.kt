package com.jpp.mpcredits

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mpcredits.databinding.ListItemCreditsBinding

/**
 * [RecyclerView.Adapter] to render the credit list.
 */
internal class CreditsAdapter(
    private val credits: List<CreditPerson>,
    private val selectionListener: (CreditPerson) -> Unit
) : RecyclerView.Adapter<CreditsAdapter.ViewHolder>() {

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

    class ViewHolder(private val itemBinding: ListItemCreditsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindCredit(credit: CreditPerson, selectionListener: (CreditPerson) -> Unit) {
            itemBinding.viewState = credit
            itemBinding.executePendingBindings()
            itemView.setOnClickListener { selectionListener(credit) }
        }
    }
}
