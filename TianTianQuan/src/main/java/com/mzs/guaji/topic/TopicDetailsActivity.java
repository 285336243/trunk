package com.mzs.guaji.topic;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;
import com.github.kevinsawicki.wishlist.ViewFinder;
import com.github.kevinsawicki.wishlist.ViewUtils;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.TopicDetailsEmptyAdapter;
import com.mzs.guaji.core.AbstractRoboAsyncTask;
import com.mzs.guaji.core.Intents;
import com.mzs.guaji.core.PageIterator;
import com.mzs.guaji.core.ProgressDialogTask;
import com.mzs.guaji.core.ResourcePager;
import com.mzs.guaji.core.ThirdPartyShareActivity;
import com.mzs.guaji.core.ThrowableLoader;
import com.mzs.guaji.core.Utils;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.http.BodyRequest;
import com.mzs.guaji.http.HttpUtils;
import com.mzs.guaji.offical.OfficialTvCircleActivity;
import com.mzs.guaji.topic.entity.Post;
import com.mzs.guaji.topic.entity.StarDetail;
import com.mzs.guaji.topic.entity.StatisticsRequest;
import com.mzs.guaji.topic.entity.Topic;
import com.mzs.guaji.topic.entity.TopicDetails;
import com.mzs.guaji.topic.entity.TopicDetailsPosts;
import com.mzs.guaji.ui.ImageDetailsActivity;
import com.mzs.guaji.ui.LoginActivity;
import com.mzs.guaji.ui.StarCenterActivity;
import com.mzs.guaji.ui.TopicCommentActivity;
import com.mzs.guaji.ui.TopicHomeActivity;
import com.mzs.guaji.util.BroadcastActionUtil;
import com.mzs.guaji.util.IConstant;
import com.mzs.guaji.util.ImageUtils;
import com.mzs.guaji.util.LoginUtil;
import com.mzs.guaji.util.SkipPersonalCenterUtil;
import com.mzs.guaji.util.StringUtil;
import com.mzs.guaji.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-5-22.
 */
@ContentView(R.layout.topic_details)
public class TopicDetailsActivity extends ThirdPartyShareActivity implements LoaderManager.LoaderCallbacks<TopicDetailsPosts>, PullToRefreshBase.OnRefreshListener<ExpandableListView>, PullToRefreshBase.OnLastItemVisibleListener {

    private final static String SHARETITLE = "#天天圈#我在%s分享了一个话题:%s";

    private final static int POSTS_ID = 0;

    private final static int HEADER_ID = 1;

    private final static int CELEBRITY_ID = 2;

    private int requestPage = 1;

    @Inject
    TopicDetailsService service;

    @Inject
    Context context;

    @Inject
    TopicDetailsAdapter adapter;

    @InjectView(R.id.topic_details_back)
    View backView;

    @InjectView(R.id.topic_details_more)
    View moreView;

    @InjectView(R.id.topic_details_list)
    PullToRefreshExpandableListView expandableListView;

    @InjectView(R.id.topic_details_loading)
    ProgressBar progressBar;

    @InjectView(R.id.topic_details_share_layout)
    View shareView;

    @InjectView(R.id.topic_details_comment_layout)
    View commentView;

    @InjectView(R.id.topic_details_link_layout)
    View linkView;

    @InjectView(R.id.topic_details_like_text)
    TextView likeText;

    @InjectView(R.id.anim_layout)
    LinearLayout animLayout;

    private ResourcePager<TopicDetailsPosts> pager;

    private ResourcePager<StarDetail> celebrityPager;

    private ResourcePager<TopicDetails> headerPager;

    @InjectExtra(IConstant.TOPIC_ID)
    private long topicId;

    private String type;

    private ImageLoader imageLoader;

    private ExpandableListView listView;

    private View headerView;

    private ImageView avatarImage;

    private TextView nicknameText;

    private TextView fromText;

    private TextView titleText;

    private TextView descText;

    private View imageView;

    private ImageView headerImage;

    private TextView createTimeText;

    private TextView likeCountText;

    private TextView postCountText;

    private View celebrityPostText;

    private LinearLayout celebrityPostContent;

    private TextView loadingText;

    private ProgressBar loadingProgressBar;

    private View footView;

    protected boolean listShown;

    private boolean isEmpty = true;

    private boolean hasMore = false;

    private int width;

    private int height;

    private final MediaPlayer mediaPlayer = new MediaPlayer();

    private ImageView voiceImage;

