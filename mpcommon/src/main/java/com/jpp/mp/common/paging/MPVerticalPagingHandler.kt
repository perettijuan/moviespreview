package com.jpp.mp.common.paging

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Custom [RecyclerView.OnScrollListener] implementation to detect when a new page needs
 * to be loaded into the underlying [RecyclerView].
 */
class MPVerticalPagingHandler(
    private val layoutManager: LinearLayoutManager,
    private val nextPageItemThreshold: Int = 2,
    private val listener: (() -> Unit)?
) : RecyclerView.OnScrollListener() {

    private var previousTotal = 0
    private var loading = true
    private var firstVisibleItem = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

        if (dy > 0) {
            visibleItemCount = recyclerView.childCount
            totalItemCount = layoutManager.itemCount
            firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
        }

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false
                previousTotal = totalItemCount
            }
        }

        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + nextPageItemThreshold)) {
            listener?.invoke()
            loading = true
        }
    }
}
