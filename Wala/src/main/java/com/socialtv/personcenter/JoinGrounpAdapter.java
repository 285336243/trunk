package com.socialtv.personcenter;


import android.app.Activity;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.home.entity.Entry;
import com.socialtv.util.ImageUtils;

import java.util.List;

public class JoinGrounpAdapter extends MultiTypeAdapter {

    private final static String TAG = "recommend";
    private final static int RECOMMEND = 0;
    private final static int JOIN = 1;

    private final static int TEXT = 0;
    private final static int PICTURE = 0;
    private final static int NAME = 1;
    private final static int FOLLOW_CNT = 2;
    private final static int TOPIC_CNT = 3;

    private final Activity activity;

    //防止添加两次text
    private boolean isAddRecommend = false;

    @Inject
    public JoinGrounpAdapter(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected int getChildLayoutId(int type) {
        if (type == RECOMMEND) {
            return R.layout.text;
        } else {
            return R.layout.join_group_adapter_item;
        }
    }

    @Override
    protected int[] getChildViewIds(int type) {
        if (type == RECOMMEND) {
            return new int[] {
                    R.id.text
            };
        } else {
            return new int[]{
                    R.id.show_picture, //展示图片    0
                    R.id.show_name,    //显示标题    1
                    R.id.show_follow_cnt,//成员数量  2
                    R.id.show_topic_cnt //话题数量   3
            };
        }
    }

    public void setItems(final List<Entry> entrys, final String tag) {
        for (int i = 0; i < entrys.size(); i++) {
            if (i == 0 && TAG.equals(tag) && !isAddRecommend) {
                isAddRecommend = true;
                addItem(RECOMMEND, entrys.get(i));
            }
            addItem(JOIN, entrys.get(i));
        }
    }

    public void setAddRecommend(boolean isAddRecommend) {
        this.isAddRecommend = isAddRecommend;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    protected void update(int position, Object item, int type) {
        if (type == RECOMMEND) {
            setText(TEXT, "也许你会对这些组感兴趣");
        } else {
            if (item != null) {
                final Entry entry = (Entry) item;
                ImageLoader.getInstance().displayImage(entry.getImg(), imageView(PICTURE), ImageUtils.imageLoader(activity, 0));
                setText(NAME, entry.getName());
                setText(FOLLOW_CNT, "成员 " + entry.getRefer().getFollowCnt());
                setText(TOPIC_CNT, "话题 " + entry.getRefer().getTopicCnt());
            }
        }
    }
}
