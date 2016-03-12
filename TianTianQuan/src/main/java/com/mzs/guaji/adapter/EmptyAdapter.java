package com.mzs.guaji.adapter;

import com.mzs.guaji.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EmptyAdapter extends BaseAdapter {

	private Context context;
	private int textResId;
	public EmptyAdapter(Context context, int textResId) {
		this.context = context;
		this.textResId = textResId;
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
		View v = View.inflate(context, R.layout.empty_layout, null);
		TextView mTextView = (TextView) v.findViewById(R.id.empty_text);
		mTextView.setText(textResId);
		return v;
	}

}
