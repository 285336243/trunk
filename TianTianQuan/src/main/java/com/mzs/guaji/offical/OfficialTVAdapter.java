package com.mzs.guaji.offical;

import android.app.Activity;
import android.text.TextUtils;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.mzs.guaji.R;
import com.mzs.guaji.offical.entity.ModuleSpecs;
import com.mzs.guaji.util.ImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by wlanjie on 14-5-27.
 */
public class OfficialTVAdapter extends SingleTypeAdapter<ModuleSpecs> {

    private final Activity activity;

    /**
     * Create adapter
     *
     * @param activity
     */
    @Inject
    public OfficialTVAdapter(Activity activity) {
        super(activity, R.layout.official_tv_grid_item);
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
                R.id.official_grid_item_image,
                R.id.official_grid_item_name,
                R.id.official_grid_item_icon
        };
    }

    /**
     * Update item
     *
     * @param position
     * @param item
     */
    @Override
    protected void update(int position, ModuleSpecs item) {
        setText(1, item.getTitle());
        if (!TextUtils.isEmpty(item.getIcon()))
            ImageLoader.getInstance().displayImage(item.getIcon(), imageView(0), ImageUtils.imageLoader(activity, 0));
        if (!TextUtils.isEmpty(item.getTag())) {
            ImageLoader.getInstance().displayImage(item.getTag(), imageView(2), ImageUtils.imageLoader(activity, 0));
        } else {
            setGone(2, true);
        }

    }
}
