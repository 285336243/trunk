package com.socialtv.topic;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.Response;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.ToastUtils;
import com.socialtv.http.HttpUtils;
import com.socialtv.message.entity.Post;
import com.socialtv.publicentity.Topic;
import com.socialtv.publicentity.User;
import com.socialtv.topic.entity.TopicPosts;
import com.socialtv.util.DialogUtils;
import com.socialtv.util.ImageUtils;
import com.socialtv.util.LoginUtil;
import com.socialtv.util.StartOtherFeedUtil;
import com.socialtv.view.TextViewFixTouchConsume;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wlanjie on 14-7-1.
 * 话题详情Adapter
 */
public class TopicDetailAdapter extends SingleTypeAdapter<Post> {

    private final static String EMPTY = "empty";
    private final static int ROOT = 0;
    private final static int IMAGE = 1;
    private final static int TEXT = 2;

    private final static int EMPTY_LAYOUT = 3;
    private final static int EMPTY_TEXT = 4;

    private final Activity activity;

    private String tag;

    /**
     * Create adapter
     *
     * @param activity
     * @param layoutResourceId
     */
    @Inject
    public TopicDetailAdapter(Activity activity) {
        super(activity, R.layout.topic_detail_item);
        this.activity = activity;
    }

    public void addItem(final List<Post> items, final String tag) {
        this.tag = tag;
        setItems(items);
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[] {
                R.id.topic_detail_item_root,
                R.id.topic_detail_item_image,
                R.id.topic_detail_item_text,

                R.id.list_empty_layout,
                R.id.list_empty_text
        };
    }

    @Override
    protected void update(int position, final Post item) {
        if (item != null) {
            if (EMPTY.equals(tag)) {
                setGone(ROOT, true);
                setGone(EMPTY_LAYOUT, false);
                setText(EMPTY_TEXT, item.getMsg());
            } else {
                setGone(EMPTY_LAYOUT, true);
                setGone(ROOT, false);

                if (position == 0) {
                    setGone(IMAGE, false);
                } else {
                    view(IMAGE).setVisibility(View.INVISIBLE);
                }
                User createUser = item.getCreateUser();
                String source = "";
                int createUserColor = 0;
                if (createUser != null) {
                    source += createUser.getNickname();
                    if (createUser.getNicknameColor() != null) {
                        createUserColor = Color.rgb(createUser.getNicknameColor().getR(), createUser.getNicknameColor().getG(), createUser.getNicknameColor().getB());
                    }
                }
                if (createUserColor == 0) {
                    createUserColor = Color.parseColor("#6F8FAA");
                }

                ReplyPost replyPost = item.getReplyPost();
                int start = 0;
                int end = 0;
                String userId = null;
                int replyUserColor = 0;
                if (replyPost != null) {
                    User replyCreateUser = replyPost.getCreateUser();
                    if (replyCreateUser != null) {
                        start = (source + "回复").length();
                        source = source + "回复" + replyCreateUser.getNickname();
                        end = source.length();
                        userId = replyCreateUser.getUserId();
                        if (replyCreateUser.getNicknameColor() != null) {
                            replyUserColor = Color.rgb(replyCreateUser.getNicknameColor().getR(), replyCreateUser.getNicknameColor().getG(), replyCreateUser.getNicknameColor().getB());
                        }
                    }
                }
                if (replyUserColor == 0) {
                    replyUserColor = Color.parseColor("#6F8FAA");
                }
                source = source + ": " + item.getMsg();

                SpannableString spannableInfo = new SpannableString(source);

                spannableInfo.setSpan(new Clickable(createUserColor, new OnClick(createUser.getUserId())), 0, createUser.getNickname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (start != 0 || end != 0) {
                    spannableInfo.setSpan(new Clickable(replyUserColor, new OnClick(userId)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                setText(TEXT, spannableInfo);
                textView(TEXT).setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());
            }
        }
    }

    private class OnClick implements View.OnClickListener {

        private final String userId;

        public OnClick(final String userId) {
            this.userId = userId;
        }

        @Override
        public void onClick(View view) {
            if (!TextUtils.isEmpty(userId)) {
                StartOtherFeedUtil.startOtherFeed(activity, userId);
            }
        }
    }

    private class Clickable extends ClickableSpan implements View.OnClickListener {
        private final View.OnClickListener listener;
        private final int color;

        public Clickable(int color, View.OnClickListener l) {
            listener = l;
            this.color = color;
        }

        @Override
        public void onClick(View view) {
            if (listener != null)
                listener.onClick(view);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(color);
            ds.setUnderlineText(false); //去掉下划线
        }
    }
}
