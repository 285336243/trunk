package com.mzs.guaji.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.Group;
import com.mzs.guaji.offical.OfficialTvCircleActivity;
import com.mzs.guaji.ui.PrivateCircleActivity;
import com.mzs.guaji.view.GuaJiGridView;

public class TvCircleAdapter extends BaseAdapter {

	private Context context;
	private List<Group> groups;
    private boolean isAnimation = true;
	
	public TvCircleAdapter(Context context, List<Group> groups) {
		this.context = context;
		this.groups = groups;
	}
	
	public void addGroups(List<Group> groups) {
		for(Group group : groups) {
			this.groups.add(group);
		}
		notifyDataSetChanged();
	}

    public void setIsAnimation(boolean isAnimation) {
        this.isAnimation = isAnimation;
    }

	public void clear() {
		groups.clear();
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
		View view = null;
		view = View.inflate(context, R.layout.tvcircle_item_grid, null);
		GuaJiGridView mGridView = (GuaJiGridView) view.findViewById(R.id.rvcircle_list_grid);
        if(isAnimation) {
            SwingBottomInAnimationAdapter mAnimationAdapter = new SwingBottomInAnimationAdapter(new TvCircleListGridAdapter(context, groups), 150);
            mAnimationAdapter.setAbsListView(mGridView);
            mGridView.setAdapter(mAnimationAdapter);
        }else {
            mGridView.setAdapter(new TvCircleListGridAdapter(context, groups));
        }
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Group mGroup = groups.get(position);
				if("OFFICIAL".equals(mGroup.getType())) {
					Intent mIntent = new Intent(context, OfficialTvCircleActivity.class);
					mIntent.putExtra("img", mGroup.getImg());
					mIntent.putExtra("id", mGroup.getId());
					mIntent.putExtra("name", mGroup.getName());
					context.startActivity(mIntent);
				}else {
					Intent privateIntent = new Intent(context, PrivateCircleActivity.class);
					privateIntent.putExtra("id", mGroup.getId());
					context.startActivity(privateIntent);
				}
			}
		});
		
		return view;
	}

}
