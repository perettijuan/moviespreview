package com.jpp.moviespreview.views

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.jpp.moviespreview.R
import com.jpp.moviespreview.ext.getStringFromResources
import kotlinx.android.synthetic.main.layout_mp_error_view.view.*


/**
 * Custom [ConstraintLayout] implementation to wrap the error views that are shown in the application
 * when an error is detected.
 */
class MPErrorView : ConstraintLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        inflate(context, R.layout.layout_mp_error_view, this)
    }


    /**
     * Prepare the view to be shown as a generic unknown error.
     */
    fun asUnknownError(retryAction: () -> Unit) {
        setupWith(R.drawable.ic_alert_circle,
                R.string.error_unexpected_error_message,
                R.string.error_retry,
                retryAction)
    }

    /**
     * Prepare the view to be shown as an error detected because of a lack of
     * internet connectivity.
     */
    fun asNoConnectivityError(retryAction: () -> Unit) {
        setupWith(R.drawable.ic_cloud_off,
                R.string.error_no_network_connection_message,
                R.string.error_retry,
                retryAction)
    }


    private fun setupWith(@DrawableRes errorImageIcon: Int,
                          @StringRes errorTitle: Int,
                          @StringRes errorButton: Int,
                          retryAction: () -> Unit) {
        errorImageView.setImageResource(errorImageIcon)
        errorTitleTextView.apply { text = getStringFromResources(errorTitle) }
        errorActionButton.apply {
            text = getStringFromResources(errorButton)
            setOnClickListener { retryAction.invoke() }
        }
    }
}