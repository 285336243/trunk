package com.socialtv.personcenter;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.widget.AbsListView;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.core.Utils;
import com.socialtv.publicentity.Pics;
import com.socialtv.util.ImageUtils;
import com.socialtv.util.LoginUtil;

import java.util.List;

/**
 * Created by wlanjie on 14-7-4.
 */
public class PhotoAlbumAdapter extends MultiTypeAdapter {

    private final Activity activity;
    private final int width;
    private final static int ITEM = 0;
    private final static int ADD = 1;

    /**
     * Create adapter
     *
     * @param activity
     * @param layoutResourceId
     */
    @Inject
    public PhotoAlbumAdapter(Activity activity) {
        super(activity);
        this.activity = activity;
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.width = metrics.widthPixels;
    }

    public void setItems(List<Pics> items, final String userId) {
        if (items.isEmpty() && userId.equals(LoginUtil.getUserId(activity))) {
            addItem(ADD, new Pics());
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            addItem(ITEM, items.get(i));
            if (i == items.size() - 1 && userId.equals(LoginUtil.getUserId(activity))) {
                addItem(ADD, items.get(i));
            }
        }
    }

    @Override
    protected int getChildLayoutId(int type) {
        if (type == ITEM) {
            return R.layout.image;
        } else if (type == ADD) {
            return R.layout.photo_album_add;
        }
        return -1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    protected int[] getChildViewIds(int type) {
        if (type == ITEM) {
            return new int[]{
                    R.id.imageview
            };
        } else {
            return new int[]{
                    R.id.photo_album_add_root
            };
        }
    }

    @Override
    protected void update(int position, Object item, int type) {
        if (type == ITEM) {
            Pics pics = (Pics) item;
            if (pics != null) {
                view(0).setLayoutParams(new AbsListView.LayoutParams((width - Utils.dip2px(activity, 32)) / 3, (int) (width * 0.225)));
                if (item != null)
                    ImageLoader.getInstance().displayImage(pics.getImg(), imageView(0), ImageUtils.imageLoader(activity, 0));
            }
        } else {
            view(0).setLayoutParams(new AbsListView.LayoutParams((width - Utils.dip2px(activity, 32)) / 3, (int) (width * 0.225)));
        }
    }
}
