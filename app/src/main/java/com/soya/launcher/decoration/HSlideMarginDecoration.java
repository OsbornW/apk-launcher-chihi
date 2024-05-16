package com.soya.launcher.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HSlideMarginDecoration extends RecyclerView.ItemDecoration {
    private final float maxMargin;
    private final float minMargin;
    public HSlideMarginDecoration(float maxMargin, float minMargin){
        this.maxMargin = maxMargin;
        this.minMargin = minMargin;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildAdapterPosition(view) == 0){
            outRect.left = (int) maxMargin;
            outRect.right = (int) minMargin;
        }else if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1){
            outRect.left = (int) minMargin;
            outRect.right = (int) maxMargin;
        }else {
            outRect.left = (int) minMargin;
            outRect.right = (int) minMargin;
        }
    }
}
