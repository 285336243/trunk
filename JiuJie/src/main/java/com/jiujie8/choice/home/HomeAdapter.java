package com.jiujie8.choice.home;

import android.app.Activity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.google.inject.Inject;
import com.jiujie8.choice.R;
import com.jiujie8.choice.core.Intents;
import com.jiujie8.choice.home.entity.ChoiceItem;
import com.jiujie8.choice.home.entity.ChoiceMode;
import com.jiujie8.choice.home.entity.Post;
import com.jiujie8.choice.home.entity.PostItem;
import com.jiujie8.choice.persioncenter.LoginActivity;
import com.jiujie8.choice.persioncenter.PercenterActivityGridview;
import com.jiujie8.choice.publicentity.User;
import com.jiujie8.choice.util.ImageUtils;
import com.jiujie8.choice.util.LoginUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by wlanjie on 14/12/8.
 *
 * 主页fragment中的List的Adapter
 */
public class HomeAdapter extends MultiTypeAdapter {

    private final static int AVATAR_LAYOUT = 0;
    private final static int AVATAR = 1;
    private final static int NICKNAME = 2;
    private final static int CROWN = 3;
    private final static int CONTENT = 4;
    private final static int CREATE_TIME = 5;
    private final static int TAG_IMAGE = 6;

    private final Activity activity;
    private final ChoiceItem mChoiceItem ;
    private final int width;
    private final int leftMargin;

//    @Inject
    public HomeAdapter(Activity activity, final ChoiceMode mMode) {
        super(activity);
        this.activity = activity;
        mChoiceItem = mMode.getChoice();
        width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, activity.getResources().getDisplayMetrics());
        leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, activity.getResources().getDisplayMetrics());

    }

    public void addItems(List<Post> mItems) {
        for (int i = 0; i < mItems.size(); i++) {
            addItem(0, mItems.get(i));
        }
    }

    @Override
    protected int getChildLayoutId(int type) {
        return R.layout.home_list_item;
    }

    @Override
    protected int[] getChildViewIds(int type) {
        return new int[] {
                R.id.home_list_item_avatar_layout,
                R.id.home_list_item_avatar,
                R.id.home_list_item_nickname,
                R.id.home_list_item_crown,
                R.id.home_list_item_content,
                R.id.home_list_item_create_time,
                R.id.home_list_item_tag_image
        };
    }

    @Override
    protected void update(int position, Object item, int type) {
        if (item != null) {
            final Post mPost = (Post) item;
            final PostItem mItem = mPost.getPost();
            if (mItem != null) {
                final User mUser = mItem.getUser();
                final User mChoiceUser = mChoiceItem.getUser();
                if (mUser != null) {
                    ImageLoader.getInstance().displayImage(mUser.getAvatar(), imageView(AVATAR), ImageUtils.avatarImageLoader());
                    setText(NICKNAME, mUser.getNickname());
                    view(AVATAR_LAYOUT).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (LoginUtil.isLogin()) {
                                activity.startActivity(new Intents(activity, PercenterActivityGridview.class).toIntent());
                            } else {
                                activity.startActivity(new Intents(activity, LoginActivity.class).toIntent());
                            }
                        }
                    });

                    LinearLayout.LayoutParams mParams;
                    if (mUser.getId().equals(mChoiceUser.getId())) {
                        mParams = new LinearLayout.LayoutParams(width, width);
                    } else {
                        mParams = new LinearLayout.LayoutParams(width * 2, width);
                    }
                    mParams.leftMargin = leftMargin;
                    mParams.gravity = Gravity.CENTER_VERTICAL;
                    view(TAG_IMAGE).setLayoutParams(mParams);
                }
                setText(CONTENT, mItem.getMessage());
                setText(CREATE_TIME, mItem.getCreateTime());
                ImageLoader.getInstance().displayImage(mPost.getValue(), imageView(TAG_IMAGE));
            }
        }
    }
}
