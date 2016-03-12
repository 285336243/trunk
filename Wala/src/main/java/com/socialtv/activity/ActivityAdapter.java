package com.socialtv.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.nineoldandroids.animation.Animator;import com.nineoldandroids.animation.AnimatorSet;import com.nineoldandroids.animation.ObjectAnimator;import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.Response;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.Intents;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.ThirdPartyShareActivity;
import com.socialtv.core.ToastUtils;
import com.socialtv.http.HttpUtils;
import com.socialtv.message.entity.Post;
import com.socialtv.personcenter.LoginActivity;
import com.socialtv.publicentity.SupportTag;
import com.socialtv.publicentity.Topic;
import com.socialtv.core.Utils;
import com.socialtv.publicentity.User;
import com.socialtv.publicentity.UserBadge;
import com.socialtv.topic.ReplyPost;
import com.socialtv.topic.TopicDetailActivity;
import com.socialtv.topic.TopicHeaderImageAdapter;
import com.socialtv.topic.TopicServices;
import com.socialtv.util.DialogUtils;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;
import com.socialtv.util.LoginUtil;
import com.socialtv.util.StartOtherFeedUtil;
import com.socialtv.view.ExpandableTextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wlanjie on 14-6-25.
 */
public class ActivityAdapter extends MultiTypeAdapter {

    private final static int ITEM_VIEW_TYPE = 0;
    private final static String EMPTY = "empty";

    private final static int ROOT = 0;
    private final static int AVATAR_LAYOUT = 1;
    private final static int AVATAR = 2;
    private final static int NICKNAME = 3;
    private final static int CREATE_TIME = 4;
    private final static int DESC = 5;
    private final static int DESC_IMAGE_LAYOUT = 6;
    private final static int DESC_IMAGE = 7;
    private final static int SUPPORT_COUNT = 8;
    private final static int POST_LAYOUT = 9;
    private final static int CONTAINER_LAYOUT = 10;
    private final static int SUPPORT_LAYOUT = 11;
    private final static int GRID = 12;
    private final static int SHARE = 13;
    private final static int SUPPORT = 14;
    private final static int POST = 15;
    private final static int SUPPORT_TEXT = 16;
    private final static int DEFAULT_DELETE_BUTTON = 17;
    private final static int DELETE_LAYOUT = 18;
    private final static int PRIZE_LAYOUT = 19;
    private final static int PRIZE_TEXT  =20;
    private final static int BADGE_LAYOUT = 21;
    private final static int PRIZE_AND_SUPPORT_AND_POST = 22;
    private final static int DIVIDER = 23;

    private final static int EMPTY_LAYOUT = 24;
    private final static int EMPTY_TEXT = 25;

    private final ActivityDetailActivity activity;
    private final int width;
    private final DialogUtils dialogUtils;
    private final TopicServices services;

    //加这个标志位是点击昵称和回复评论会有冲突
    private boolean isNicknameClick = false;

    private String tag;

    public ActivityAdapter(ActivityDetailActivity activity) {
        super(activity);
        this.activity = activity;
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.width = metrics.widthPixels;
        dialogUtils = new DialogUtils();
        services = new TopicServices();
    }

    public void addItem(List<Topic> items, String tag) {
        this.tag = tag;
        for (int i = 0; i < items.size(); i++) {
            addItem(ITEM_VIEW_TYPE, items.get(i));
        }
    }

