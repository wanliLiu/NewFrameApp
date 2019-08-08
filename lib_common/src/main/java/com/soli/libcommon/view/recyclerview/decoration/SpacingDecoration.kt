package com.taihe.libCommon.view.recyclerview.decoration

import android.graphics.Rect
import android.view.View

/**
 * Created by huangjun on 2016/12/9.
 */
class SpacingDecoration(hSpacing: Int, vSpacing: Int, includeEdge: Boolean) : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {

    private var mHorizontalSpacing = hSpacing
    private var mVerticalSpacing = vSpacing
    private var mIncludeEdge = includeEdge


    override fun getItemOffsets(outRect: Rect, view: View, parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        // Only handle the vertical situation
        val position = parent.getChildAdapterPosition(view)
        if (parent.layoutManager is androidx.recyclerview.widget.GridLayoutManager) {
            val layoutManager = parent.layoutManager as androidx.recyclerview.widget.GridLayoutManager?
            val spanCount = layoutManager!!.spanCount
            val column = position % spanCount
            getGridItemOffsets(outRect, position, column, spanCount)
        } else if (parent.layoutManager is androidx.recyclerview.widget.StaggeredGridLayoutManager) {
            val layoutManager = parent.layoutManager as androidx.recyclerview.widget.StaggeredGridLayoutManager?
            val spanCount = layoutManager!!.spanCount
            val lp = view.layoutParams as androidx.recyclerview.widget.StaggeredGridLayoutManager.LayoutParams
            val column = lp.spanIndex
            getGridItemOffsets(outRect, position, column, spanCount)
        } else if (parent.layoutManager is androidx.recyclerview.widget.LinearLayoutManager) {
            outRect.left = mHorizontalSpacing
            outRect.right = mHorizontalSpacing
            if (mIncludeEdge) {
                if (position == 0) {
                    outRect.top = mVerticalSpacing
                }
                outRect.bottom = mVerticalSpacing
            } else {
                if (position > 0) {
                    outRect.top = mVerticalSpacing
                }
            }
        }
    }

    private fun getGridItemOffsets(outRect: Rect, position: Int, column: Int, spanCount: Int) {
        if (mIncludeEdge) {
            outRect.left = mHorizontalSpacing * (spanCount - column) / spanCount
            outRect.right = mHorizontalSpacing * (column + 1) / spanCount
            if (position < spanCount) {
                outRect.top = mVerticalSpacing
            }
            outRect.bottom = mVerticalSpacing
        } else {
            outRect.left = mHorizontalSpacing * column / spanCount
            outRect.right = mHorizontalSpacing * (spanCount - 1 - column) / spanCount
            if (position >= spanCount) {
                outRect.top = mVerticalSpacing
            }
        }
    }
}
