package com.jpp.mpdesign.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.jpp.mpdesign.R
import kotlinx.android.synthetic.main.layout_title_value_row.view.*

/**
 * Custom [ConstraintLayout] that shows two TextViews: one on the start margin and another one
 * at the end margin.
 */
class MPTitleValueRow : ConstraintLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MPTitleValueRow)
        try {
            setTitleText(typedArray.getText(R.styleable.MPTitleValueRow_titleText))
            setValueText(typedArray.getText(R.styleable.MPTitleValueRow_valueText))
        } finally {
            typedArray.recycle()
        }
    }

    private fun init(context: Context) {
        View.inflate(context, R.layout.layout_title_value_row, this)
    }

    fun setTitleText(title: CharSequence?) {
        columnTextViewTitle.text = title
    }

    fun setTitleText(@StringRes stringRes: Int) {
        setTitleText(context.getString(stringRes))
    }

    fun setValueText(value: CharSequence?) {
        columnTextViewValue.text = value
    }
}