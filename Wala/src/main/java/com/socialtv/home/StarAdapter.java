package com.socialtv.home;

import android.app.Activity;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.home.entity.Entry;
import com.socialtv.util.ImageUtils;
import com.socialtv.view.HorizontalAbsListView;

import java.util.List;

/**
 * Created by wlanjie on 14-6-23.
 * 主页中的明星Adapter
 */
public class StarAdapter extends MultiTypeAdapter {

    private final static int CONTENT = 0;
    private final static int EMPTY = 1;
    private final static int ITEM_ROOT = 0;
    private final static int ITEM_AVATAR = 1;
    private final Activity activity;

    private final int width;

    /**
     * Create adapter
     *
     * @param activity
     */
    public StarAdapter(Activity activity, final int width) {
        super(activity);
        this.activity = activity;
        this.width = (int) (width * 0.2);
    }

    public void addItem(List<Entry> entries) {
        if (entries != null && !entries.isEmpty()) {
            for (int i = 0; i < entries.size(); i++) {
                if (i == 0) {
                    addItem(EMPTY, entries.get(i));
                    addItem(CONTENT, entries.get(i));
                } else if (i == entries.size() - 1) {
                    addItem(CONTENT, entries.get(i));
                    addItem(EMPTY, entries.get(i));
                } else {
                    addItem(CONTENT, entries.get(i));
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
        if (type == CONTENT) {
            return R.layout.home_star_item;
        } else if (type == EMPTY) {
            return R.layout.empty;
        }
        return -1;
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
        if (type == CONTENT) {
            return new int[] {
                    R.id.home_star_item_root,
                    R.id.home_star_item_avatar
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
        Entry entry = (Entry) item;
        if (item != null && type == CONTENT) {
            HorizontalAbsListView.LayoutParams params = new HorizontalAbsListView.LayoutParams(width, width);
            view(ITEM_ROOT).setLayoutParams(params);
            ImageLoader.getInstance().displayImage(entry.getImg(), imageView(ITEM_AVATAR), ImageUtils.avatarImageLoader());
        }
    }
}
