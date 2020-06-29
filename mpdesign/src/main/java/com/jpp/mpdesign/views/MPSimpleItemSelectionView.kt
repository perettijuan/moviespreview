package com.jpp.mpdesign.views

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.jpp.mpdesign.R

/**
 * A custom [ConstraintLayout] that shows a text view at the start margin and a chevron image
 * view at the end margin.
 */
class MPSimpleItemSelectionView : ConstraintLayout {

    private var itemSelectionViewTitle: TextView? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)

        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.MPSimpleItemSelectionView)
        val titleText = typedArray.getText(R.styleable.MPSimpleItemSelectionView_itemText)
        itemSelectionViewTitle?.text = titleText
        typedArray.recycle()
    }

    private fun init(context: Context) {
        inflate(context, R.layout.layout_item_selection, this)
        isClickable = true
        itemSelectionViewTitle = findViewById(R.id.itemSelectionViewTitle)
    }

    fun itemText(@StringRes text: Int) {
        itemSelectionViewTitle?.setText(text)
    }
}
