package com.jiujie8.choice.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.jiujie8.choice.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wlanjie on 14/11/21.
 */
public class PieGraph extends View implements  HoloGraphAnimate, Animator.AnimatorListener {

    private int mPadding;
    private ArrayList<PieSlice> mSlices = new ArrayList<PieSlice>();
    private final Paint mPaint = new Paint();
    private int mSelectedIndex = -1;
    private OnSliceClickedListener mListener;
    private boolean mDrawCompleted = false;
    private final RectF mRectF = new RectF();
    private Bitmap mBackgroundImage = null;
    private final Point mBackgroundImageAnchor = new Point(0,0);
    private int mDuration = 300;//in ms
    private Interpolator mInterpolator;
    private Animator.AnimatorListener mAnimationListener;
    private ValueAnimator mValueAnimator;
    private final float radius;
    private final float radiusPadding;
    private final float circleWidth;
    private final float marginEdge;
    private final int mCircleBackgroundColor;
    private int backgroundColor;

    public PieGraph(Context context) {
        this(context, null);
    }

    public PieGraph(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieGraph(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PieGraph);
        mPadding = a.getDimensionPixelSize(R.styleable.PieGraph_pieSlicePadding, 0);
        mCircleBackgroundColor = a.getColor(R.styleable.PieGraph_pieCircleBackground, R.color.pie_bkg);
        a.recycle();
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        final int mScreenWith = metrics.widthPixels;
        radius = (int) (mScreenWith * 0.3);

        radiusPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, metrics);
        circleWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, metrics);
        marginEdge = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, metrics);

    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        mPaint.reset();
        mPaint.setAntiAlias(true);

        mPaint.setColor(mCircleBackgroundColor);
        canvas.drawCircle((getMeasuredWidth()) / 2, (getMeasuredHeight() + getPaddingTop()) / 2, radius / 2, mPaint);

        float currentAngle = 270;
        float currentSweep = 0;
        float totalValue = 0;
        for (PieSlice slice : mSlices) {
            totalValue += slice.getValue();
        }

        for (int i = 0; i < mSlices.size(); i++) {
            PieSlice slice = mSlices.get(i);
            Path p = slice.getPath();
            p.reset();
            if (slice.getValue() >= slice.getOldValue()) {
                currentSweep = ( slice.getValue() /  totalValue) * (360);
            } else {
                currentSweep = 360.0f / totalValue * slice.getValue() * slice.getAnimatedValue();
                if (slice.getOldValue() == 0) {
                    continue;
                }
            }

            if (Float.isNaN(currentSweep)) {
                mPaint.setColor(mCircleBackgroundColor);
                if (i == mSlices.size() - 1 && slice.getValue() == 0) {
                    return;
                }
                currentSweep = 360;
            } else {
                mPaint.setColor(slice.getColor());
            }

            mRectF.set(0, 0, radius + radiusPadding, radius + radiusPadding);
            createArc(p, mRectF, currentSweep,
                    currentAngle + mPadding, currentSweep - mPadding);

            if (slice.isBold()) {
                final float marginEdge = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, getResources().getDisplayMetrics());
                mRectF.set(mRectF.left + marginEdge, mRectF.top + marginEdge, mRectF.right - marginEdge, mRectF.bottom - marginEdge);
            } else {
                final float marginEdge = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
                mRectF.set(mRectF.left + marginEdge, mRectF.top + marginEdge, mRectF.right - marginEdge, mRectF.bottom - marginEdge);
            }
            createArc(p, mRectF, currentSweep,
                    (currentAngle + mPadding) + (currentSweep - mPadding),
                    -(currentSweep - mPadding));

            p.close();

            canvas.drawPath(p, mPaint);
            currentAngle = currentAngle + currentSweep;
        }
        mDrawCompleted = true;
    }

    private void createArc(Path p, RectF mRectF, float currentSweep, float startAngle, float sweepAngle) {
        if (currentSweep == 360) {
            p.addArc(mRectF, startAngle, sweepAngle);
        } else {
            p.arcTo(mRectF, startAngle, sweepAngle);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDrawCompleted) {
            Point point = new Point();
            point.x = (int) event.getX();
            point.y = (int) event.getY();

            int count = 0;
            Region r = new Region();
            for (PieSlice slice : mSlices) {
                r.setPath(slice.getPath(), slice.getRegion());
                switch (event.getAction()) {
                    default:
                        break;
                    case MotionEvent.ACTION_DOWN:
                        if (r.contains(point.x, point.y)) {
                            mSelectedIndex = count;
                            postInvalidate();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (count == mSelectedIndex
                                && mListener != null
                                && r.contains(point.x, point.y)) {
                            mListener.onClick(mSelectedIndex);
                        }
                        break;
                }
                count++;
            }
        }
        // Case we click somewhere else, also get feedback!
        if(MotionEvent.ACTION_UP == event.getAction()
                && mSelectedIndex == -1
                && mListener != null) {
            mListener.onClick(mSelectedIndex);
        }
        // Reset selection
        if (MotionEvent.ACTION_UP == event.getAction()
                || MotionEvent.ACTION_CANCEL == event.getAction()) {
            mSelectedIndex = -1;
            postInvalidate();
        }
        return true;
    }

    public Bitmap getBackgroundBitmap() {
        return mBackgroundImage;
    }

    public void setBackgroundBitmap(Bitmap backgroundBitmap, int pos_x, int pos_y) {
        mBackgroundImage = backgroundBitmap;
        mBackgroundImageAnchor.set(pos_x, pos_y);
        postInvalidate();
    }

    public void setBackgroundBitmap(Bitmap backgroundBitmap) {
        mBackgroundImage = backgroundBitmap;
        postInvalidate();
    }

//    public void setBackgroundColor(final int backgroundColor) {
//        this.backgroundColor = backgroundColor;
//        postInvalidate();
//    }

    /**
     * sets padding
     * @param padding
     */
    public void setPadding(int padding) {
        mPadding = padding;
        postInvalidate();
    }

    public ArrayList<PieSlice> getSlices() {
        return mSlices;
    }

    public void setSlices(ArrayList<PieSlice> slices) {
        for (PieSlice slice : slices) {
            slice.setOldValue(slice.getValue());
        }
        mSlices = slices;
        postInvalidate();
    }

    public PieSlice getSlice(int index) {
        return mSlices.get(index);
    }

    public void addSlice(PieSlice slice) {
        mSlices.add(slice);
        postInvalidate();
    }

    public void setOnSliceClickedListener(OnSliceClickedListener listener) {
        mListener = listener;
    }

    public void removeSlices() {
        mSlices.clear();
        postInvalidate();
    }

    @Override
    public int getDuration() {
        return mDuration;
    }

    @Override
    public void setDuration(int duration) {mDuration = duration;}

    @Override
    public Interpolator getInterpolator() {
        return mInterpolator;
    }

    @Override
    public void setInterpolator(Interpolator interpolator) {mInterpolator = interpolator;}


    @Override
    public boolean isAnimating() {
        return mValueAnimator != null && mValueAnimator.isRunning();
    }

    @Override
    public boolean cancelAnimating() {
        if (mValueAnimator != null)
            mValueAnimator.cancel();
        return false;
    }


    /**
     * Stops running animation and starts a new one, animating each slice from their current to goal value.
     * If removing a slice, consider animating to 0 then removing in onAnimationEnd listener.
     * Default inerpolator is linear; constant speed.
     */
    @Override
    public void animateToGoalValues() {
        if (mValueAnimator != null)
            mValueAnimator.cancel();

        ValueAnimator va = ValueAnimator.ofFloat(0,1);
        mValueAnimator = va;
        va.setDuration(getDuration());
        if (mInterpolator == null) mInterpolator = new LinearInterpolator();
        va.setInterpolator(mInterpolator);
        if (mAnimationListener != null) va.addListener(this);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                for (PieSlice s : mSlices) {
                    float progress = s.getOldValue() * (Float) animation.getAnimatedValue();
                    s.setAnimatedValue((Float) animation.getAnimatedValue());
                    s.setValue(progress);
                }
                postInvalidate();
            }});
            va.start();
    }

    public void startAnimation() {
        setDuration(1000);
        setInterpolator(new AccelerateDecelerateInterpolator());

    }

    @Override
    public void setAnimationListener(Animator.AnimatorListener animationListener) {
        mAnimationListener = animationListener;
    }

    @Override
    public void onAnimationStart(Animator animator) {
        if (mAnimationListener != null) {
            mAnimationListener.onAnimationStart(animator);
        }
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        for (PieSlice s : mSlices) {
            s.setValue(s.getOldValue());
        }
        if (mAnimationListener != null) {
            mAnimationListener.onAnimationEnd(animator);
        }
    }

    @Override
    public void onAnimationCancel(Animator animator) {
        if (mAnimationListener != null) {
            mAnimationListener.onAnimationCancel(animator);
        }
    }

    @Override
    public void onAnimationRepeat(Animator animator) {
        if (mAnimationListener != null) {
            mAnimationListener.onAnimationRepeat(animator);
        }
    }

    public interface OnSliceClickedListener {
        public abstract void onClick(int index);
    }
}
