package com.mzs.guaji.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mzs.guaji.R;
import com.mzs.guaji.entity.GroupNews;

public class NewsListAdapter extends BaseAdapter {

	private Context context;
	private List<GroupNews> mGroupNews;
	
	public NewsListAdapter(Context context, List<GroupNews> mGroupNews) {
		this.context = context;
		this.mGroupNews = mGroupNews;
	}

    public void clear() {
        this.mGroupNews.clear();
    }

	public void addGroupNewsItem(List<GroupNews> mGroupNews) {
		for(GroupNews mNews : mGroupNews) {
			this.mGroupNews.add(mNews);
		}
		notifyDataSetChanged();
	}
	
	public void addMoreGroupNewsItem(List<GroupNews> mGroupNews) {
		for(GroupNews mNews : mGroupNews) {
			this.mGroupNews.add(mNews);
		}
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mGroupNews.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mGroupNews.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder mHolder = null;
		if(v == null) {
			mHolder = new ViewHolder();
			v = View.inflate(context, R.layout.news_list_item, null);
			mHolder.mTitleText = (TextView) v.findViewById(R.id.news_list_item_title);
			mHolder.mSourceText = (TextView) v.findViewById(R.id.news_list_item_source);
			mHolder.mCreateTimeText = (TextView) v.findViewById(R.id.news_list_item_create_time);
			v.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) v.getTag();
		}
		GroupNews mNews = mGroupNews.get(position);
		mHolder.mTitleText.setText(mNews.getTitle());
		mHolder.mSourceText.setText(mNews.getSource());
		mHolder.mCreateTimeText.setText(mNews.getCreateTime());
		return v;
	}

	private static class ViewHolder {
		public TextView mTitleText;
		public TextView mSourceText;
		public TextView mCreateTimeText;
	}
}
