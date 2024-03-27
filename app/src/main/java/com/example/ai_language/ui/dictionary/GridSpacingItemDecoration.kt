package com.example.ai_language.ui.dictionary

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // 아이템 위치
        val column = position % spanCount // 아이템 열

        if (includeEdge) {
            outRect.left = spacing * (spanCount - column) / spanCount // 왼쪽 여백
            outRect.right = spacing * (column + 1) / spanCount // 오른쪽 여백

            if (position < spanCount) { // 첫 번째 행의 상단 여백
                outRect.top = spacing
            }
            outRect.bottom = spacing // 아이템 하단 여백
        } else {
            outRect.left = spacing * column / spanCount // 왼쪽 여백
            outRect.right = spacing - (column + 1) * spacing / spanCount // 오른쪽 여백
            if (position >= spanCount) {
                outRect.top = spacing // 아이템 상단 여백
            }
        }
    }
}
