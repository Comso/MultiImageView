package com.cosmo.common.multiimageview

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 *Author:ruchao.jiang
 *Created: 2018/8/14 09:53
 *Email:ruchao.jiang@uama.com.cn
 */
class SpacesItemDecoration(
        private val  spanCount:Int,
        private val  spacing:Int,
        private val includeEdge: Boolean)
    : RecyclerView.ItemDecoration(){
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount
        when(includeEdge){
            true->{
                outRect.left = spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)
                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            }
            else->{
                outRect.left = column * spacing / spanCount// column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount// spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing // item top
                }
            }
        }
    }
}