    private int animCount = 0;

    private long time = 0;

    private long pauseTime = 0;

    private long totalTime = 0;

    private boolean isRefresh = false;

    private ReplyBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        type = getStringExtra(IConstant.TOPIC_TYPE);
        if (TextUtils.isEmpty(type)) {
            type = "group";
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.width = metrics.widthPixels;
        this.height = metrics.heightPixels;

        imageLoader = ImageLoader.getInstance();

        expandableListView.setOnRefreshListener(this);
        expandableListView.setOnLastItemVisibleListener(this);
        listView = expandableListView.getRefreshableView();
        listView.setGroupIndicator(null);

        headerView = getLayoutInflater().inflate(R.layout.topic_detail_header, null);
        ViewFinder finder = new ViewFinder(headerView);
        avatarImage = finder.find(R.id.topic_details_header_avatar);
        nicknameText = finder.find(R.id.topic_details_header_nickname);
        fromText = finder.find(R.id.topic_details_header_form);
        titleText = finder.find(R.id.topic_details_header_title);
        descText = finder.find(R.id.topic_details_header_desc);
        imageView = finder.find(R.id.topic_details_header_image_layout);
        headerImage = finder.find(R.id.topic_details_header_pic_image);
        createTimeText = finder.find(R.id.topic_details_header_createtime);
        likeCountText = finder.find(R.id.topic_details_header_like_count);
        postCountText = finder.find(R.id.topic_details_header_post_count);
        celebrityPostText = finder.find(R.id.topic_detail_header_star_post);
        celebrityPostContent = finder.find(R.id.topic_detail_header_star_content);
        addListViewHeader();

        adapter.setType(type);
        adapter.setExpandableListView(listView);
        adapter.setTopicDetailsService(service);
        listView.setAdapter(adapter);

        pager = createPager();
        headerPager = createHeaderPager();
        celebrityPager = createCelebrityPostsPager();
        getSupportLoaderManager().initLoader(0, null, this);

        commentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginUtil.isLogin(context)) {
                    Intent intent = new Intents(context, TopicCommentActivity.class)
                            .add(IConstant.TOPIC_ID, topicId)
                            .add(IConstant.TOPIC_TYPE, type).toIntent();
                    startActivityForResult(intent, 0);
                } else {
                    startActivity(new Intents(context, LoginActivity.class).toIntent());
                }
            }
        });
        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < adapter.getGroupCount(); i++) {
                    if (groupPosition != i) {
                        listView.collapseGroup(i);
                    }
                }
            }
        });

        receiver = new ReplyBroadcastReceiver();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(BroadcastActionUtil.REPLY_OK);
        registerReceiver(receiver, mFilter);
    }

    private void setMoreClickListener(final TopicDetails data) {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog mDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.setContentView(R.layout.topic_more_layout);
                Button cancelButton = (Button) mDialog.findViewById(R.id.topic_more_cancel);
                Button reportButton = (Button) mDialog.findViewById(R.id.topic_more_report_content);
                if (data.getTopic() != null && data.getTopic().getUserId() == LoginUtil.getUserId(context)) {
                    reportButton.setText("删除");
                }
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mDialog.isShowing())
                            mDialog.dismiss();
                    }
                });
                reportButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (data.getTopic() != null && data.getTopic().getUserId() == LoginUtil.getUserId(context)) {
                            new ProgressDialogTask<DefaultReponse>(context) {
                                @Override
                                protected DefaultReponse run(Object data) throws Exception {
                                    return new ResourcePager<DefaultReponse>() {
                                        @Override
                                        public PageIterator<DefaultReponse> createIterator(int page, int count) {
                                            return service.pageDeleteTopic(topicId, type);
                                        }
                                    }.start();
                                }

                                @Override
                                protected void onSuccess(DefaultReponse defaultReponse) throws Exception {
                                    super.onSuccess(defaultReponse);
                                    if (defaultReponse != null) {
                                        if (defaultReponse.getResponseCode() == 0) {
                                            ToastUtil.showToast(context, "删除成功");
                                            sendBroadcast(new Intent(BroadcastActionUtil.DELETE_TOPIC));
                                            finish();
                                        }
                                    }
                                }
                            }.start("正在删除...");
                        } else {
                            final Map<String, String> bodyMap = new HashMap<String, String>();
                            bodyMap.put("id", topicId + "");
                            bodyMap.put("type", "TOPIC");
                            bodyMap.put("comment", "");
                            new ProgressDialogTask<DefaultReponse>(context) {
                                @Override
                                protected DefaultReponse run(Object data) throws Exception {
                                    return new ResourcePager<DefaultReponse>() {
                                        @Override
                                        public PageIterator<DefaultReponse> createIterator(int page, int count) {
                                            return service.pageExpose(bodyMap);
                                        }
                                    }.start();
                                }

                                @Override
                                protected void onSuccess(DefaultReponse defaultReponse) throws Exception {
                                    super.onSuccess(defaultReponse);
                                    if (defaultReponse != null) {
                                        if (defaultReponse.getResponseCode() == 0) {
                                            ToastUtil.showToast(context, "举报成功");
                                            if (mDialog.isShowing()) {
                                                mDialog.dismiss();
                                            }
                                        } else {
                                            ToastUtil.showToast(context, defaultReponse.getResponseMessage());
                                        }
                                    }
                                }
                            }.start("正在提交举报");
                        }
                    }
                });
                if (!mDialog.isShowing())
                    mDialog.show();
            }
        };
        moreView.setOnClickListener(clickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            adapter.clear();
            requestPage = 1;
            isEmpty = true;
            hasMore = false;
            loadingProgressBar.setVisibility(View.VISIBLE);
            loadingText.setText("正在加载评论...");
            getSupportLoaderManager().restartLoader(POSTS_ID, null, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        expandableListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
        time = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseTime = System.currentTimeMillis();
        totalTime = pauseTime - time + totalTime;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();

        unregisterReceiver(receiver);

        StatisticsRequest request = new StatisticsRequest();
        request.setUserId(LoginUtil.getUserId(this) + "");
        request.setItemId(topicId + "");
        String itemType = "";
        if ("activity".equals(type)) {
            itemType = "ACTIVITY_TOPIC";
        } else if ("celebrity".equals(type)) {
            itemType = "CELEBRIT_TOPIC";
        } else if ("group".equals(type)) {
            itemType = "GROUP_TOPIC";
        }
        request.setItemType(itemType);
        request.setDuring(totalTime);
        request.setActionType("VIEW");
        final String url = "http://statistics.ttq.51wala.com/";
        BodyRequest<DefaultReponse> bodyRequest = new BodyRequest<DefaultReponse>(Request.Method.POST, url, request, DefaultReponse.class, new Response.Listener<DefaultReponse>() {
            @Override
            public void onResponse(DefaultReponse response) {
            }
        }, null);
        Volley.newRequestQueue(context, new HttpClientStack(HttpUtils.getHttpClient(context))).add(bodyRequest);
    }

    private void addListViewHeader() {

        listView.addHeaderView(headerView);

        footView = getLayoutInflater().inflate(R.layout.loading_item, null);
        loadingText = (TextView) footView.findViewById(R.id.tv_loading);
        footView.setBackgroundColor(getResources().getColor(R.color.white));
        loadingText.setText("正在加载评论...");
        loadingProgressBar = (ProgressBar) footView.findViewById(R.id.pb_loading);
        listView.addFooterView(footView);

        // 使用loader请求头部数据
        getSupportLoaderManager().initLoader(HEADER_ID, null, headerCallbacks);

        //使用loader请求明星评论
        getSupportLoaderManager().initLoader(CELEBRITY_ID, null, celebrityCallbacks);
    }

    /**
     * 头部请求
     */
    LoaderManager.LoaderCallbacks<TopicDetails> headerCallbacks = new LoaderManager.LoaderCallbacks<TopicDetails>() {
        @Override
        public Loader<TopicDetails> onCreateLoader(int id, Bundle args) {
            return new ThrowableLoader<TopicDetails>(context, null) {
                @Override
                public TopicDetails loadData() throws Exception {
                    headerPager.next();
                    return headerPager.get();
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<TopicDetails> loader, TopicDetails data) {
            if (data != null) {
                setHeaderContent(data);
                setMoreClickListener(data);
            }
        }

        @Override
        public void onLoaderReset(Loader<TopicDetails> loader) {

        }
    };

    /**
     * 明星的请求
     */
    LoaderManager.LoaderCallbacks<StarDetail> celebrityCallbacks = new LoaderManager.LoaderCallbacks<StarDetail>() {
        @Override
        public Loader<StarDetail> onCreateLoader(int id, Bundle args) {
            return new ThrowableLoader<StarDetail>(context, null) {
                @Override
                public StarDetail loadData() throws Exception {
                    celebrityPager.next();
                    return celebrityPager.get();
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<StarDetail> loader, StarDetail data) {
            if (data != null) {
                if (data.getPosts() != null && data.getPosts().size() > 0) {
                    celebrityPostContent.removeAllViews();
                    ViewUtils.setGone(celebrityPostText, false);
                    for (int i = 0; i < data.getPosts().size(); i++) {
                        final Post post = data.getPosts().get(i);
                        View view = getLayoutInflater().inflate(R.layout.star_post_item, null);
                        ImageView starAvatarImage = (ImageView) view.findViewById(R.id.iv_star_post_item_avatar);
                        final ImageView iconImage = (ImageView) view.findViewById(R.id.iv_star_post_item_voice_icon);

                        RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        iconParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        iconParams.addRule(RelativeLayout.CENTER_VERTICAL);
                        iconParams.rightMargin = Utils.dip2px(context, 8);
                        iconImage.setLayoutParams(iconParams);

                        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.pb_star_post_item);

                        ImageLoader.getInstance().displayImage(post.getUserAvatar(), starAvatarImage, ImageUtils.imageLoader(context, 4));
                        RelativeLayout voiceLayout = (RelativeLayout) view.findViewById(R.id.rl_star_post_voice);
                        int voiceTime;
                        if (post.getAudioTime() < 10) {
                            voiceTime = 48;
                        }
                        int time = 200 * post.getAudioTime() / 45;
                        if (time > 200) {
                            voiceTime = 200;
                        } else {
                            voiceTime = time;
                        }
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dip2px(context, voiceTime), Utils.dip2px(context, 38));
                        params.leftMargin = Utils.dip2px(context, 6);
                        voiceLayout.setLayoutParams(params);
                        voiceLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (voiceImage == iconImage && mediaPlayer.isPlaying()) {
                                    iconImage.setBackgroundResource(R.drawable.icon_playvoice_star);
                                    mediaPlayer.pause();
                                    return;
                                }
                                if (voiceImage != null && voiceImage != iconImage) {
                                    voiceImage.setBackgroundResource(R.drawable.icon_playvoice_star);
                                }
                                try {
                                    mediaPlayer.reset();
                                    voiceImage = iconImage;
                                    mediaPlayer.setDataSource(post.getAudio());
                                    mediaPlayer.prepareAsync();
                                    progressBar.setVisibility(View.VISIBLE);
                                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            mediaPlayer.start();
                                            progressBar.setVisibility(View.GONE);
                                            iconImage.setBackgroundResource(R.drawable.icon_stopvoice_star);
                                        }
                                    });
                                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            iconImage.setBackgroundResource(R.drawable.icon_playvoice_star);
                                        }
                                    });
                                    mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                        @Override
                                        public boolean onError(MediaPlayer mp, int what, int extra) {
                                            ToastUtil.showToast(context, "播放出错了");
                                            return false;
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        TextView voiceTimeText = (TextView) view.findViewById(R.id.tv_star_post_voice_time);
                        voiceTimeText.setText(post.getAudioTime() + "\"");

                        celebrityPostContent.addView(view);
                    }
                } else {
                    ViewUtils.setGone(celebrityPostText, true);
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<StarDetail> loader) {

        }
    };

    /**
     * 设置list头部数据
     *
     * @param data
     */
    private void setHeaderContent(TopicDetails data) {
        if (data.getTopic() != null) {
            final Topic topic = data.getTopic();
            imageLoader.displayImage(topic.getUserAvatar(), avatarImage, ImageUtils.imageLoader(context, 4));
            avatarImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SkipPersonalCenterUtil.startPersonalCore(context, topic.getUserId(), topic.getUserRenderTo());
                }
            });
            nicknameText.setText(topic.getUserNickname());
            titleText.setText(topic.getTitle());
            descText.setText(topic.getDesc());
            if (IConstant.TOPIC_ACTIVITY.equals(type)) {
                fromText.setText(topic.getActivityName());
            } else if (IConstant.TOPIC_CELEBRITY.equals(type)) {
                fromText.setText(topic.getCelebrityUserNickname());
            } else {
                fromText.setText(topic.getGroupName());
            }
            fromText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (IConstant.TOPIC_ACTIVITY.equals(type)) {
                        fromText.setText(topic.getActivityName());
                        startActivity(new Intents(context, TopicHomeActivity.class)
                                .add("activityId", topic.getActivityId())
                                .add("topic_home_image", topic.getActivityImg())
                                .toIntent());
                    } else if (IConstant.TOPIC_CELEBRITY.equals(type)) {
                        startActivity(new Intents(context, StarCenterActivity.class)
                                .add("img", topic.getGroupImg())
                                .add("id", topic.getGroupId())
                                .add("name", topic.getGroupName())
                                .toIntent());
                    } else {
                        startActivity(new Intents(context, OfficialTvCircleActivity.class)
                                .add("img", topic.getGroupImg())
                                .add("id", topic.getGroupId())
                                .add("name", topic.getGroupName())
                                .toIntent());
                    }
                }
            });
            postCountText.setText(topic.getPostsCnt() + "");
            likeCountText.setText(topic.getSupportsCnt() + "");
            createTimeText.setText(topic.getCreateTime());
            if (!TextUtils.isEmpty(topic.getImg())) {
                ViewUtils.setGone(imageView, false);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width / 3 * 2, width / 3 * 2 / 4 * 3);
                params.leftMargin = Utils.dip2px(context, 16);
                params.topMargin = Utils.dip2px(context, 6);
                imageView.setLayoutParams(params);
                ImageLoader.getInstance().displayImage(topic.getImg(), headerImage, ImageUtils.imageLoader(context, 0));
            }
            headerImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(context, ImageDetailsActivity.class);
                    mIntent.putExtra("imageUrl", topic.getImg());
                    startActivity(mIntent);
                }
            });
            linkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (LoginUtil.isLogin(context)) {
//                        execLike(topic);
                        likeAnim(topic);
                    } else {
                        startActivity(new Intents(context, LoginActivity.class).toIntent());
                    }
                }
            });

            if (topic.getIsLiked() == 1) {
                likeText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_liketopic_active_tj), null, null, null);
            } else {
                likeText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_topiclike_tj), null, null, null);
            }

            setShareClickListener(topic);
        }
    }

    /**
     * 分享点击
     *
     * @param topic
     */
    private void setShareClickListener(final Topic topic) {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog mDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
                mDialog.setContentView(R.layout.share_pop);
                Button cancelButton = (Button) mDialog.findViewById(R.id.share_pop_cancel);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mDialog.isShowing())
                            mDialog.dismiss();
                    }
                });

                View sinaView = mDialog.findViewById(R.id.share_sina_layout);
                setSinaClickListener(mDialog, sinaView, topic);

                View tencentView = mDialog.findViewById(R.id.share_tencent_layout);
                setTencentClickListener(mDialog, tencentView, topic);

                View weiXinView = mDialog.findViewById(R.id.share_weixin_layout);
                setWeiXinClickListener(mDialog, weiXinView, topic);

                if (!mDialog.isShowing())
                    mDialog.show();
            }
        };
        shareView.setOnClickListener(clickListener);
    }

    /**
     * 新浪
     *
     * @param dialog
     * @param sinaView
     * @param topic
     */
    private void setSinaClickListener(final Dialog dialog, final View sinaView, final Topic topic) {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing())
                    dialog.dismiss();


                String shareText = "";
                if (IConstant.TOPIC_ACTIVITY.equals(type)) {
                    shareText = String.format(SHARETITLE, topic.getActivityName(), topic.getTitle());
                } else if (IConstant.TOPIC_CELEBRITY.equals(type)) {
                    shareText = String.format(SHARETITLE, topic.getCelebrityUserNickname(), topic.getTitle());
                } else {
                    shareText = String.format(SHARETITLE, topic.getGroupName(), topic.getTitle());
                }

                if (topic.getImg() == null || "".equals(topic.getImg())) {
                    sinaShareText(StringUtil.getShareText(shareText, topic.getDesc()));
                } else {
                    sinaSharePic(headerImage, StringUtil.getShareText(shareText, topic.getDesc()));
                }
            }
        };
        sinaView.setOnClickListener(clickListener);
    }

    /**
     * 腾讯分享
     *
     * @param tencentView
     * @param topic
     */
    private void setTencentClickListener(final Dialog dialog, final View tencentView, final Topic topic) {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing())
                    dialog.dismiss();

                String shareText = "";
                if (IConstant.TOPIC_ACTIVITY.equals(type)) {
                    shareText = String.format(SHARETITLE, topic.getActivityName(), topic.getTitle());
                } else if (IConstant.TOPIC_CELEBRITY.equals(type)) {
                    shareText = String.format(SHARETITLE, topic.getCelebrityUserNickname(), topic.getTitle());
                } else {
                    shareText = String.format(SHARETITLE, topic.getGroupName(), topic.getTitle());
                }

                if (topic.getImg() == null || "".equals(topic.getImg())) {
                    tencentShareText(StringUtil.getShareText(shareText, topic.getDesc()));
                } else {
                    tencentSharePic(headerImage, StringUtil.getShareText(shareText, topic.getDesc()));
                }
            }
        };
        tencentView.setOnClickListener(clickListener);
    }

    /**
     * 微信分享
     *
     * @param weiXinView
     * @param topic
     */
    private void setWeiXinClickListener(final Dialog dialog, final View weiXinView, final Topic topic) {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing())
                    dialog.dismiss();

                String shareText = "";
                if (IConstant.TOPIC_ACTIVITY.equals(type)) {
                    shareText = String.format(SHARETITLE, topic.getActivityName(), topic.getTitle());
                } else if (IConstant.TOPIC_CELEBRITY.equals(type)) {
                    shareText = String.format(SHARETITLE, topic.getCelebrityUserNickname(), topic.getTitle());
                } else {
                    shareText = String.format(SHARETITLE, topic.getGroupName(), topic.getTitle());
                }

                shareWeiXinText(StringUtil.getShareText(shareText, topic.getDesc()));
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                shareWeiXinPic(bmp);
                Toast.makeText(context,"点击微信分享",Toast.LENGTH_SHORT).show();
            }
        };
        weiXinView.setOnClickListener(clickListener);
    }

    /**
     * 执行喜欢操作
     *
     * @param topic
     */
    private void likeAnim(final Topic topic) {
        animLayout.removeAllViews();
        final AccelerateInterpolator interpolator = new AccelerateInterpolator();
        final AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(600L);
        alphaAnimation.setInterpolator(interpolator);
        if (topic.getIsLiked() == 0) {
            topic.setIsLiked(1);
            likeText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_liketopic_active_tj), null, null, null);
            final ImageView imageView = new ImageView(context);
            imageView.setImageResource(R.drawable.like_anim_full);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width / 2, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            animLayout.addView(imageView);

            final Animation scaleAnimation = new ScaleAnimation(1.0F, 1.5F, 1.0F, 1.5F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(200);
            scaleAnimation.setInterpolator(interpolator);

            final Animation smallAnimation = new ScaleAnimation(1.5F, 0.8F, 1.5F, 0.8F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            smallAnimation.setInterpolator(interpolator);
            smallAnimation.setDuration(200);

            final Animation largeAnimation = new ScaleAnimation(0.8F, 1.5F, 0.8F, 1.5F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            largeAnimation.setInterpolator(interpolator);
            largeAnimation.setDuration(400);
            imageView.startAnimation(scaleAnimation);

            Animation.AnimationListener animationListener = new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    animCount++;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (animCount == 4) {
                        animLayout.removeAllViews();
                        animCount = 0;
                        return;
                    }
                    if (largeAnimation.hashCode() == animation.hashCode()) {
                        if (animCount == 3) {
                            AnimationSet animationSet = new AnimationSet(false);
                            animationSet.setDuration(400);
                            animationSet.setInterpolator(interpolator);
                            animationSet.addAnimation(alphaAnimation);
                            animationSet.addAnimation(smallAnimation);
                            animationSet.setFillAfter(true);
                            imageView.startAnimation(animationSet);
                            return;
                        }
                        imageView.startAnimation(smallAnimation);
                    } else if (smallAnimation.hashCode() == animation.hashCode()) {
                        imageView.startAnimation(largeAnimation);
                    } else {
                        imageView.startAnimation(smallAnimation);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            };
            largeAnimation.setAnimationListener(animationListener);
            smallAnimation.setAnimationListener(animationListener);
            scaleAnimation.setAnimationListener(animationListener);
            new AbstractRoboAsyncTask<DefaultReponse>(context) {
                @Override
                protected DefaultReponse run(Object data) throws Exception {
                    return new ResourcePager<DefaultReponse>() {
                        @Override
                        protected Object getId(DefaultReponse resource) {
                            return null;
                        }

                        @Override
                        public PageIterator<DefaultReponse> createIterator(int page, int count) {
                            return service.pageLike(topicId, type);
                        }
                    }.start();
                }

                @Override
                protected void onSuccess(DefaultReponse defaultReponse) throws Exception {
                    super.onSuccess(defaultReponse);
                    if (defaultReponse != null) {
                        if (defaultReponse.getResponseCode() == 0) {
                            topic.setIsLiked(1);
                        } else {
                            ToastUtil.showToast(context, defaultReponse.getResponseMessage());
                        }
                    }
                }
            }.execute();
        } else {
            topic.setIsLiked(0);
            likeText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_topiclike_tj), null, null, null);
            final ImageView leftImage = new ImageView(context);
            leftImage.setImageResource(R.drawable.like_anim_broke_left);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            leftImage.setLayoutParams(params);
            leftImage.setScaleType(ImageView.ScaleType.CENTER);
            animLayout.addView(leftImage);

            TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, height / 2 - 148, height / 2 + 100);
            RotateAnimation rotateAnimation = new RotateAnimation(0.0F, -15.0F, 1, 1.0F, 1, 1.0F);
            AnimationSet animationSet = new AnimationSet(true);
            animationSet.setDuration(600L);
            animationSet.setInterpolator(interpolator);
            animationSet.addAnimation(rotateAnimation);
            animationSet.addAnimation(translateAnimation);
            animationSet.addAnimation(alphaAnimation);
            animationSet.setFillAfter(true);
            leftImage.startAnimation(animationSet);

            final ImageView rightImage = new ImageView(context);
            rightImage.setLayoutParams(params);
            rightImage.setImageResource(R.drawable.like_anim_broke_right);
            rightImage.setScaleType(ImageView.ScaleType.CENTER);
            animLayout.addView(rightImage);

            TranslateAnimation rightTranslateAnimation = new TranslateAnimation(0, 0, height / 2 - 148, height / 2 + 100);
            rightTranslateAnimation.setDuration(600L);
            RotateAnimation rightRotateAnimation = new RotateAnimation(0.0F, 15.0F, 1, 0.0F, 1, 1.0F);
            AnimationSet rightAnimationSet = new AnimationSet(true);
            rightAnimationSet.setDuration(600L);
            rightAnimationSet.setInterpolator(interpolator);
            rightAnimationSet.addAnimation(rightRotateAnimation);
            rightAnimationSet.addAnimation(rightTranslateAnimation);
            rightAnimationSet.addAnimation(alphaAnimation);
            rightAnimationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animLayout.removeAllViews();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            rightAnimationSet.setFillAfter(true);
            rightImage.startAnimation(rightAnimationSet);
            new AbstractRoboAsyncTask<DefaultReponse>(context) {
                @Override
                protected DefaultReponse run(Object data) throws Exception {
                    return new ResourcePager<DefaultReponse>() {
                        @Override
                        protected Object getId(DefaultReponse resource) {
                            return null;
                        }

                        @Override
                        public PageIterator<DefaultReponse> createIterator(int page, int count) {
                            return service.pageUnLike(topicId, type);
                        }
                    }.start();
                }

                @Override
                protected void onSuccess(DefaultReponse defaultReponse) throws Exception {
                    super.onSuccess(defaultReponse);
                    if (defaultReponse != null) {
                        if (defaultReponse.getResponseCode() == 0) {
                            topic.setIsLiked(0);
                            likeText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_topiclike_tj), null, null, null);
                        } else {
                            ToastUtil.showToast(context, defaultReponse.getResponseMessage());
                        }
                    }
                }
            }.execute();
        }
    }

    /**
     * list header pager
     *
     * @return
     */
    private ResourcePager<TopicDetails> createHeaderPager() {
        return new ResourcePager<TopicDetails>() {
            @Override
            protected Object getId(TopicDetails resource) {
                return null;
            }

            @Override
            public PageIterator<TopicDetails> createIterator(int page, int count) {
                return service.pageTopicDetailsHeader(topicId, type);
            }
        };
    }

    /**
     * 评论内容 pager
     *
     * @return
     */
    private ResourcePager<TopicDetailsPosts> createPager() {
        return new ResourcePager<TopicDetailsPosts>() {
            @Override
            protected Object getId(TopicDetailsPosts resource) {
                return null;
            }

            @Override
            public PageIterator<TopicDetailsPosts> createIterator(int page, int count) {
                return service.pageTopicDetails(topicId, requestPage, requestCount, type);
            }
        };
    }

    private ResourcePager<StarDetail> createCelebrityPostsPager() {

        return new ResourcePager<StarDetail>() {
            @Override
            public PageIterator<StarDetail> createIterator(int page, int count) {
                return service.pageCelebrityPost(topicId, type);
            }
        };
    }

    @Override
    public Loader<TopicDetailsPosts> onCreateLoader(int id, Bundle args) {
        return new ThrowableLoader<TopicDetailsPosts>(context, null) {
            @Override
            public TopicDetailsPosts loadData() throws Exception {
                pager.next();
                return pager.get();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<TopicDetailsPosts> loader, TopicDetailsPosts data) {
        expandableListView.onRefreshComplete();
        setSupportProgressBarIndeterminateVisibility(false);
        Exception exception = getException(loader);
        if (exception != null) {
            showError(exception);
            if (isEmpty) {
                listView.removeFooterView(footView);
                listView.setDivider(null);
                listView.setAdapter(new TopicDetailsEmptyAdapter(context));
            }
        }

        if (data != null) {
            if (data.getPosts() != null && data.getPosts().size() > 0) {
                if (isEmpty && data.getPosts().size() < 20) {
                    listView.removeFooterView(footView);
                }
                isEmpty = false;
                if (listView.getExpandableListAdapter() instanceof TopicDetailsEmptyAdapter) {
                    listView.setAdapter(adapter);
                }
                if (isRefresh) {
                    adapter.clear();
                }
                adapter.addPosts(data.getPosts());
            } else {
                hasMore = true;
                loadingProgressBar.setVisibility(View.GONE);
                loadingText.setText("已加载全部内容");
                if (isEmpty) {
                    listView.removeFooterView(footView);
                    listView.setDivider(null);
                    listView.setAdapter(new TopicDetailsEmptyAdapter(context));
                }
            }
        }
        hide(progressBar).fadeIn(listView, true).show(expandableListView).show(listView);
    }

    @Override
    public void onLoaderReset(Loader<TopicDetailsPosts> loader) {

    }

    protected Exception getException(final Loader<TopicDetailsPosts> loader) {
        if (loader instanceof ThrowableLoader) {
            return ((ThrowableLoader<TopicDetailsPosts>) loader).clearException();
        } else {
            return null;
        }
    }

    protected void showError(final Exception e) {
        expandableListView.onRefreshComplete();
        ToastUtil.showToast(context, "评论加载失败");
    }

    /**
     * Called when the user has scrolled to the end of the list
     */
    @Override
    public void onLastItemVisible() {
        if (hasMore) {
            return;
        }
        if (getSupportLoaderManager().hasRunningLoaders()) {
            return;
        }
        isRefresh = false;
        requestPage = requestPage + 1;
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    /**
     * onRefresh will be called for both a Pull from start, and Pull from
     * end
     *
     * @param refreshView
     */
    @Override
    public void onRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
        requestPage = 1;
        isRefresh = true;
        getSupportLoaderManager().restartLoader(POSTS_ID, null, this);
        getSupportLoaderManager().restartLoader(HEADER_ID, null, headerCallbacks);
        getSupportLoaderManager().restartLoader(CELEBRITY_ID, null, celebrityCallbacks);
    }

    private void showList() {
        setListShown(true, true);
    }

    public TopicDetailsActivity setListShown(final boolean shown, final boolean animate) {

        if (shown == listShown) {
            if (shown) {
                if (isEmpty) {
                    hide(listView);//.show(emptyView);
                } else {
                    /**hide(emptyView).*/show(listView);
                }
                return this;
            }
        }

        listShown = shown;
        if (shown) {
            if (!isEmpty) {
                hide(progressBar)/**.hide(emptyView)*/.fadeIn(listView, animate).show(expandableListView).show(listView);
            } else {
                hide(listView).hide(progressBar)/**.fadeIn(emptyView, animate).show(emptyView)*/;
            }
        } else {
            hide(listView)/**.hide(emptyView)*/.fadeIn(progressBar, animate).show(progressBar);
        }
        return this;
    }

    /**
     * hide view
     *
     * @param view
     * @return
     */
    protected TopicDetailsActivity hide(final View view) {
        ViewUtils.setGone(view, true);
        return this;
    }

    /**
     * show view
     *
     * @param view
     * @return
     */
    protected TopicDetailsActivity show(final View view) {
        ViewUtils.setGone(view, false);
        return this;
    }

    private TopicDetailsActivity fadeIn(final View view, final boolean animate) {
        if (view != null) {
            if (animate) {
                view.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
            } else {
                view.clearAnimation();
            }
        }
        return this;
    }

    private class ReplyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            requestPage = 1;
            getSupportLoaderManager().restartLoader(POSTS_ID, null, TopicDetailsActivity.this);
        }
    }

}
