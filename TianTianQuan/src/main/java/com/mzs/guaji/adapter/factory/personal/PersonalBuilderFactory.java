package com.mzs.guaji.adapter.factory.personal;

import android.content.Context;
import android.view.View;

import com.mzs.guaji.R;
import com.mzs.guaji.adapter.factory.ViewHolder;

public class PersonalBuilderFactory {

	public static ViewHolder builder(int type,Context context, View v){
		ViewHolder viewHolder = null;
		switch (type) {
		case 0:
			if(v == null) {
				viewHolder = new TopicViewHolder(context);
				v = View.inflate(context, R.layout.personal_topic_list_item, null);
				viewHolder.build(v);
				v.setTag(viewHolder);
				return viewHolder;
			}else {
				return (ViewHolder) v.getTag();
			}
		case 1:
			if(v == null) {
				viewHolder = new UserPicViewHolder();
				v = View.inflate(context, R.layout.personal_user_pic_list_item, null);
				viewHolder.build(v);
				v.setTag(viewHolder);
				return viewHolder;
			}else {
				return (ViewHolder) v.getTag();
			}
		case 2:
			if(v == null) {
				viewHolder = new TopicPostHolder(context);
				v = View.inflate(context, R.layout.personal_topic_post_list_item, null);
				viewHolder.build(v);
				v.setTag(viewHolder);
				return viewHolder;
			}else {
				return (ViewHolder) v.getTag();
			}
		}
		
		throw new IllegalArgumentException();
	}
}
