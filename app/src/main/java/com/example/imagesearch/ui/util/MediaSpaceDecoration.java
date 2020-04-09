package com.example.imagesearch.ui.util;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imagesearch.R;

import java.util.Arrays;
import java.util.List;

public class MediaSpaceDecoration extends RecyclerView.ItemDecoration {
    private final int spacing;
    private final List<Integer> allowedViewTypes = Arrays.asList(
    //        R.layout.item_image,
    //        R.layout.item_blur
    );

    public MediaSpaceDecoration(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect,
                               View view,
                               RecyclerView parent,
                               RecyclerView.State state) {
        final int position = parent.getChildAdapterPosition(view);
        /*if (!isMedia(parent, position)) {
            return;
        }*/

        final int totalSpanCount = getTotalSpanCount(parent);
        int spanSize = getItemSpanSize(parent, position);
        if (totalSpanCount == spanSize) {
            return;
        }

        outRect.top = isInTheFirstRow(position, totalSpanCount) ? 0 : spacing;
        outRect.left = isFirstInRow(position, totalSpanCount) ? 0 : spacing / 2;
        outRect.right = isLastInRow(position, totalSpanCount) ? 0 : spacing / 2;
        outRect.bottom = 0; // don't need
    }

    private boolean isInTheFirstRow(int position, int spanCount) {
        return position < spanCount;
    }

    private boolean isFirstInRow(int position, int spanCount) {
        return position % spanCount == 0;
    }

    private boolean isLastInRow(int position, int spanCount) {
        return isFirstInRow(position + 1, spanCount);
    }

    private int getTotalSpanCount(RecyclerView parent) {
        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        return layoutManager instanceof GridLayoutManager
                ? ((GridLayoutManager) layoutManager).getSpanCount()
                : 1;
    }

    private int getItemSpanSize(RecyclerView parent, int position) {
        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        return layoutManager instanceof GridLayoutManager
                ? ((GridLayoutManager) layoutManager).getSpanSizeLookup().getSpanSize(position)
                : 1;
    }

    private boolean isMedia(RecyclerView parent, int viewPosition) {
        final RecyclerView.Adapter adapter = parent.getAdapter();
        final int viewType = adapter.getItemViewType(viewPosition);
        return allowedViewTypes.contains(viewType);
    }
}