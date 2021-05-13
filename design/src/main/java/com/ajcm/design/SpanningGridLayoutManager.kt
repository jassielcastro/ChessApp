package com.ajcm.design

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.ceil
import kotlin.math.roundToInt

class SpanningGridLayoutManager : GridLayoutManager {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes)

    constructor(context: Context, spanCount: Int) : super(context, spanCount)
    constructor(context: Context, spanCount: Int, orientation: Int, reverseLayout: Boolean) :
            super(context, spanCount, orientation, reverseLayout)

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return spanLayoutSize(super.generateDefaultLayoutParams())
    }

    override fun generateLayoutParams(c: Context?, attrs: AttributeSet?): RecyclerView.LayoutParams {
        return spanLayoutSize(super.generateLayoutParams(c, attrs))
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): RecyclerView.LayoutParams {
        return spanLayoutSize(super.generateLayoutParams(lp))
    }

    override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
        return super.checkLayoutParams(lp)
    }

    private fun spanLayoutSize(layoutParams: RecyclerView.LayoutParams): RecyclerView.LayoutParams {
        when (orientation) {
            HORIZONTAL -> layoutParams.width = (getHorizontalSpace() / ceil((itemCount / spanCount).toDouble())).roundToInt()
            VERTICAL -> layoutParams.height = (getVerticalSpace() / ceil((itemCount / spanCount).toDouble())).roundToInt()
        }
        return layoutParams
    }

    override fun canScrollVertically(): Boolean {
        return false
    }

    override fun canScrollHorizontally(): Boolean {
        return false
    }

    private fun getHorizontalSpace(): Int {
        return width - paddingRight - paddingLeft
    }

    private fun getVerticalSpace(): Int {
        return height - paddingBottom - paddingTop
    }

}