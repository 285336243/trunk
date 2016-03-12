package com.socialtv.view;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import java.util.ArrayList;

/**
 * ListAdapter used when a HeaderGridView has header views. This ListAdapter
 * wraps another one and also keeps track of the header views and their
 * associated data objects.
 *<p>This is intended as a base class; you will probably not need to
 * use this class directly in your own code.
 */
public class HeaderViewGridAdapter implements WrapperListAdapter, Filterable {

    // This is used to notify the container of updates relating to number of columns
    // or headers changing, which changes the number of placeholders needed
    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    private final ListAdapter mAdapter;
    private int mNumColumns = 1;

    // This ArrayList is assumed to NOT be null.
    ArrayList<GridView.FixedViewInfo> mHeaderViewInfos;
    ArrayList<GridView.FixedViewInfo> mFooterViewInfos;

    boolean mAreAllFixedViewsSelectable;

    private final boolean mIsFilterable;

    public HeaderViewGridAdapter(ArrayList<GridView.FixedViewInfo> headerViewInfos, ArrayList<GridView.FixedViewInfo> footerViewInfos, ListAdapter adapter) {
        mAdapter = adapter;
        mIsFilterable = adapter instanceof Filterable;

        if (headerViewInfos == null) {
            throw new IllegalArgumentException("headerViewInfos cannot be null");
        }
        mHeaderViewInfos = headerViewInfos;
        mFooterViewInfos = footerViewInfos;

        mAreAllFixedViewsSelectable = areAllListInfosSelectable(mHeaderViewInfos);
    }

    public int getHeadersCount() {
        return mHeaderViewInfos.size();
    }

    @Override
    public boolean isEmpty() {
        return (mAdapter == null || mAdapter.isEmpty()) && getHeadersCount() == 0;
    }

    public void setNumColumns(int numColumns) {
        if (numColumns < 1) {
            throw new IllegalArgumentException("Number of columns must be 1 or more");
        }
        if (mNumColumns != numColumns) {
            mNumColumns = numColumns;
            notifyDataSetChanged();
        }
    }

    private boolean areAllListInfosSelectable(ArrayList<GridView.FixedViewInfo> infos) {
        if (infos != null) {
            for (GridView.FixedViewInfo info : infos) {
                if (!info.isSelectable) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean removeHeader(View v) {
        for (int i = 0; i < mHeaderViewInfos.size(); i++) {
            GridView.FixedViewInfo info = mHeaderViewInfos.get(i);
            if (info.view == v) {
                mHeaderViewInfos.remove(i);

                mAreAllFixedViewsSelectable = areAllListInfosSelectable(mHeaderViewInfos);

                mDataSetObservable.notifyChanged();
                return true;
            }
        }

        return false;
    }

    public boolean removeFooter(View v) {
        for (int i = 0; i < mFooterViewInfos.size(); i++) {
            GridView.FixedViewInfo info = mFooterViewInfos.get(i);
            if (info.view == v) {
                mFooterViewInfos.remove(i);

                mAreAllFixedViewsSelectable = areAllListInfosSelectable(mFooterViewInfos);

                mDataSetObservable.notifyChanged();
                return true;
            }
        }

        return false;
    }

    @Override
    public int getCount() {
        if (mAdapter != null) {
            return getHeadersCount() * mNumColumns + mAdapter.getCount();
        } else {
            return getHeadersCount() * mNumColumns;
        }
    }

    @Override
    public boolean areAllItemsEnabled() {
        if (mAdapter != null) {
            return mAreAllFixedViewsSelectable && mAdapter.areAllItemsEnabled();
        } else {
            return true;
        }
    }

    @Override
    public boolean isEnabled(int position) {
        // Header (negative positions will throw an ArrayIndexOutOfBoundsException)
        int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
        if (position < numHeadersAndPlaceholders) {
            return (position % mNumColumns == 0)
                    && mHeaderViewInfos.get(position / mNumColumns).isSelectable;
        }

        // Adapter
        final int adjPosition = position - numHeadersAndPlaceholders;
        int adapterCount = 0;
        if (mAdapter != null) {
            adapterCount = mAdapter.getCount();
            if (adjPosition < adapterCount) {
                return mAdapter.isEnabled(adjPosition);
            }
        }

        throw new ArrayIndexOutOfBoundsException(position);
    }

    @Override
    public Object getItem(int position) {
        // Header (negative positions will throw an ArrayIndexOutOfBoundsException)
        int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
        if (position < numHeadersAndPlaceholders) {
            if (position % mNumColumns == 0) {
                return mHeaderViewInfos.get(position / mNumColumns).data;
            }
            return null;
        }

        // Adapter
        final int adjPosition = position - numHeadersAndPlaceholders;
        int adapterCount = 0;
        if (mAdapter != null) {
            adapterCount = mAdapter.getCount();
            if (adjPosition < adapterCount) {
                return mAdapter.getItem(adjPosition);
            }
        }

        throw new ArrayIndexOutOfBoundsException(position);
    }

    @Override
    public long getItemId(int position) {
        int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
        if (mAdapter != null && position >= numHeadersAndPlaceholders) {
            int adjPosition = position - numHeadersAndPlaceholders;
            int adapterCount = mAdapter.getCount();
            if (adjPosition < adapterCount) {
                return mAdapter.getItemId(adjPosition);
            }
        }
        return -1;
    }

    @Override
    public boolean hasStableIds() {
        if (mAdapter != null) {
            return mAdapter.hasStableIds();
        }
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Header (negative positions will throw an ArrayIndexOutOfBoundsException)
        int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns ;
        if (position < numHeadersAndPlaceholders) {
            View headerViewContainer = mHeaderViewInfos
                    .get(position / mNumColumns).viewContainer;
            if (position % mNumColumns == 0) {
                return headerViewContainer;
            } else {
                if (convertView == null) {
                    convertView = new View(parent.getContext());
                }
                // We need to do this because GridView uses the height of the last item
                // in a row to determine the height for the entire row.
                convertView.setVisibility(View.INVISIBLE);
                convertView.setMinimumHeight(headerViewContainer.getHeight());
                return convertView;
            }
        }

        // Adapter
        final int adjPosition = position - numHeadersAndPlaceholders;
        int adapterCount = 0;
        if (mAdapter != null) {
            adapterCount = mAdapter.getCount();
            if (adjPosition < adapterCount) {
                return mAdapter.getView(adjPosition, convertView, parent);
            }
        }

        throw new ArrayIndexOutOfBoundsException(position);
    }

    @Override
    public int getItemViewType(int position) {
        int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
        if (position < numHeadersAndPlaceholders && (position % mNumColumns != 0)) {
            // Placeholders get the last view type number
            return mAdapter != null ? mAdapter.getViewTypeCount() : 1;
        }
        if (mAdapter != null && position >= numHeadersAndPlaceholders) {
            int adjPosition = position - numHeadersAndPlaceholders;
            int adapterCount = mAdapter.getCount();
            if (adjPosition < adapterCount) {
                return mAdapter.getItemViewType(adjPosition);
            }
        }

        return AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER;
    }

    @Override
    public int getViewTypeCount() {
        if (mAdapter != null) {
            return mAdapter.getViewTypeCount() + 1;
        }
        return 2;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
        if (mAdapter != null) {
            mAdapter.registerDataSetObserver(observer);
        }
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(observer);
        }
    }

    @Override
    public Filter getFilter() {
        if (mIsFilterable) {
            return ((Filterable) mAdapter).getFilter();
        }
        return null;
    }

    @Override
    public ListAdapter getWrappedAdapter() {
        return mAdapter;
    }

    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }
}