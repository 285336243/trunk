package com.mzs.guaji.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.PrivatePost;
import com.mzs.guaji.util.ImageUtils;
import com.mzs.guaji.util.LoginUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by wlanjie on 14-3-4.
 */
public class PrivateLetterDetailAdapter extends SingleTypeAdapter<PrivatePost> {

    private final Context context;

    private final LayoutInflater inflater;

    public PrivateLetterDetailAdapter(Context context, LayoutInflater inflater, List<?> items) {
        super(inflater, R.layout.loading_item);
        this.context = context;
        this.inflater = inflater;
        setItems(items);
    }

    public void addItems(List<PrivatePost> items) {
        for (PrivatePost post : items) {
            if (getItems() != null)
                getItems().add(post);
        }
        notifyDataSetChanged();
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[]{R.id.iv_private_letter_detail_left_avatar,
                        R.id.tv_private_letter_detail_left_content,
                        R.id.tv_private_letter_detail_left_time,
                        R.id.iv_private_letter_detail_right_avatar,
                        R.id.tv_private_letter_detail_right_content,
                        R.id.tv_private_letter_detail_right_time,
                        R.id.ll_private_letter_detail_left_root,
                        R.id.ll_private_letter_detail_right_root
        };
    }

    @Override
    protected void update(int position, PrivatePost item) {
        int itemType = getItemViewType(position);
        if (itemType == 0) {
            ImageLoader.getInstance().displayImage(item.getUserAvatar(), imageView(0), ImageUtils.imageLoader(context, 4));
            setText(1, item.getPost());
            setText(2, item.getCreateTime());

        } else if (itemType == 1) {
            ImageLoader.getInstance().displayImage(item.getUserAvatar(), imageView(3), ImageUtils.imageLoader(context, 4));
            setText(4, item.getPost());
            setText(5, item.getCreateTime());
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        PrivatePost privatePost = getItem(position);
        if (privatePost.getUserId() != LoginUtil.getUserId(context)) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (type == 0) {
            if (convertView == null) {
                convertView = initialize(inflater.inflate(R.layout.private_letter_detail_left, null));
            }
        } else {
            if (convertView == null) {
                convertView = initialize(inflater.inflate(R.layout.private_letter_detail_right, null));
            }
        }
        update(position, convertView, getItem(position));
        return convertView;
    }
}