    /**
     * Get layout id for type
     *
     * @param type
     * @return layout id
     */
    @Override
    protected int getChildLayoutId(int type) {
        return R.layout.activity_detail_item;
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
        return new int[]{
                //话题
                R.id.topic_item_root,
                R.id.topic_item_avatar_layout,
                R.id.topic_item_avatar,
                R.id.topic_item_nickname,
                R.id.topic_item_createtime,
                R.id.topic_expand_text_view,
                R.id.topic_item_desc_image_layout,
                R.id.topic_item_desc_image,
                R.id.topic_support_count,
                R.id.topic_post_layout,
                R.id.topic_post_container_layout,
                R.id.topic_support_layout,
                R.id.topic_item_image_grid,
                R.id.topic_item_share,
                R.id.topic_item_support,
                R.id.topic_item_post,
                R.id.topic_item_support_text,
                R.id.topic_item_default_delete_button,
                R.id.topic_item_delete_layout,
                R.id.topic_prize_layout,
                R.id.topic_prize_text,
                R.id.topic_item_badge_layout,
                R.id.topic_prize_and_support_and_post_layout,

                R.id.item_divider,

                //当list没有内容时,显示这个Layout
                R.id.list_empty_layout,
                R.id.list_empty_text,
        };
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
        if (position != getCount() - 1) {
            setGone(DIVIDER, false);
        } else  {
            setGone(DIVIDER, true);
        }
        setTopicData((Topic) item);
    }

