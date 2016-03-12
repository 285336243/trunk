package com.shengzhish.xyj.activity.vote;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.activity.entity.VoteList;
import com.shengzhish.xyj.util.AnimationUtil;
import com.shengzhish.xyj.util.ImageUtils;

import java.util.LinkedList;
import java.util.List;


/**
 * vote list Adapter
 */
public class VoteListAdapter extends SingleTypeAdapter<VoteList> {
    private final Context activity;
    private String mode;
    ListWatch listWatch;

    private List<String> idList = new LinkedList<String>();

    public void setListWatch(ListWatch listWatch) {
        this.listWatch = listWatch;
    }

    public VoteListAdapter(VoteActivity activity) {
        super(activity, R.layout.vote_list_adapter_item);
        this.activity = activity;
    }

    /**
     * Get child view ids to store
     * <p/>
     * The index of each id in the returned array should be used when using the
     * helpers to update a specific child view
     *
     * @return ids
     */
    @Override
    protected int[] getChildViewIds() {
        return new int[]{
                R.id.show_img,   //展示头像图片    0
                R.id.name_textview,    //显示名字    1
                R.id.vote_textview,  // 投票     2
                R.id.vote_root_view, //布局layout   3
                R.id.progress_bar  //加载进度条progressbar    4
        };
    }

    /**
     * Update item
     *
     * @param position
     * @param item
     */
    @Override
    protected void update(final int position, final VoteList item) {
        if (item != null) {
//            Log.v("person", "position ==" + position + ", name=" + item.getName());
            if (position % 2 == 0) {
                //AnimationUtil.horizontalAnimation2(view(3), true);
                view(3).setBackgroundColor(activity.getResources().getColor(R.color.message_background_color));
            } else {
                //AnimationUtil.horizontalAnimation(view(3), true);
                view(3).setBackgroundColor(activity.getResources().getColor(R.color.transparent));
            }

            ImageLoader.getInstance().displayImage(item.getImg(), imageView(0), ImageUtils.imageLoader(activity, 0),
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            view(4).setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            super.onLoadingStarted(imageUri, view);
                            view(4).setVisibility(View.VISIBLE);

                        }

                    });
            setText(1, item.getName());
            if ("SHOW_RESULT".equals(mode)) {
                //0  未投票，1  投票
                if (item.getIsVote() == 0) {
                    textView(2).setBackgroundResource(R.drawable.corner_no_vote_button);
                } else {
                    textView(2).setBackgroundResource(R.drawable.corner_have_vote_button);
                }
                setText(2, item.getVote());
            }
            if ("SHOW_VOTE".equals(mode)) {

                //0  未投票，1  投票
                if (item.getIsVote() == 0) {
                    textView(2).setBackgroundResource(R.drawable.corner_no_vote_button);
                    setText(2, "投票");
                } else {
                    setText(2, "");
                    textView(2).setBackgroundResource(R.drawable.have_vote_icon);
                }
                textView(2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v("person", "点击的position ==" + position);
                        String id = item.getId();
                        if (item.getIsVote() == 0) {
                            idList.add(id);
                            listWatch.listChange(idList);
                            item.setIsVote(1);
                            notifyDataSetChanged();
                        } else {
                            idList.remove(id);
                            listWatch.listChange(idList);
                            item.setIsVote(0);
                            notifyDataSetChanged();
                        }
                    }
                });

            }

        }
    }


    public void setMode(String mode) {
        this.mode = mode;
        notifyDataSetChanged();
    }


    public interface ListWatch {
        void listChange(List list);
    }

}
