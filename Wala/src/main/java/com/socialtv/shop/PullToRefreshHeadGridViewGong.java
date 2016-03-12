package com.socialtv.shop;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.View;

import com.handmark.pulltorefresh.library.OverscrollHelper;
import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.internal.EmptyViewMethodAccessor;
import com.socialtv.R;

public class PullToRefreshHeadGridViewGong extends PullToRefreshAdapterViewBase<GridView> {
    private AttributeSet shuxing;
    private Context contextd;
    private View headerView;

    public GridView getGridView() {
        return gridView;
    }

    private GridView gridView;


    public PullToRefreshHeadGridViewGong(Context context) {
        super(context);
        contextd=context;
    }

    public PullToRefreshHeadGridViewGong(Context context, AttributeSet attrs) {
        super(context, attrs);
        contextd=context;
        shuxing=attrs;
    }

    public PullToRefreshHeadGridViewGong(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshHeadGridViewGong(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
        contextd=context;
    }

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected final GridView createRefreshableView(Context context, AttributeSet attrs) {
        final GridView gv;
        if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
            gv = new InternalGridViewSDK9(context, attrs);


        } else {
            gv = new InternalGridView(context, attrs);
        }
//        gv.setNumColumns(2);
//        if(null!=headerView)
//            View header = View.inflate(context, R.layout.shop_header, null);
//        gv.addHeaderView(headerView);
        // Use Generated ID (from res/values/ids.xml)
        gv.setId(R.id.gridview);
        gridView=gv;
        return gv;
    }


    protected final GridView createGridView(Context context) {
        final GridView gv;

        gv=new GridView(context);
        return gv;
    }

    protected void addHeaderView(View view){
        headerView=view;
    }


    class InternalGridView extends GridView implements EmptyViewMethodAccessor {

        public InternalGridView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
        public InternalGridView(Context context) {
            super(context);
        }

        @Override
        public void setEmptyView(View emptyView) {
            PullToRefreshHeadGridViewGong.this.setEmptyView(emptyView);
        }

        @Override
        public void setEmptyViewInternal(View emptyView) {
            super.setEmptyView(emptyView);
        }
    }

    @TargetApi(9)
    final class InternalGridViewSDK9 extends InternalGridView {

        public InternalGridViewSDK9(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
                                       int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

            final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
                    scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

            // Does all of the hard work...
            OverscrollHelper.overScrollBy(PullToRefreshHeadGridViewGong.this, deltaX, scrollX, deltaY, scrollY, isTouchEvent);

            return returnValue;
        }
    }
}
