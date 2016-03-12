package com.mzs.guaji.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by sunjian on 13-12-18.
 */
public class ScaleImageView extends ImageView{

    private Matrix matrix = new Matrix();
    private float scale = 1.0f;

    public ScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Drawable drawable = getDrawable();
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if(drawable!=null){
            scale = Math.min((float)(width-getPaddingLeft()-getPaddingRight())/(float)drawable.getIntrinsicWidth(),(float)(height-getPaddingTop()-getPaddingBottom())/(float)drawable.getIntrinsicHeight());
            setMeasuredDimension(width, (int)(getPaddingBottom()+getPaddingTop()+drawable.getIntrinsicHeight()*scale));
        }else{
            setMeasuredDimension(width, getSuggestedMinimumHeight());
        }
    }
}
