package com.mzs.guaji.fragment;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.Label;
import com.mzs.guaji.entity.LabelSearch;
import com.mzs.guaji.ui.SearchCircleLabelActivity;

public class SearchLabelFragment extends GuaJiFragment {
	private String labelSearch = "group/labels.json";
	private CircleLabelAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.search_label_layout, container, false);
		final GridView mGridView = (GridView) v.findViewById(R.id.search_circle_grid);
		mApi.requestGetData(DOMAIN + labelSearch, LabelSearch.class, new Response.Listener<LabelSearch>() {
				@Override
				public void onResponse(LabelSearch response) {
					if(response != null && response.getLabels() != null && response.getLabels().size() > 0) {
						mAdapter = new CircleLabelAdapter(response.getLabels());
						mGridView.setAdapter(mAdapter);
						mGridView.setOnItemClickListener(new SearchLabelClick(response.getLabels()));
					}
				}
	    }, this);
		return v;
	}
	
	private class SearchLabelClick implements AdapterView.OnItemClickListener {
		
		private List<Label> labels;
		public SearchLabelClick(List<Label> labels) {
			this.labels = labels;
		}
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			final String content = labels.get(position).getLabel();
			if(TextUtils.isEmpty(content)) {
				Toast.makeText(getActivity(), "搜索内容不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			Intent mIntent = new Intent(getActivity(), SearchCircleLabelActivity.class);
			mIntent.putExtra("key", content);
			getActivity().startActivity(mIntent);
		}
		
	}
	
	private class CircleLabelAdapter extends BaseAdapter {
		
		private List<Label> labels;
		
		public CircleLabelAdapter(List<Label> labels) {
			this.labels = labels;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return labels.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return labels.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = View.inflate(getActivity(), R.layout.search_label_grid_item, null);
			TextView mTextView = (TextView) v.findViewById(R.id.search_circle_grid_item_text);
			mTextView.setText(labels.get(position).getLabel());
			return v;
		}
		
	}
}
