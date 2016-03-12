package com.socialtv.view;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ListView.FixedViewInfo;

import java.util.ArrayList;

public class HeaderGridView extends HorizontalGridView{

	private Context mContext;

	public HeaderGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public HeaderGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public HeaderGridView(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context context){
		mContext = context;
	}

    protected View fillLeft(int pos, int nextRight) {
        View selectedView = null;

        final int end = mListPadding.left;

        while (nextRight > end && pos >= 0) {

            View temp = makeColumn(pos, nextRight, false);
            if (temp != null) {
                selectedView = temp;
            }

            nextRight = mReferenceView.getLeft() - mHorizontalSpacing;

            mFirstPosition = pos;

            pos -= mNumRows;
        }

        if (mStackFromBottom) {
            mFirstPosition = Math.max(0, pos + 1);
        }

        return selectedView;
    }

    protected View fillRight(int pos, int nextLeft) {
        View selectedView = null;

        final int end = (getRight() - getLeft()) - mListPadding.right;

        while (nextLeft < end && pos < mItemCount) {
            View temp = makeColumn(pos, nextLeft, true);
            if (temp != null) {
                selectedView = temp;
            }

            nextLeft = mReferenceView.getRight() + mHorizontalSpacing;

            if(pos == 0 ){
                pos ++;
            }else {
                pos += mNumRows;
            }
        }

        return selectedView;
    }

    protected View makeColumn(int startPos, int x, boolean flow) {
        final int rowHeight = mRowHeight;//mColumnWidth;
        final int verticalSpacing = mRequestedVerticalSpacing;//mHorizontalSpacing;

        int last;
        int nextTop = mListPadding.top + ((mStretchMode == STRETCH_SPACING_UNIFORM) ? verticalSpacing : 0);

        if (!mStackFromBottom) {
            if(startPos == 0 ){
                last = 1;
            }else {
                last = Math.min(startPos + mNumRows, mItemCount);
            }
        } else {
            last = startPos + 1;
            startPos = Math.max(0, startPos - mNumRows + 1);

            if (last - startPos < mNumRows) {
                nextTop += (mNumRows - (last - startPos)) * (rowHeight + verticalSpacing);
            }
        }

        View selectedView = null;

        final boolean hasFocus = shouldShowSelector();
        final boolean inClick = touchModeDrawsInPressedState();
        final int selectedPosition = mSelectedPosition;

        mReferenceView = null;

        for (int pos = startPos; pos < last; pos++) {
            // is this the selected item?
            boolean selected = pos == selectedPosition;
            // does the list view have focus or contain focus

            final int where = flow ? -1 : pos - startPos;
            final View child = makeAndAddView(pos, x, flow, nextTop, selected, where);
            mReferenceView = child;

            nextTop += rowHeight;
            if (pos < last - 1) {
                nextTop += verticalSpacing;
            }

            if (selected && (hasFocus || inClick)) {
                selectedView = child;
            }
        }

        if (selectedView != null) {
            mReferenceViewInSelectedRow = mReferenceView;
        }

        return selectedView;
    }


    protected void setupChild(View child, int position, int x, boolean flow, int childrenTop,
                              boolean selected, boolean recycled, int where) {
        boolean isSelected = selected && shouldShowSelector();

        final boolean updateChildSelected = isSelected != child.isSelected();
        boolean needToMeasure = !recycled || updateChildSelected || child.isLayoutRequested();

        // Respect layout params that are already in the view. Otherwise make
        // some up...
        HorizontalAbsListView.LayoutParams p = (HorizontalAbsListView.LayoutParams)child.getLayoutParams();
        if (p == null) {
            p = new HorizontalAbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        }
        p.viewType = mAdapter.getItemViewType(position);

        if (recycled) {
            attachViewToParent(child, where, p);
        } else {
            addViewInLayout(child, where, p, true);
        }

        if (updateChildSelected) {
            child.setSelected(isSelected);
            if (isSelected) {
                requestFocus();
            }
        }

        if (needToMeasure) {
            int childHeightSpec = ViewGroup.getChildMeasureSpec(
                    MeasureSpec.makeMeasureSpec((position == 0?(2*mRowHeight+mRequestedVerticalSpacing):mRowHeight), MeasureSpec.EXACTLY), 0, (position == 0?(2*p.height+mRequestedVerticalSpacing):p.height));

            int childWidthSpec = ViewGroup.getChildMeasureSpec(
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 0, p.width);
            child.measure(childWidthSpec, childHeightSpec);
        } else {
            cleanupLayoutState(child);
        }

        final int w = child.getMeasuredWidth();
        final int h = child.getMeasuredHeight();

        int childLeft = flow ? x : x - w;
        int childTop = 0;

        switch (mGravity & Gravity.VERTICAL_GRAVITY_MASK) {
            case Gravity.TOP:
                childTop = childrenTop;
                break;
            case Gravity.CENTER_VERTICAL:
                childTop = childrenTop + ((mRowHeight - h) / 2);
                break;
            case Gravity.BOTTOM:
                childTop = childrenTop + mRowHeight - w;
                break;
            default:
                childTop = childrenTop;
                break;
        }

        if (needToMeasure) {
            final int childRight = childLeft + w;
            final int childBottom = childTop + h;
            child.layout(childLeft, childTop, childRight, childBottom);
        } else {
            child.offsetLeftAndRight(childLeft - child.getLeft());
            child.offsetTopAndBottom(childTop - child.getTop());
        }

        if (mCachingStarted) {
            child.setDrawingCacheEnabled(true);
        }
    }

