package com.jpp.mp.views

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.jpp.mp.R
import kotlinx.android.synthetic.main.layout_item_selection.view.*

/**
 * A custom [ConstraintLayout] that shows a text view at the start margin and a chevron image
 * view at the end margin.
 */
class MPItemSelectionView : ConstraintLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MPItemSelectionView)
        val titleText = typedArray.getText(R.styleable.MPItemSelectionView_itemText)
        itemSelectionViewTitle.text = titleText
        typedArray.recycle()
    }

    private fun init(context: Context) {
        inflate(context, R.layout.layout_item_selection, this)
        isClickable = true
    }
}