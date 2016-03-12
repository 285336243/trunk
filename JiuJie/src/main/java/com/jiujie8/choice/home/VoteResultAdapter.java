package com.jiujie8.choice.home;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.google.inject.Inject;
import com.jiujie8.choice.R;
import com.jiujie8.choice.publicentity.User;
import com.jiujie8.choice.util.ImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by wlanjie on 15/1/13.
 * 投票结果页的Adapter
 */
public class VoteResultAdapter extends MultiTypeAdapter {

    private final Activity mActivity;

    private final DisplayMetrics mMetrics;

    private final int ROOT = 0;

    private final int AVATAR = 1;

    @Inject
    public VoteResultAdapter(final Activity activity) {
        super(activity);
        mActivity = activity;
        mMetrics = mActivity.getResources().getDisplayMetrics();

    }

    @Override
    protected int getChildLayoutId(int type) {
        return R.layout.vote_result_item;
    }

    @Override
    protected int[] getChildViewIds(int type) {
        return new int[] {
                R.id.vote_result_item_root,
                R.id.vote_result_item_avatar
        };
    }

    @Override
    protected void update(int position, Object item, int type) {
        if (item instanceof User) {
            final int width = (mMetrics.widthPixels - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7 * 12, mMetrics)) / 6;
            final AbsListView.LayoutParams mParams = new AbsListView.LayoutParams(width, width);
            view(ROOT).setLayoutParams(mParams);
            User mUser = (User) item;
            ImageLoader.getInstance().displayImage(mUser.getAvatar(), imageView(AVATAR), ImageUtils.avatarImageLoader());
        }

    }
}
