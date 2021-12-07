package com.example.news;

import android.content.Context;
import android.widget.ImageView;

public class ImageViewSquare extends androidx.appcompat.widget.AppCompatImageView {
    public ImageViewSquare(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }
}
