package com.projectmaterial.videos.recyclerview;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class GridLayoutItemDecoration extends RecyclerView.ItemDecoration {
    private int spanCount;
    private int spacingItems;
    private int mSpacingEdgeBottom;
    private int mSpacingEdgeStart;
    private int mSpacingEdgeEnd;
    private int mSpacingEdgeTop;
    private boolean includeEdge;

    public GridLayoutItemDecoration(int spanCount, int spacingItems, int spacingEdgeBottom, int spacingEdgeStart, int spacingEdgeEnd, int spacingEdgeTop, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacingItems = spacingItems;
        mSpacingEdgeBottom = spacingEdgeBottom;
        mSpacingEdgeStart = spacingEdgeStart;
        mSpacingEdgeEnd = spacingEdgeEnd;
        mSpacingEdgeTop = spacingEdgeTop;
        this.includeEdge = includeEdge;
    }
    
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int column = position % spanCount;
        if (includeEdge) {
            outRect.left = mSpacingEdgeStart - column * mSpacingEdgeStart / spanCount;
            outRect.right = (column + 1) * mSpacingEdgeEnd / spanCount;
            if (position < spanCount) {
                outRect.top = mSpacingEdgeTop;
            }
        } else {
            outRect.left = column * spacingItems / spanCount;
            outRect.right = spacingItems - (column + 1) * spacingItems / spanCount;
            if (position >= spanCount) {
                outRect.top = spacingItems;
            }
            if (position < spanCount) {
                outRect.top = mSpacingEdgeTop;
            }
            if (column == 0) {
                outRect.left = mSpacingEdgeStart;
            } else if (column == spanCount -1) {
                outRect.right = mSpacingEdgeEnd;
            }
        }
        
        int itemCount = parent.getAdapter().getItemCount();
        int rows = itemCount / spanCount + (itemCount % spanCount == 0 ? 0 : 1);
        int lastRowFirstPosition = (rows - 1) * spanCount;
        if (position >= lastRowFirstPosition) {
            outRect.bottom = mSpacingEdgeBottom;
        } else {
            outRect.bottom = 0;
        }
    }
}