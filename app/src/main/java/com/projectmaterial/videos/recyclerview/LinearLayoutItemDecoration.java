package com.projectmaterial.videos.recyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.projectmaterial.videos.R;

public class LinearLayoutItemDecoration extends RecyclerView.ItemDecoration {
    private final int margin16dp;
    private final int spacing8dp;

    public LinearLayoutItemDecoration(Context context) {
        margin16dp = context.getResources().getDimensionPixelSize(R.dimen.item_linear_spacing_edge_side);
        spacing8dp = context.getResources().getDimensionPixelSize(R.dimen.item_linear_spacing);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int itemCount = parent.getAdapter().getItemCount();

        // Add margins to the top and bottom of the RecyclerView
        if (position == 0) {
            outRect.top = spacing8dp;
        } else if (position == itemCount - 1) {
            outRect.bottom = margin16dp;
        }

        // Add margins to the left and right of the items
        outRect.left = margin16dp;
        outRect.right = margin16dp;

        // Add spacing between items
        if (position < itemCount - 1) {
            outRect.bottom = spacing8dp;
        }
    }
}