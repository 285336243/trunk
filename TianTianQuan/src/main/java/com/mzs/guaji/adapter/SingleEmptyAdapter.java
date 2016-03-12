package com.mzs.guaji.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.mzs.guaji.R;

/**
 * Created by wlanjie on 14-3-13.
 */
public class SingleEmptyAdapter extends SingleTypeAdapter {

    private Activity activity;
    public SingleEmptyAdapter(Activity activity) {
        super(activity, R.layout.empty_layout);
        this.activity = activity;
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[]{R.id.empty_text};
    }

    @Override
    protected void update(int position, Object item) {
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(activity, R.layout.empty_layout, null);
        TextView mTextView = (TextView) v.findViewById(R.id.empty_text);
        mTextView.setText("暂无明星信息");
        return v;
    }
}