    protected View fillSpecific(int position, int left) {
        final int numRows = mNumRows;

        int motionColumnStart;
        int motionColumnEnd = -1;

        if (!mStackFromBottom) {
            if(position == 0){
                motionColumnStart = 0;
            }else {
                motionColumnStart = (position / numRows + (position % numRows == 0 ? 0 : 1) - 1) * numRows + 1;
            }
        } else {
            final int invertedSelection = mItemCount - 1 - position;

            motionColumnEnd = mItemCount - 1 - (invertedSelection - (invertedSelection % numRows));
            motionColumnStart = Math.max(0, motionColumnEnd - numRows + 1);
        }

        final View temp = makeColumn(mStackFromBottom ? motionColumnEnd : motionColumnStart, left, true);

        // Possibly changed again in fillUp if we add rows above this one.
        mFirstPosition = motionColumnStart;

        final View referenceView = mReferenceView;
        final int verticalSpacing = mRequestedVerticalSpacing;
        final int horizonalSpacing = mHorizontalSpacing;

        View above;
        View below;

        if (!mStackFromBottom) {
            if(motionColumnStart == 1){
                above = fillLeft(0, referenceView.getLeft() - horizonalSpacing);
            }else {
                above = fillLeft(motionColumnStart - numRows, referenceView.getLeft() - horizonalSpacing);
            }
            adjustViewsUpOrDown();
            if(motionColumnStart == 0){
                below = fillRight(1, referenceView.getRight() + horizonalSpacing);
            }else {
                below = fillRight(motionColumnStart + numRows, referenceView.getRight() + horizonalSpacing);
            }
            // Check if we have dragged the bottom of the grid too high
            final int childCount = getChildCount();
            if (childCount > 0) {
                correctTooHigh(numRows, verticalSpacing, childCount);
            }
        } else {
            below = fillLeft(motionColumnEnd + numRows, referenceView.getBottom() + verticalSpacing);
            adjustViewsUpOrDown();
            above = fillLeft(motionColumnStart - 1, referenceView.getTop() - verticalSpacing);
            // Check if we have dragged the bottom of the grid too high
            final int childCount = getChildCount();
            if (childCount > 0) {
                correctTooLow(numRows, verticalSpacing, childCount);
            }
        }

        if (temp != null) {
            return temp;
        } else if (above != null) {
            return above;
        } else {
            return below;
        }
    }

    @Override
    protected void fillGap(boolean down) {
        final int numRows = mNumRows;
        final int horizontalSpacing = mRequestedHorizontalSpacing;

        final int count = getChildCount();

        if (down) {
            final int startOffset = count > 0 ?
                    getChildAt(count - 1).getRight() + horizontalSpacing : getListPaddingLeft();
            int position = mFirstPosition + count;
            if (mStackFromBottom) {
                position += numRows - 1;
            }
            fillRight(position, startOffset);
            correctTooHigh(numRows, horizontalSpacing, getChildCount());
        } else {
            final int startOffset = count > 0 ?
                    getChildAt(0).getLeft() - horizontalSpacing : getWidth() - getListPaddingRight();
            int position = mFirstPosition;
            if (!mStackFromBottom) {
                if(position == 1){
                    position--;
                }else {
                    position -= numRows;
                }
            } else {
                position--;
            }
            fillLeft(position, startOffset);
            correctTooLow(numRows, horizontalSpacing, getChildCount());
        }
    }

}
