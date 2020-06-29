package com.jpp.mpabout.licenses

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jpp.mpabout.R
import com.jpp.mpdesign.ext.inflate
import com.jpp.mpdesign.ext.setTextAppearanceCompat

internal class LicensesAdapter(
    private val items: List<LicenseItem>,
    private val selectionListener: (LicenseItem) -> Unit
) : RecyclerView.Adapter<LicensesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent.inflate(android.R.layout.simple_list_item_1))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindLicense(items[position], selectionListener)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindLicense(license: LicenseItem, listener: (LicenseItem) -> Unit) {
            val textView = itemView.findViewById<TextView>(android.R.id.text1)
            textView.setTextAppearanceCompat(R.style.MPTextViewSmall)
            textView.text = license.name
            itemView.setOnClickListener { listener(license) }
        }
    }
}
