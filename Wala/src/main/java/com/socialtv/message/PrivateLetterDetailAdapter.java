package com.socialtv.message;

import android.app.Activity;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.message.entity.PrivateLetterDetailList;
import com.socialtv.util.ImageUtils;
import com.socialtv.util.LoginUtil;

import java.util.List;

/**
 * Created by wlanjie on 14-7-3.
 * 私信详情的Adapter
 */
public class PrivateLetterDetailAdapter extends MultiTypeAdapter {

    private Activity activity;
    private final static int LEFT = 0;
    private final static int RIGHT = 1;

    private final static int AVATAR = 0;
    private final static int MESSAGE = 1;
    private final static int CREATE_TIME = 2;
    private final static int ROOT = 3;

    /**
     * Create adapter
     *
     * @param activity
     */
    public PrivateLetterDetailAdapter(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public void addItems(List<PrivateLetterDetailList> items) {
        for (PrivateLetterDetailList item : items) {
            if (item.getUser() != null) {
                if (LoginUtil.getUserId(activity).equals(item.getUser().getUserId())) {
                    addItem(RIGHT, item);
                } else {
                    addItem(LEFT, item);
                }
            }
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /**
     * Get layout id for type
     *
     * @param type
     * @return layout id
     */
    @Override
    protected int getChildLayoutId(int type) {
        if (type == LEFT) {
            return R.layout.private_letter_detail_left;
        } else if (type == RIGHT) {
            return R.layout.private_letter_detail_right;
        }
        return 0;
    }

    /**
     * Get child view ids for type
     * <p/>
     * The index of each id in the returned array should be used when using the
     * helpers to update a specific child view
     *
     * @param type
     * @return array of view ids
     */
    @Override
    protected int[] getChildViewIds(int type) {
        if (type == LEFT) {
            return new int[] {
                    R.id.private_letter_detail_left_avatar,
                    R.id.private_letter_detail_left_message,
                    R.id.private_letter_detail_left_create_time,
                    R.id.private_letter_detail_left_root
            };
        } else if (type == RIGHT) {
            return new int[] {
                    R.id.private_letter_detail_right_avatar,
                    R.id.private_letter_detail_right_message,
                    R.id.private_letter_detail_right_create_time,
                    R.id.private_letter_detail_right_root
            };
        }
        return new int[0];
    }

    /**
     * Update view for item
     *
     * @param position
     * @param item
     * @param type
     */
    @Override
    protected void update(int position, Object item, int type) {
        try {
            if (item != null) {
                PrivateLetterDetailList list = (PrivateLetterDetailList) item;
                ImageLoader.getInstance().displayImage(list.getUser().getAvatar(), imageView(AVATAR), ImageUtils.imageLoader(activity, 1000));
                setText(MESSAGE, list.getPost());
                setText(CREATE_TIME, list.getCreateTime());
                if (type == LEFT) {
                    view(ROOT).setBackgroundColor(activity.getResources().getColor(R.color.white));
                } else if (type == RIGHT) {
                    view(ROOT).setBackgroundColor(activity.getResources().getColor(R.color.baby_white));
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