    private void setTopicData(final Topic item) {
        try {
            if (item != null) {
                if (EMPTY.equals(tag)) {
                    //空的,显示默认的
                    setGone(ROOT, true);
                    setGone(EMPTY_LAYOUT, false);
                    setText(EMPTY_TEXT, item.getMessage());
                } else {
                    //隐藏默认的
                    setGone(ROOT, false);
                    setGone(EMPTY_LAYOUT, true);
                    if (item.getIsFavorit() == 0) {
                        //没赞过
                        textView(SUPPORT_TEXT).setText("赞");
                    } else {
                        // 已赞
                        textView(SUPPORT_TEXT).setText("已赞");
                    }

                    final User user = item.getUser();
                    if (user != null) {
                        setText(NICKNAME, user.getNickname());
                        ImageLoader.getInstance().displayImage(user.getAvatar(), imageView(AVATAR), ImageUtils.avatarImageLoader());
                        view(AVATAR_LAYOUT).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                StartOtherFeedUtil.startOtherFeed(activity, user.getUserId());
                            }
                        });

                        if (user.getBadges() != null && !user.getBadges().isEmpty()) {
                            setGone(BADGE_LAYOUT, false);
                            ((LinearLayout) view(BADGE_LAYOUT)).removeAllViews();
                            for (UserBadge badge : user.getBadges()) {
                                if (badge != null) {
                                    ImageView imageView = new ImageView(activity);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dip2px(activity, 12), Utils.dip2px(activity, 12));
                                    params.gravity = Gravity.CENTER_VERTICAL;
                                    imageView.setLayoutParams(params);
                                    ImageLoader.getInstance().displayImage(badge.getImg(), imageView, ImageUtils.imageLoader(activity, 0));
                                    ((LinearLayout) view(BADGE_LAYOUT)).addView(imageView);
                                }
                            }
                        } else {
                            setGone(BADGE_LAYOUT, true);
                        }

                        //如果是自己发的话题,显示删除按钮
                        if (user.getUserId().equals(LoginUtil.getUserId(activity))) {
                            setGone(DEFAULT_DELETE_BUTTON, false);
                            view(DEFAULT_DELETE_BUTTON).setTag(view(DELETE_LAYOUT));
                            view(DEFAULT_DELETE_BUTTON).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    view.setVisibility(View.INVISIBLE);
                                    View v = (View) view.getTag();
                                    v.setVisibility(View.VISIBLE);
                                    View deleteButton = v.findViewById(R.id.topic_item_delete_button);
                                    final View deleteText = v.findViewById(R.id.topic_item_delete_text);

                                    ObjectAnimator emptyAnimation = ObjectAnimator.ofFloat(deleteText, "alpha", 0f, 0f, 0f).setDuration(320);
                                    emptyAnimation.addListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            deleteText.setVisibility(View.VISIBLE);
                                            ObjectAnimator.ofFloat(deleteText, "alpha", 0f, 1.0f, 1.0f).setDuration(400).start();
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animation) {

                                        }
                                    });
                                    AnimatorSet rotationAndTranslaitionAnimation = new AnimatorSet();
                                    rotationAndTranslaitionAnimation.playTogether(ObjectAnimator.ofFloat(deleteButton, "rotation", 360, 0, 0).setDuration(800),
                                            ObjectAnimator.ofFloat(deleteButton, "translationX", v.getMeasuredWidth(), 0, 0).setDuration(800), emptyAnimation);
                                    AnimatorSet set = new AnimatorSet();
                                    set.playSequentially(rotationAndTranslaitionAnimation);
                                    set.start();
                                    //点击删除
                                    v.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            execDeleteTopic(item.getId());
                                        }
                                    });
                                }
                            });
                        } else {
                            view(DEFAULT_DELETE_BUTTON).setVisibility(View.INVISIBLE);
                            view(DELETE_LAYOUT).setVisibility(View.INVISIBLE);
                        }

                        if (user.getUserId().equals(LoginUtil.getUserId(activity))) {
                            //如果删除是显示的,点击整个item时,让删除隐藏
                            view(ROOT).setTag(view(DEFAULT_DELETE_BUTTON));
                            view(ROOT).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final View deleteDefaultButton = (View) view.getTag();
                                    final View deleteLayout = view.findViewById(R.id.topic_item_delete_layout);
                                    final View deleteText = view.findViewById(R.id.topic_item_delete_text);
                                    if (deleteLayout.getVisibility() == View.VISIBLE) {
                                        deleteLayout.setVisibility(View.INVISIBLE);
                                        deleteText.setVisibility(View.INVISIBLE);
                                        deleteDefaultButton.setVisibility(View.VISIBLE);
                                        AnimatorSet set = new AnimatorSet();
                                        set.playTogether(ObjectAnimator.ofFloat(deleteDefaultButton, "translationX", -(deleteLayout.getMeasuredWidth() - deleteDefaultButton.getMeasuredWidth()), 0, 0).setDuration(800),
                                                ObjectAnimator.ofFloat(deleteDefaultButton, "rotation", 0, 360, 360).setDuration(800));
                                        set.start();
                                    }
                                }
                            });
                        }
                    }
                    setText(CREATE_TIME, item.getCreateTime());
                    if (!TextUtils.isEmpty(item.getMessage())) {
                        ((ExpandableTextView) view(DESC)).setText(item.getMessage());
                    } else {
                        setGone(DESC, true);
                    }
                    if (item.getPics() != null && !item.getPics().isEmpty()) {
                        setGone(DESC_IMAGE_LAYOUT, false);
                        if (item.getPics().size() == 1) {
                            //单图
                            setGone(GRID, true);
                            setGone(DESC_IMAGE_LAYOUT, false);
                            ImageLoader.getInstance().displayImage(item.getPics().get(0).getImg() + ".296x296", imageView(DESC_IMAGE), ImageUtils.imageLoader(activity, 0));
                            dialogUtils.setSingleImageOnClick(activity, view(DESC_IMAGE_LAYOUT), item.getPics().get(0).getImg());
                        } else {
                            //多张图
                            setGone(GRID, false);
                            setGone(DESC_IMAGE_LAYOUT, true);
                            TopicHeaderImageAdapter imageAdapter = new TopicHeaderImageAdapter(activity);
                            ((GridView) view(GRID)).setAdapter(imageAdapter);
                            imageAdapter.clear();
                            int count = (item.getPics().size() + 2) / 3;
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width - Utils.dip2px(activity, 24), ((int) (width * 0.225) * count + Utils.dip2px(activity, (count - 1) * 4)));
                            params.topMargin = Utils.dip2px(activity, 12);
                            ((GridView) view(GRID)).setLayoutParams(params);

                            imageAdapter.addItems(0, item.getPics());
                            ((GridView) view(GRID)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    dialogUtils.showImageDetailDialog(activity, item.getPics(), position);
                                }
                            });
                        }
                    } else {
                        //无图,隐藏grid和单图layout
                        setGone(GRID, true);
                        setGone(DESC_IMAGE_LAYOUT, true);
                    }

                    if (!TextUtils.isEmpty(item.getPrize()) || item.getFavoritsCnt() > 0 || item.getPosts() != null && !item.getPosts().isEmpty()) {
                        setGone(PRIZE_AND_SUPPORT_AND_POST, false);
                    } else {
                        setGone(PRIZE_AND_SUPPORT_AND_POST, true);
                    }

                    //奖励
                    if (!TextUtils.isEmpty(item.getPrize())) {
                        setGone(PRIZE_LAYOUT,false);
                        setText(PRIZE_TEXT, item.getPrize());
                    } else {
                        setGone(PRIZE_LAYOUT, true);
                    }

                    //赞的数量
                    if (item.getFavoritsCnt() <= 0) {
                        setGone(SUPPORT_LAYOUT, true);
                    } else {
                        setGone(SUPPORT_LAYOUT, false);
                        setText(SUPPORT_COUNT, item.getFavoritsCnt() + "个赞");
                    }

                    //评论
                    int count = 0;
                    if (item.getPosts() != null) {
                        if (item.getPosts().size() >= 10) {
                            count = 10;
                        } else {
                            count = (int) item.getPosts().size();
                        }
                    }
                    if (count <= 0) {
                        setGone(CONTAINER_LAYOUT, true);
                    } else {
                        setGone(CONTAINER_LAYOUT, false);
                        addPostToView(item, count);
                    }

                    //分享点击
                    view(SHARE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (LoginUtil.isLogin(activity)) {
                                Dialog shareDialog = dialogUtils.createShareDialog((ThirdPartyShareActivity) activity, item);
                                if (shareDialog != null && !shareDialog.isShowing()) {
                                    shareDialog.show();
                                }
                            } else {
                                activity.startActivity(new Intents(activity, LoginActivity.class).toIntent());
                            }
                        }
                    });

                    SupportTag tag = new SupportTag();
                    tag.setSupportCountText(textView(SUPPORT_COUNT));
                    tag.setSupportText(textView(SUPPORT_TEXT));
                    view(SUPPORT).setTag(tag);
                    //赞
                    view(SUPPORT).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (LoginUtil.isLogin(activity)) {
                                SupportTag tag = (SupportTag) view.getTag();
                                supportEventListener(item, tag.getSupportCountText(), tag.getSupportText());
                            } else {
                                activity.startActivity(new Intents(activity, LoginActivity.class).toIntent());
                            }
                        }
                    });

                    //评论
                    view(POST).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (LoginUtil.isLogin(activity)) {
                                dialogUtils.createPostDialog(activity);
                                submitPost(item);
                            } else {
                                activity.startActivity(new Intents(activity, LoginActivity.class).toIntent());
                            }
                        }
                    });
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    /**
     * 删除话题,刷新list列表
     * @param topicId
     */
    private void execDeleteTopic(final String topicId) {
        new ProgressDialogTask<Response>(activity) {
            /**
             * Execute task with an authenticated account
             *
             * @param data
             * @return result
             * @throws Exception
             */
            @Override
            protected Response run(Object data) throws Exception {
                return (Response) HttpUtils.doRequest(services.createDeleteRequest(topicId)).result;
            }

            @Override
            protected void onSuccess(Response response) throws Exception {
                super.onSuccess(response);
                if (response != null) {
                    if (response.getResponseCode() == 0) {
                        activity.refreshList();
                    } else {
                        ToastUtils.show(activity, response.getResponseMessage());
                    }
                }
            }

        }.start("请稍候");
    }

    /**
     * 添加评论
     * @param item
     * @param count
     */
    private void addPostToView(final Topic item, final int count) {
        ((LinearLayout) view(POST_LAYOUT)).removeAllViews();
        for (int i = 0; i < count; i++) {
            View v = activity.getLayoutInflater().inflate(R.layout.feed_item_text, null);
            TextView textView = (TextView) v.findViewById(R.id.feed_item_text);
            Post post = item.getPosts().get(i);
            if (post != null) {
                User createUser = post.getCreateUser();
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

                ReplyPost replyPost = post.getReplyPost();
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
                source = source + ": " + post.getMsg();

                SpannableString spannableInfo = new SpannableString(source);

                spannableInfo.setSpan(new Clickable(createUserColor, new OnClick(createUser.getUserId())), 0, createUser.getNickname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (start != 0 || end != 0) {
                    spannableInfo.setSpan(new Clickable(replyUserColor, new OnClick(userId)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                textView.setText(spannableInfo);
                textView.setTag(post);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                ((LinearLayout) view(POST_LAYOUT)).addView(v);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isNicknameClick) {
                            isNicknameClick = false;
                            return;
                        }
                        TextView textView = (TextView) view;
                        final Post post = (Post) textView.getTag();
                        User user = post.getCreateUser();
                        if (user.getUserId().equals(LoginUtil.getUserId(activity))) {
                            return;
                        }
                        Dialog submitDialog = dialogUtils.createPostDialog(activity);
                        if (submitDialog != null && !submitDialog.isShowing()) {
                            submitDialog.show();
                        }
                        dialogUtils.setHintText("回复:" + user.getNickname());
                        dialogUtils.setSubmitOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sendReplyToServer(item, post);
                            }
                        });
                    }
                });
            }
        }
        if (item.getPostsCnt() > 10) {
            View v = activity.getLayoutInflater().inflate(R.layout.feed_item_text, null);
            TextView textView = (TextView) v.findViewById(R.id.feed_item_text);
            textView.setTextColor(activity.getResources().getColor(R.color.white_grey));
            textView.setText("查看全部" + item.getPostsCnt() + "条评论");
            ((LinearLayout) view(POST_LAYOUT)).addView(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.startActivityForResult(new Intents(activity, TopicDetailActivity.class).add(IConstant.TOPIC_ID, item.getId()).toIntent(),0);
                }
            });
        }
    }

    private void supportEventListener(final Topic topic, final TextView supportCount, final TextView supportText) {
        if (topic.getIsFavorit() == 0) {
            //没赞过,执行赞的操作
            execLike(topic, supportCount, supportText);
        } else {
            //赞过,取消赞
            execUnLike(topic, supportCount, supportText);
        }
    }

    private void execUnLike(final Topic topic, final TextView supportCount, final TextView supportText) {
        activity.startUnLikeAnimation();
        topic.setFavoritsCnt(topic.getFavoritsCnt() - 1);
        supportCount.setText((topic.getFavoritsCnt() - 1) + "个赞");
        supportText.setText("赞");
        topic.setIsFavorit(0);
        new AbstractRoboAsyncTask<Response>(activity) {
            @Override
            protected Response run(Object data) throws Exception {
                return (Response) HttpUtils.doRequest(services.createUnLikeRequest(topic.getId())).result;
            }

            @Override
            protected void onSuccess(Response defaultReponse) throws Exception {
                super.onSuccess(defaultReponse);
                if (defaultReponse != null) {
                    if (defaultReponse.getResponseCode() == 0) {
//                        topic.setIsFavorit(0);
                    } else {
                        ToastUtils.show(activity, defaultReponse.getResponseMessage());
                    }
                }
            }
        }.execute();
        notifyDataSetChanged();
    }

    private void execLike(final Topic topic, final TextView supportCount, final TextView supportText) {
        activity.startFullAnimation();
        supportText.setText("已赞");
        topic.setFavoritsCnt(topic.getFavoritsCnt() + 1);
        supportCount.setText((topic.getFavoritsCnt() + 1) + "个赞");
        topic.setIsFavorit(1);
        new AbstractRoboAsyncTask<Response>(activity) {
            @Override
            protected Response run(Object data) throws Exception {
                return (Response) HttpUtils.doRequest(services.createLikeRequest(topic.getId())).result;
            }

            @Override
            protected void onSuccess(Response defaultReponse) throws Exception {
                super.onSuccess(defaultReponse);
                if (defaultReponse != null) {
                    if (defaultReponse.getResponseCode() == 0) {
//                        topic.setIsFavorit(1);
                    } else {
                        ToastUtils.show(activity, defaultReponse.getResponseMessage());
                    }
                }
            }
        }.execute();
        notifyDataSetChanged();
    }

    private void submitPost(final Topic topic) {
        dialogUtils.setHintText("");
        Dialog submitDialog = dialogUtils.createPostDialog(activity);
        if (submitDialog != null && !submitDialog.isShowing()) {
            submitDialog.show();
        }
        dialogUtils.setSubmitOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCommentToServer(topic);
            }
        });
    }

    /**
     * 回复评论
     */
    private void sendReplyToServer(final Topic topic, final Post post) {
        String message = dialogUtils.getEditText().getText().toString();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        final Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("id", post.getId());
        bodys.put("msg", message);
        new ProgressDialogTask<Response>(activity) {

            @Override
            protected Response run(Object data) throws Exception {
                return (Response) HttpUtils.doRequest(services.createReplyRequest(bodys)).result;
            }

            @Override
            protected void onSuccess(Response response) throws Exception {
                super.onSuccess(response);
                if (response != null) {
                    if (response.getResponseCode() == 0) {
                        dialogUtils.hideKeyboard();
                        dialogUtils.createPostDialog(activity).dismiss();
                        dialogUtils.setEditContentText("");
                        refreshComment(topic);
                    } else {
                        ToastUtils.show(activity, response.getResponseMessage());
                    }
                }
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                super.onException(e);
            }
        }.start("请稍候");
    }

    /**
     * 发表评论
     *
     * @param topic
     */
    private void sendCommentToServer(final Topic topic) {
        String message = dialogUtils.getEditText().getText().toString();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        final Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("id", topic.getId());
        bodys.put("msg", message);
        new ProgressDialogTask<Response>(activity) {

            @Override
            protected Response run(Object data) throws Exception {
                return (Response) HttpUtils.doRequest(services.createCommentRequest(bodys)).result;
            }

            @Override
            protected void onSuccess(Response response) throws Exception {
                super.onSuccess(response);
                if (response != null) {
                    if (response.getResponseCode() == 0) {
                        dialogUtils.hideKeyboard();
                        dialogUtils.createPostDialog(activity).dismiss();
                        dialogUtils.setEditContentText("");
                        refreshComment(topic);
                    } else {
                        ToastUtils.show(activity, response.getResponseMessage());
                    }
                }
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                super.onException(e);
            }
        }.start("请稍候");
    }

    /**
     * 刷新评论
     *
     * @param item
     */
    private void refreshComment(final Topic item) {
        new AbstractRoboAsyncTask<Topic>(activity) {
            @Override
            protected Topic run(Object data) throws Exception {
                return (Topic) HttpUtils.doRequest(services.createCommentRequest(item.getId(), 1, 10)).result;
            }

            @Override
            protected void onSuccess(Topic topic) throws Exception {
                super.onSuccess(topic);
                if (topic != null) {
                    item.setPostsCnt(topic.getTotal());
                    item.setPosts(topic.getPosts());
                    notifyDataSetChanged();
                }
            }
        }.execute();
    }

    private class OnClick implements View.OnClickListener {

        private final String userId;

        public OnClick(final String userId) {
            this.userId = userId;
        }

        @Override
        public void onClick(View view) {
            if (!TextUtils.isEmpty(userId)) {
                isNicknameClick = true;
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
