package com.shengzhish.xyj.activity.specialnews;



import android.app.Activity;
import android.content.Context;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.activity.entity.Post;
import com.shengzhish.xyj.persionalcore.entity.User;
import com.shengzhish.xyj.util.ImageUtils;


public class CommentListAdapter extends SingleTypeAdapter<Post> {

    private Context activity;

    public CommentListAdapter(Activity activity) {
        super(activity, R.layout.dynamic_comment_adapter_item);
        this.activity=activity;
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[]{
                R.id.show_avatar_picture, //展示头像图片    0
                R.id.show_name,    //显示名字    1
                R.id.show_message,//评论信息     2
                R.id.show_create_time //评论时间   3
        };
    }




    @Override
    protected void update(int position, Post item) {
        if (item != null) {
            User user = item.getUser();
            ImageLoader.getInstance().displayImage(user.getAvatar(), imageView(0), ImageUtils.imageLoader(activity,4));
            setText(1,  user.getNickname());
            setText(2, item.getMessage());
            setText(3, item.getCreateTime());

        }

    }

}
