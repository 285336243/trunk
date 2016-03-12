package com.mzs.guaji.adapter;

import android.content.Context;
import android.view.View;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.PrivatePost;
import com.mzs.guaji.entity.PrivatePostList;
import com.mzs.guaji.util.ImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by wlanjie on 14-3-3.
 */
public class PrivateLetterListAdapter extends SingleTypeAdapter<PrivatePostList> {

    private final Context context;
    private int position = -1;
    private boolean isSelect;
    private PrivatePostList listItem;

    public PrivateLetterListAdapter(Context context) {
        super(context, R.layout.private_letter_list_item);
        this.context = context;
    }
    @Override
    protected int[] getChildViewIds() {
        return new int[]{R.id.private_letter_avatar, R.id.private_letter_nickname, R.id.private_letter_content,
                        R.id.private_letter_create_time, R.id.iv_private_letter_list_item_badge, R.id.private_letter_list_parent};
    }

    @Override
    protected void update(int position, PrivatePostList item) {
        ImageLoader.getInstance().displayImage(item.getContactUserAvatar(), imageView(0), ImageUtils.imageLoader(context, 4));
        setText(1, item.getContactUserNickname());
        PrivatePost privatePost = item.getPrivatePost();
        if (privatePost != null) {
            setText(2, privatePost.getPost());
            setText(3, privatePost.getCreateTime());
        }
        if (item.getStatus() == 2) {
            setGone(4, false);
        } else if (item.getStatus() == 1){
            setGone(4, true);
        }
//        if (this.position == position) {
//            setGone(4, true);
//        } else {
//            setGone(4, false);
//        }

        if (this.position == position) {
            setGone(4, true);
        }
        if(item.isChecked()) {
            view(5).setBackgroundColor(context.getResources().getColor(R.color.message_item_selector));
        }else{
            view(5).setBackgroundColor(context.getResources().getColor(R.color.white));
        }
    }

    public void setPosition(int position) {
        this.position = position;
        notifyDataSetChanged();
    }

    public void setItemBackground(boolean isSelect, PrivatePostList listItem, View v) {
        this.isSelect = isSelect;
        this.listItem = listItem;
        listItem.setChecked(isSelect);
        if(v!=null){
            if(listItem.isChecked()) {
                ((View[]) (v.getTag()))[5].setBackgroundColor(context.getResources().getColor(R.color.message_item_selector));
            }else{
                ((View[]) (v.getTag()))[5].setBackgroundColor(context.getResources().getColor(R.color.white));
            }
        }

    }
}
