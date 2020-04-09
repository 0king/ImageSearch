package com.example.imagesearch.ui.util;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SquareGridLayoutManager extends GridLayoutManager {
    public SquareGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SquareGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public SquareGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
        // force width of viewHolder to be a fraction of RecyclerViews
        // this will override layout_width from xml
        lp.width = getWidth() / getSpanCount();
        lp.height = lp.width;
        return super.checkLayoutParams(lp);
    }
}
