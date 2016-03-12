package com.mzs.guaji.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mzs.guaji.R;
import com.mzs.guaji.entity.EntryForm;
import com.mzs.guaji.topic.entity.Topic;
import com.mzs.guaji.util.ImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunjian on 13-12-21.
 */
public class HotTopicAdapter extends SearchToPicAdapter {

    private List<EntryForm> entryForms;

    private boolean hasHead;
    private  View headerView;
    private Context context;

    public HotTopicAdapter(Context context, List<Topic> topics, List<EntryForm> entryForms) {
        super(context, topics);
        this.entryForms = entryForms;
        this.context = context;
        if(entryForms!=null && entryForms.size() > 0 ){
            hasHead = true;
            List<View> mViews = new ArrayList<View>();
            headerView = View.inflate(context, R.layout.topic_hot_list_header, null);
            ViewPager mViewPager = (ViewPager) headerView.findViewById(R.id.topic_hot_list_viewpager);
            int size = entryForms.size()/2+(entryForms.size()%2 == 0?0:1);
            for (int i = 0; i < size;) {
                View view = View.inflate(context, R.layout.topic_hot_viewpager_item, null);
                ImageView mImageOne = (ImageView) view.findViewById(R.id.topic_hot_viewpager_item_image_one);
                ImageView mImageTwo = (ImageView) view.findViewById(R.id.topic_hot_viewpager_item_image_two);
                ImageLoader.getInstance().displayImage(entryForms.get(2 * i).getCoverImg(), mImageOne, ImageUtils.imageLoader(context, 0));
                mImageOne.setTag(entryForms.get(2 * i));
                if(2*i+1<entryForms.size()){
                    ImageLoader.getInstance().displayImage(entryForms.get(2 * i+1).getCoverImg(), mImageTwo, ImageUtils.imageLoader(context, 0));
                    mImageTwo.setTag(entryForms.get(2 * i+1));
                }
                mViews.add(view);
                i++;
            }
            mViewPager.setAdapter(new ToPicHotViewPagerAdapter(context,mViews));
        }
    }

    public List<Topic> getTopics(){
        return topics;
    }

    public List<EntryForm> getEntryForms(){
        return entryForms;
    }

    @Override
    public int getCount() {
        return super.getCount()+(hasHead?1:0);
    }

    @Override
    public Object getItem(int position) {

        if(position == 0 && hasHead){
            return headerView;
        }
        return super.getItem(hasHead?(position-1):position);
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount()+(hasHead?1:0);
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0 && hasHead){
            return 1;
        }
        return super.getItemViewType(hasHead?(position-1):position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(position == 0 && hasHead){
            return headerView;
        }
        return super.getView(hasHead?(position-1):position,convertView, parent);
    }

}
