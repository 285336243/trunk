package com.socialtv.topic;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.Keyboard;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.kevinsawicki.wishlist.ViewFinder;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.socialtv.R;
import com.socialtv.Response;
import com.socialtv.SharePage;
import com.socialtv.ShareTemplete;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.FragmentProvider;
import com.socialtv.core.Intents;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.SingleListActivity;
import com.socialtv.core.ThrowableLoader;
import com.socialtv.core.ToastUtils;
import com.socialtv.core.Utils;
import com.socialtv.http.HttpUtils;
import com.socialtv.message.entity.Post;
import com.socialtv.personcenter.LoginActivity;
import com.socialtv.publicentity.Topic;
import com.socialtv.publicentity.User;
import com.socialtv.publicentity.UserBadge;
import com.socialtv.topic.entity.TopicHeader;
import com.socialtv.util.DialogUtils;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;
import com.socialtv.util.LoginUtil;
import com.socialtv.util.StartOtherFeedUtil;
import com.socialtv.view.ExpandableTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-7-1.
 * 话题详情页面
 */
public class TopicDetailActivity extends SingleListActivity<Topic> {

    private final static int HEADER_ID = 100;

    @Inject
    private Activity activity;

    @Inject
    private TopicServices services;

    @InjectExtra(value = IConstant.TOPIC_ID, optional = true)
    private String topicId;

    @Inject
    private TopicDetailAdapter adapter;

    private List<Post> posts = new ArrayList<Post>();

    private String userId;

    private View avatarLayout;

    private ImageView avatarImageView;

    private TextView nicknameTextView;

    private TextView createTimeTextView;

    private ExpandableTextView descTextView;

    private ImageView descImageView;

    private View descImageLayoutView;

    private GridView descImageGridView;

    private View supportLayout;

    private TextView supportCount;

    private LinearLayout badgeLayout;

    private DialogUtils dialogUtils;

    @InjectView(R.id.topic_detail_submit_comment_layout)
    private View sumitCommentLayout;

    @InjectView(R.id.topic_detail_like_button)
    private ImageButton likeButton;

    @InjectView(R.id.topic_detail_like_edit)
    private EditText editText;

    @InjectView(R.id.topic_detail_like_publish)
    private TextView publishView;

    @InjectView(R.id.feed_like_anim_full)
    private ImageView likFullAnimView;

    @InjectView(R.id.feed_un_like_anim_layout)
    private View unLikeAnimView;

    @InjectView(R.id.feed_like_anim_left)
    private ImageView likeLeftAnimView;

    @InjectView(R.id.feed_like_anim_right)
    private ImageView likeRightAnimView;

    private AnimatorSet likeFullSet;

    private AnimatorSet unLikeLeftSet;

    private AnimatorSet unLikeRightSet;

    private int width;

    private TopicHeaderImageAdapter imageAdapter;

    private Dialog moreDialog;

    private View deleteTopicView;

    private View reportView;

    private View sinaShareView;

    private View tencentShareView;

    private View weixinShareView;

    private View weixinFriendView;

    private View cancelView;

    private View deleteLineView;

    private View reportLineView;

    private Dialog deleteTipDialog;

    private boolean isReply = false;

    private final Intent resultIntent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.width = metrics.widthPixels;

        dialogUtils = new DialogUtils();

        getSupportActionBar().setLogo(R.drawable.btn_back_tomato);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("详情");

        getSupportLoaderManager().initLoader(HEADER_ID, null, headerCallbacks);

        moreDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        moreDialog.setContentView(R.layout.topic_detail_more_layout);
        deleteLineView = moreDialog.findViewById(R.id.topic_detail_more_delete_line);
        reportLineView = moreDialog.findViewById(R.id.topic_detail_more_report_line);
        deleteTopicView = moreDialog.findViewById(R.id.topic_detail_more_delete);
        reportView = moreDialog.findViewById(R.id.topic_detail_more_report);
        sinaShareView = moreDialog.findViewById(R.id.topic_detail_more_share_sina);
        tencentShareView = moreDialog.findViewById(R.id.topic_detail_more_share_tencent);
        weixinShareView = moreDialog.findViewById(R.id.topic_detail_more_share_weixin);
        weixinFriendView = moreDialog.findViewById(R.id.topic_detail_more_share_weixin_friend);
        cancelView = moreDialog.findViewById(R.id.topic_detail_more_cancel);
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moreDialog.isShowing())
                    moreDialog.dismiss();
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.topic_detail_list;
    }

    LoaderManager.LoaderCallbacks<TopicHeader> headerCallbacks = new LoaderManager.LoaderCallbacks<TopicHeader>() {
        @Override
        public Loader<TopicHeader> onCreateLoader(int id, Bundle args) {
            return new ThrowableLoader<TopicHeader>(activity) {
                @Override
                public TopicHeader loadData() throws Exception {
                    return (TopicHeader) HttpUtils.doRequest(services.createTopicHeaderRequest(topicId)).result;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<TopicHeader> loader, final TopicHeader data) {
            if (data != null) {
                if (data.getResponseCode() == 0) {
                    if (data.getTopic() != null) {
                        if (data.getTopic().getUser() != null) {
                            userId = data.getTopic().getUser().getUserId();
                            ImageLoader.getInstance().displayImage(data.getTopic().getUser().getAvatar(), avatarImageView, ImageUtils.imageLoader(activity, 1000));
                            nicknameTextView.setText(data.getTopic().getUser().getNickname());

                            avatarLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    StartOtherFeedUtil.startOtherFeed(activity, data.getTopic().getUser().getUserId());
                                }
                            });

                            if (data.getTopic().getUser().getBadges() != null && !data.getTopic().getUser().getBadges().isEmpty()) {
                                show(badgeLayout);
                                badgeLayout.removeAllViews();
                                for (UserBadge badge : data.getTopic().getUser().getBadges()) {
                                    if (badge != null) {
                                        ImageView imageView = new ImageView(activity);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dip2px(activity, 12), Utils.dip2px(activity, 12));
                                        params.gravity = Gravity.CENTER_VERTICAL;
                                        imageView.setLayoutParams(params);
                                        ImageLoader.getInstance().displayImage(badge.getImg(), imageView, ImageUtils.imageLoader(activity, 0));
                                        badgeLayout.addView(imageView);
                                    }
                                }
                            } else {
                                hide(badgeLayout);
                            }
                        }
                        createTimeTextView.setText(data.getTopic().getCreateTime());
                        if (!TextUtils.isEmpty(data.getTopic().getMessage())) {
                            descTextView.setText(data.getTopic().getMessage());
                        } else {
                            hide(descTextView);
                        }
                        if (data.getTopic().getFavoritsCnt() <= 0) {
                            hide(supportLayout);
                        } else {
                            show(supportLayout);
                            supportCount.setText(data.getTopic().getFavoritsCnt() + "个赞");
                        }

                        if (data.getTopic().getPics() != null && !data.getTopic().getPics().isEmpty()) {
                            if (data.getTopic().getPics().size() == 1) {
                                //单张图
                                hide(descImageGridView);
                                show(descImageLayoutView);
                                ImageLoader.getInstance().displayImage(data.getTopic().getPics().get(0).getImg() + ".296x296", descImageView, ImageUtils.imageLoader(activity, 0));
                                descImageLayoutView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogUtils.setSingleImageOnClick(activity, descImageLayoutView, data.getTopic().getPics().get(0).getImg());
                                    }
                                });
                            } else {
                                //多张图
                                hide(descImageLayoutView);
                                show(descImageGridView);
                                int count = (data.getTopic().getPics().size() + 2) / 3;
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width - Utils.dip2px(activity, 24), ((int) (width * 0.225) * count + Utils.dip2px(activity, (count - 1) * 4)));
                                params.topMargin = Utils.dip2px(activity, 12);
                                descImageGridView.setLayoutParams(params);
                                imageAdapter.addItems(0, data.getTopic().getPics());
                                descImageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        dialogUtils.showImageDetailDialog(activity, data.getTopic().getPics(), position);
                                    }
                                });

                            }
                        }
                        if (data.getTopic().getIsFavorit() == 0) {
                            likeButton.setBackgroundResource(R.drawable.btn_topiclike_toamto);
                        } else {
                            likeButton.setBackgroundResource(R.drawable.btn_topiclikeactive_toamto);
                        }

                        likeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (LoginUtil.isLogin(activity)) {
                                    if (data.getTopic().getIsFavorit() == 0) {
                                        execLike(data.getTopic());
                                    } else {
                                        execUnLike(data.getTopic());
                                    }
                                } else {
                                    activity.startActivity(new Intents(activity, LoginActivity.class).toIntent());
                                }
                            }
                        });
                    }
                    shareMessage(data);
                    show(sumitCommentLayout);
                    setPublishOnClick(null);
                } else {
                    ToastUtils.show(activity, data.getResponseMessage());
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<TopicHeader> loader) {

        }
    };

    private void setPublishOnClick(final Post post) {
        publishView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isReply) {
                    isReply = false;
                    if (post != null) {
                        sendReplyToServer(post);
                    }
                } else {
                    sendCommentToServer();
                }
            }
        });
    }

    /**
     * 回复评论
     */
    private void sendReplyToServer(final Post post) {
        String message = editText.getText().toString();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        final Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("id", post.getId());
        bodys.put("msg", message);
        new ProgressDialogTask<Response>(activity){

            @Override
            protected Response run(Object data) throws Exception {
                return (Response) HttpUtils.doRequest(services.createReplyRequest(bodys)).result;
            }

            @Override
            protected void onSuccess(Response response) throws Exception {
                super.onSuccess(response);
                if (response != null) {
                    if (response.getResponseCode() == 0) {
                        editText.setText("");
                        editText.setHint(getResources().getString(R.string.posts));
                        requestPage = 1;
                        posts.clear();
                        Keyboard.hideSoftInput(editText);
                        refresh();
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

    private void sendCommentToServer() {
        String message = editText.getText().toString();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        final Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("id", topicId);
        bodys.put("msg", message);
        new ProgressDialogTask<Response>(activity){

            @Override
            protected Response run(Object data) throws Exception {
                return (Response) HttpUtils.doRequest(services.createCommentRequest(bodys)).result;
            }

            @Override
            protected void onSuccess(Response response) throws Exception {
                super.onSuccess(response);
                if (response != null) {
                    if (response.getResponseCode() == 0) {
                        editText.setText("");
                        requestPage = 1;
                        posts.clear();
                        Keyboard.hideSoftInput(editText);
                        refresh();
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

    private void execUnLike(final Topic topic) {
        startUnLikeAnimation();
        supportCount.setText((topic.getFavoritsCnt() - 1) + "个赞");
        topic.setFavoritsCnt(topic.getFavoritsCnt() - 1);
        if (topic.getFavoritsCnt() <= 0) {
            hide(supportLayout);
        }
        likeButton.setBackgroundResource(R.drawable.btn_topiclike_toamto);
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
                        resultIntent.putExtra(IConstant.TOPIC_LIKE_UPDATE, topic);
                        setResult(RESULT_OK, resultIntent);
                    } else {
                        ToastUtils.show(activity, defaultReponse.getResponseMessage());
                    }
                }
            }
        }.execute();
        notifyDataSetChanged();
    }

    private void execLike(final Topic topic) {
        startFullAnimation();
        likeButton.setBackgroundResource(R.drawable.btn_topiclikeactive_toamto);
        supportCount.setText((topic.getFavoritsCnt() + 1) + "个赞");
        topic.setFavoritsCnt(topic.getFavoritsCnt() + 1);
        show(supportLayout);
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
                        resultIntent.putExtra(IConstant.TOPIC_LIKE_UPDATE, topic);
                        setResult(RESULT_OK, resultIntent);
                    } else {
                        ToastUtils.show(activity, defaultReponse.getResponseMessage());
                    }
                }
            }
        }.execute();
        notifyDataSetChanged();
    }

    private void shareMessage(final TopicHeader data) {
        final ShareTemplete templete = data.getShareTemplete();
        sinaShareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (templete != null) {
                    if (TextUtils.isEmpty(templete.getShareImg())) {
                        sinaShareText(templete.getShareText());
                    } else {
                        ImageLoader.getInstance().loadImage(templete.getShareImg(), ImageUtils.imageLoader(activity, 0), new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view, loadedImage);
                                sinaSharePic(loadedImage, templete.getShareText());
                            }
                        });
                    }
                }
                if (moreDialog != null && moreDialog.isShowing())
                    moreDialog.dismiss();
            }
        });

        tencentShareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (templete != null) {
                    if (TextUtils.isEmpty(templete.getShareImg())) {
                        tencentShareText(templete.getShareText());
                    } else {
                        ImageLoader.getInstance().loadImage(templete.getShareImg(), ImageUtils.imageLoader(activity, 0), new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view, loadedImage);
                                tencentSharePic(loadedImage, templete.getShareText());
                            }
                        });
                    }
                }
                if (moreDialog != null && moreDialog.isShowing())
                    moreDialog.dismiss();
            }
        });

        weixinShareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (templete != null) {
                    final SharePage page = templete.getSharePage();
                    if (page != null) {
                        if (TextUtils.isEmpty(page.getIcon())) {
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
                            shareWeiXinWebPage(page.getUrl(), page.getTitle(), page.getDesc(), bitmap);
                        } else {
                            ImageLoader.getInstance().loadImage(page.getIcon(), new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    super.onLoadingComplete(imageUri, view, loadedImage);
                                    shareWeiXinWebPage(page.getUrl(), page.getTitle(), page.getDesc(), loadedImage);
                                }
                            });
                        }
                    }
                }
                if (moreDialog != null && moreDialog.isShowing())
                    moreDialog.dismiss();
            }
        });

        weixinFriendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (templete != null) {
                    final SharePage page = templete.getSharePage();
                    if (page != null) {
                        if (TextUtils.isEmpty(page.getIcon())) {
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
                            shareWeiXinWebPage(page.getUrl(), page.getTitle(), page.getDesc(), bitmap);
                        } else {
                            ImageLoader.getInstance().loadImage(page.getIcon(), new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    super.onLoadingComplete(imageUri, view, loadedImage);
                                    shareWeiXinFriendWebPage(page.getUrl(), page.getTitle(), page.getDesc(), loadedImage);
                                }
                            });
                        }
                    }
                }
                if (moreDialog != null && moreDialog.isShowing())
                    moreDialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && resultCode != RESULT_CANCELED) {
            setResult(RESULT_OK);
            requestPage = 1;
            getSupportLoaderManager().restartLoader(HEADER_ID, null, headerCallbacks);
            refresh();
        }
    }

    /**
     * Create adapter to display items
     *
     * @return adapter
     */
    @Override
    protected SingleTypeAdapter<?> createAdapter() {
        return adapter;
    }

    /**
     * Is add adapter header
     *
     * @return
     */
    @Override
    protected boolean isAddAdapterHeader() {
        return true;
    }

    /**
     * Adapter header view
     *
     * @return
     */
    @Override
    protected View adapterHeaderView() {
        View v = getLayoutInflater().inflate(R.layout.topic_detail_header, null);
        ViewFinder finder = new ViewFinder(v);
        avatarLayout = finder.find(R.id.topic_detail_avatar_layout);
        avatarImageView = finder.find(R.id.topic_detail_avatar);
        nicknameTextView = finder.find(R.id.topic_detail_nickname);
        createTimeTextView = finder.find(R.id.topic_detail_createtime);
        descTextView = finder.find(R.id.topic_detail_expand_text_view);
        descImageView = finder.find(R.id.topic_detail_desc_image);
        descImageLayoutView = finder.find(R.id.topic_detail_desc_image_layout);
        descImageGridView = finder.find(R.id.topic_detail_desc_image_grid);
        supportLayout = finder.find(R.id.topic_detail_support_layout);
        supportCount = finder.find(R.id.topic_detail_support_count);
        badgeLayout = finder.find(R.id.topic_detail_badge_layout);
        imageAdapter = new TopicHeaderImageAdapter(this);
        descImageGridView.setAdapter(imageAdapter);
        return v;
    }

    /**
     * Create Request
     *
     * @return
     */
    @Override
    protected Request<Topic> createRequest() {
        return services.createPostsRequest(topicId, requestPage, requestCount);
    }

    @Override
    public void onLoadFinished(Loader<Topic> loader, Topic data) {
        super.onLoadFinished(loader, data);
        if (data != null) {
            if (data.getResponseCode() == 0) {
                if (data.getPosts() != null && !data.getPosts().isEmpty()) {

                    //发布评论成功之后设置result
                    data.setId(topicId);
                    resultIntent.putExtra(IConstant.TOPIC_POST_UPDATE, data);
                    setResult(RESULT_OK, resultIntent);

                    this.items = data.getPosts();
                    posts.addAll(data.getPosts());
                    adapter.addItem(posts, "");

                    if (data.getPosts().size() < requestCount)
                        loadingIndicator.setVisible(false);
                } else {
                    hasMore = true;
                    loadingIndicator.setVisible(false);
                    if (posts.isEmpty()) {
                        Post post = new Post();
                        post.setMsg("还没有评论...");
                        List<Post> items = new ArrayList<Post>();
                        adapter.addItem(items, "empty");
                    }
                }
            } else{
                ToastUtils.show(activity, data.getResponseMessage());
            }
        }
        showList();
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        this.items.clear();
        this.posts.clear();
        loadingIndicator.setVisible(true);
        super.onRefresh(refreshView);
    }

    /**
     * Get error message to display for exception
     *
     * @param exception
     * @return string resource id
     */
    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.topic_detail_error;
    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    @Override
    protected int getLoadingMessage() {
        return R.string.topic_detail_loading;
    }

    /**
     * Get provider of the currently selected fragment
     *
     * @return fragment provider
     */
    @Override
    protected FragmentProvider getProvider() {
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, 0, "更多")
                .setIcon(R.drawable.btn_topmore_tomato)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case 0:
                if (LoginUtil.isLogin(activity)) {
                    showMoreDialog();
                } else {
                    startActivity(new Intents(activity, LoginActivity.class).toIntent());
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMoreDialog() {
        if (!LoginUtil.getUserId(activity).equals(userId)) {
            hide(deleteTopicView);
            hide(deleteLineView);
        } else {
            hide(reportView);
            hide(reportLineView);
        }
        if (!moreDialog.isShowing()) {
            moreDialog.show();
        }

        setDeleteTopicClickListener();
        setReportClickListener();
    }

    private void setReportClickListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moreDialog != null && moreDialog.isShowing()) {
                    moreDialog.dismiss();
                }
                final Map<String, String> bodys = new HashMap<String, String>();
                bodys.put("id", topicId);
                bodys.put("type", "TOPIC");
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
                        return (Response) HttpUtils.doRequest(services.createReportRequest(bodys)).result;
                    }

                    @Override
                    protected void onSuccess(Response response) throws Exception {
                        super.onSuccess(response);
                        if (response != null) {
                            if (response.getResponseCode() == 0) {
                                ToastUtils.show(activity, "举报成功");
                            } else {
                                ToastUtils.show(activity, response.getResponseMessage());
                            }
                        }
                    }
                }.start("正在举报");
            }
        };
        reportView.setOnClickListener(listener);
    }

    private void setDeleteTopicClickListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moreDialog != null && moreDialog.isShowing()) {
                    moreDialog.dismiss();
                }
                showDeleteTipDialog();
            }
        };
        deleteTopicView.setOnClickListener(listener);
    }

    private void showDeleteTipDialog() {
        if (deleteTipDialog == null) {
            deleteTipDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        }
        deleteTipDialog.setContentView(R.layout.delete_topic_tip_dialog);
        View cancel = deleteTipDialog.findViewById(R.id.delete_topic_dialog_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteTipDialog != null && deleteTipDialog.isShowing()) {
                    deleteTipDialog.dismiss();
                }
            }
        });
        View delete = deleteTipDialog.findViewById(R.id.delete_topic_dialog_delete);


        View.OnClickListener deleteListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                //这里发送广播是为了让FeedItemFragment接收,并刷新数据的
                                //因为嵌套fragment不能接收onActivityForResult
                                sendBroadcast(new Intent(IConstant.DELETE_OK));
                                Intent intent = new Intent(IConstant.DELETE_OK).putExtra(IConstant.TOPIC_ID, topicId);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                ToastUtils.show(activity, response.getResponseMessage());
                            }
                        }
                    }

                }.start("正在删除话题...");
            }
        };
        delete.setOnClickListener(deleteListener);
        if (!deleteTipDialog.isShowing())
            deleteTipDialog.show();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Post post = (Post) l.getItemAtPosition(position);
        if (post != null) {
            User createUser = post.getCreateUser();
            if (createUser != null && !createUser.getUserId().equals(LoginUtil.getUserId(activity))) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
                editText.setHint("回复" + createUser.getNickname());
                isReply = true;
                setPublishOnClick(post);
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            editText.setHint("评论");
            editText.setText("");
            setPublishOnClick(null);
        }
        return super.dispatchKeyEvent(event);
    }

    private void createFullAnimation() {
        if (likeFullSet == null) {
            ObjectAnimator alphaFirst = ObjectAnimator.ofFloat(likFullAnimView, "alpha", 0.0f, 1.0f);
            ObjectAnimator scaleFirstX = ObjectAnimator.ofFloat(likFullAnimView, "scaleX", 1.0f, 1.5f);
            ObjectAnimator scaleFirstY = ObjectAnimator.ofFloat(likFullAnimView, "scaleY", 1.0f, 1.5f);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(likFullAnimView, "scaleX", 1.5f, 0.8f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(likFullAnimView, "scaleY", 1.5f, 0.8f);
            ObjectAnimator zoomOut = ObjectAnimator.ofFloat(likFullAnimView, "scaleX", 0.8f, 1.5f);
            ObjectAnimator zoomIn = ObjectAnimator.ofFloat(likFullAnimView, "scaleY", 0.8f, 1.5f);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(likFullAnimView, "alpha", 1.0f, 0.0f);
            likeFullSet = new AnimatorSet();
            likeFullSet.setInterpolator(new DecelerateInterpolator());
            AnimatorSet set = new AnimatorSet();
            set.play(alphaFirst).with(scaleFirstX).with(scaleFirstY);
            set.setDuration(200);
            AnimatorSet set1 = new AnimatorSet();
            set1.play(scaleX).with(scaleY);
            set1.setDuration(200);
            AnimatorSet set2 = new AnimatorSet();
            set2.play(zoomOut).with(zoomIn);
            set2.setDuration(400);
            AnimatorSet set3 = new AnimatorSet();
            set3.play(scaleX).with(scaleY);
            set3.setDuration(200);
            AnimatorSet set4 = new AnimatorSet();
            set4.play(zoomOut).with(zoomIn);
            set4.setDuration(200);
            AnimatorSet alphaSet = new AnimatorSet();
            alphaSet.setDuration(400);
            alphaSet.play(scaleX).with(scaleY).with(alpha);
            likeFullSet.playSequentially(set, set1, set2, set3, set4, alphaSet);
            likeFullSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    likFullAnimView.setVisibility(View.VISIBLE);
                    unLikeAnimView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    likFullAnimView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    public void startFullAnimation() {
        createFullAnimation();
        if (likeFullSet.isRunning()) {
            likFullAnimView.setVisibility(View.GONE);
            likeFullSet.cancel();
        }
        likeFullSet.start();
    }

    public void startUnLikeAnimation() {
        createUnLikeAnimation();
        if (unLikeLeftSet.isRunning() && unLikeRightSet.isRunning()) {
            unLikeAnimView.setVisibility(View.GONE);
            unLikeLeftSet.cancel();
            unLikeRightSet.cancel();
        }
        AnimatorSet set = new AnimatorSet();
//        set.playSequentially(unLikeLeftSet, unLikeRightSet);
        set.playTogether(unLikeLeftSet, unLikeRightSet);
        set.start();
    }

    private void createUnLikeAnimation() {
        if (unLikeLeftSet == null) {
//            ObjectAnimator translateAnimation = ObjectAnimator.ofFloat(likeLeftAnimView, "translationY", ViewCompat.getTranslationY(likeLeftAnimView), ViewCompat.getTranslationY(likeLeftAnimView) + 500);
            ObjectAnimator viewTranslateAnimation = ObjectAnimator.ofFloat(unLikeAnimView, "translationY", ViewCompat.getTranslationY(likeLeftAnimView), ViewCompat.getTranslationY(likeLeftAnimView) + 100);

            ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(likeLeftAnimView, "rotationX", 0.0F, -15.0F);
            unLikeLeftSet = new AnimatorSet();
//            unLikeLeftSet.setInterpolator(new AccelerateInterpolator());
            unLikeLeftSet.setDuration(2000);
            unLikeLeftSet.playTogether(viewTranslateAnimation, rotateAnimation);
            unLikeLeftSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    likFullAnimView.setVisibility(View.GONE);
                    unLikeAnimView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    unLikeAnimView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        if (unLikeRightSet == null) {
//            ObjectAnimator translateAnimation = ObjectAnimator.ofFloat(likeRightAnimView, "translationY",  ViewCompat.getTranslationY(likeLeftAnimView), ViewCompat.getTranslationY(likeLeftAnimView) + 500);
            ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(likeRightAnimView, "rotationX", 0.0F, 15.0F);
            unLikeRightSet = new AnimatorSet();
//            unLikeRightSet.setInterpolator(new AccelerateInterpolator());
            unLikeRightSet.setDuration(2000);
            unLikeRightSet.playTogether(rotateAnimation);
        }

    }
}
