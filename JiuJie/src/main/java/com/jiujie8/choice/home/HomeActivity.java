package com.jiujie8.choice.home;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.kevinsawicki.wishlist.Keyboard;
import com.jiujie8.choice.ChoiceApplication;
import com.jiujie8.choice.DistributeAPI;
import com.jiujie8.choice.R;
import com.jiujie8.choice.Response;
import com.jiujie8.choice.SharePage;
import com.jiujie8.choice.choice.PublishChoiceActivity;
import com.jiujie8.choice.core.AbstractRoboAsyncTask;
import com.jiujie8.choice.core.Intents;
import com.jiujie8.choice.core.ProgressDialogTask;
import com.jiujie8.choice.core.ThirdPartyShareActivity;
import com.jiujie8.choice.core.ThrowableLoader;
import com.jiujie8.choice.core.ToastUtils;
import com.jiujie8.choice.home.entity.ChoiceItem;
import com.jiujie8.choice.home.entity.ChoiceList;
import com.jiujie8.choice.home.entity.ChoiceMode;
import com.jiujie8.choice.home.entity.Favorite;
import com.jiujie8.choice.http.GsonRequest;
import com.jiujie8.choice.http.HttpUtils;
import com.jiujie8.choice.persioncenter.LoginActivity;
import com.jiujie8.choice.persioncenter.PercenterActivityGridview;
import com.jiujie8.choice.publicentity.User;
import com.jiujie8.choice.setting.SettingActivity;
import com.jiujie8.choice.util.AppManager;
import com.jiujie8.choice.util.CheckVersion;
import com.jiujie8.choice.util.ChoiceTypeUtil;
import com.jiujie8.choice.util.IConstant;
import com.jiujie8.choice.util.LoginUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14/11/21.
 * <p/>
 * 主页面
 */
@ContentView(R.layout.home)
public class HomeActivity extends ThirdPartyShareActivity implements ShareDialog.OnShareListener, MoreDialog.OnMoreCallBack {

    private final static int REFESH = 0;

    private final static int PERSION = 1;

    private final static int PUBLISH_CHOICE = 0;

    private final static int LOADER_ID = 0;

    private long firstTime = 0;

    @InjectView(R.id.home_page)
    private ViewPager mViewPager;

    private HomePagerAdapter mAdapter;

    @InjectView(R.id.home_list_publish_choice)
    public ImageButton mPublishChoiceButton;

    @InjectView(R.id.home_collet)
    private ImageButton mColletButton;

    @InjectView(R.id.home_comment)
    private ImageButton mCommentButton;

    @InjectView(R.id.home_vote)
    private ImageButton mVoteButton;

    @InjectView(R.id.home_share)
    private ImageButton mShareButton;

    @InjectView(R.id.home_more)
    private ImageButton mMoreButton;

    @InjectView(R.id.home_tool_bar_layout)
    public View mToolbarView;

    private int currentItem = 0;

    private MenuItem mRefreshMenuItem;

    private final static List<ChoiceMode> ITEMS = new ArrayList<ChoiceMode>();

    private Dialog mCommentDialog;

    private EditText mCommentText;

    private TextView mPublishView;

    private String mEditContent;

    private boolean isSuccess = false;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        //信鸽配置
        //开启debug
        XGPushConfig.enableDebug(this, IConstant.DEBUG);
        //注册信鸽
        ChoiceApplication application = (ChoiceApplication) getApplication();
        XGPushManager.registerPush(getApplicationContext());

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(false);
        //检查版本更新
        if (((ChoiceApplication) getApplication()).isUpdate) {
            CheckVersion.check_update(activity);
        }

        mAdapter = new HomePagerAdapter(this);
        mViewPager.setAdapter(mAdapter);
        getSupportLoaderManager().initLoader(LOADER_ID, null, mLoaderCallbacks);

        mPublishChoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LoginUtil.isLogin()) {
                    startActivityForResult(new Intents(activity, PublishChoiceActivity.class).toIntent(), PUBLISH_CHOICE);
                } else {
                    startActivity(new Intents(activity, LoginActivity.class).toIntent());
                }
            }
        });

        mColletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colletClickListenenr();
            }
        });

        mCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishComment();
            }
        });

        mVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ChoiceMode mMode = ITEMS.get(mViewPager.getCurrentItem());
                startActivity(new Intents(activity, VoteResultActivity.class).add(IConstant.MODE_ITEM, mMode).toIntent());
            }
        });

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShareDialog();
            }
        });

        mMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreDialog();
            }
        });
    }

    private void inforCollect() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Object object = applicationInfo.metaData.get("JIUJIE_CHANNEL");
            if (object != null) {
                String marketHouseId = object.toString();
                if (marketHouseId.length() == 1) {
                    marketHouseId = "0" + marketHouseId;
                }
                DistributeAPI.activateDevice(this, marketHouseId, getPackageName(), versionName);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 收藏的点击事件
     */
    private synchronized void colletClickListenenr() {
        if (LoginUtil.isLogin()) {
            final HomeFragment mFragment = mAdapter.getFragments().get(mViewPager.getCurrentItem());
            final ChoiceMode mMode = ITEMS.get(mViewPager.getCurrentItem());
            final ChoiceItem mItem = mMode.getChoice();
            final ChoiceTypeUtil.ItemInfo mItemInfo = mFragment.getItemInfo();
            if (mItemInfo != null) {
                final Favorite mFavorite = mMode.getFavorite();
                if (mFavorite != null) {
                    mColletButton.setImageResource(R.drawable.btn_collecting_choice);
                    mMode.setFavorite(null);
                    if (mItem != null) {
                        final int favoriteCnt = mItem.getFavoriteCnt() - 1;
                        mItem.setFavoriteCnt(favoriteCnt);
                        mItemInfo.collectView.setText(String.valueOf(favoriteCnt));
                    }
                    new AbstractRoboAsyncTask<Response>(activity) {

                        @Override
                        protected Response run(Object data) throws Exception {
                            final Map<String, String> map = new HashMap<String, String>();
                            if (mItem != null) {
                                map.put(IConstant.CHOICE_ID, String.valueOf(mItem.getId()));
                            }
                            return (Response) HttpUtils.doRequest(HomeServices.createCancelFavoriteRequest(map)).result;
                        }

                        @Override
                        protected void onSuccessCallback(Response response) {

                        }
                    }.execute();
                } else {
                    mColletButton.setImageResource(R.drawable.btn_collect_choice);
                    mMode.setFavorite(new Favorite());
                    if (mItem != null) {
                        final int favoriteCnt = mItem.getFavoriteCnt() + 1;
                        mItem.setFavoriteCnt(favoriteCnt);
                        mItemInfo.collectView.setText(String.valueOf(favoriteCnt));
                    }
                    new AbstractRoboAsyncTask<Response>(activity) {

                        @Override
                        protected Response run(Object data) throws Exception {
                            final Map<String, String> map = new HashMap<String, String>();
                            if (mItem != null) {
                                map.put(IConstant.CHOICE_ID, String.valueOf(mItem.getId()));
                            }
                            return (Response) HttpUtils.doRequest(HomeServices.createFavoriteRequest(map)).result;
                        }

                        @Override
                        protected void onSuccessCallback(Response response) {

                        }
                    }.execute();
                }
            }
        } else {
            startActivity(new Intents(this, LoginActivity.class).toIntent());
        }
    }

    /**
     * 更多的dialog
     */
    private void showMoreDialog() {
        MoreDialog mDialog = (MoreDialog) getSupportFragmentManager().findFragmentByTag("more");
        if (mDialog == null) {
            mDialog = new MoreDialog();
        }
        mDialog.setOnDeleteCallback(this);
        final Bundle args = new Bundle();
        final ChoiceMode mMode = ITEMS.get(mViewPager.getCurrentItem());
        final ChoiceItem mItem = mMode.getChoice();
        args.putLong(IConstant.CHOICE_ID, mItem.getId());
        final User mUser = mItem.getUser();
        if (mUser != null) {
            final String mUserId = LoginUtil.getUserId();
            if (!TextUtils.isEmpty(mUserId) && mUserId.equals(mUser.getId())) {
                args.putBoolean(IConstant.IS_SELF, true);
            } else {
                args.putBoolean(IConstant.IS_SELF, false);
            }
        }
        mDialog.setArguments(args);
        mDialog.show(getSupportFragmentManager(), "more");
    }

    /**
     * 设置已读的事件
     */
    @Override
    public void onNotReadListener() {
        ITEMS.remove(mViewPager.getCurrentItem());
        mAdapter.setItems(ITEMS, false);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 删除成功之后重新请求列表数据
     */
    @Override
    public void onDeleteSuccess() {
        requestPage = 1;
        mAdapter.clear();
        ITEMS.clear();
        currentItem = 0;
        getSupportLoaderManager().restartLoader(LOADER_ID, null, mLoaderCallbacks);
    }

    /**
     * 显示分享的dialog
     */
    private void showShareDialog() {
        ShareDialog mDialog = (ShareDialog) getSupportFragmentManager().findFragmentByTag("share");
        if (mDialog == null) {
            mDialog = new ShareDialog();
        }
        mDialog.setShareListener(this);
        mDialog.show(getSupportFragmentManager(), "share");
    }

    /**
     * 分享到微信朋友圈
     */
    @Override
    public void onShareWeixinTimelineListener() {
        final ChoiceMode mMode = ITEMS.get(mViewPager.getCurrentItem());
        final ChoiceItem mItem = mMode.getChoice();
        final SharePage mPage = mItem.getSharePage();

        ImageLoader.getInstance().loadImage(mPage.getIcon(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                shareWeiXinWebPage(mPage.getUrl(), mPage.getTitle(), mPage.getDesc(), loadedImage);
            }
        });
    }

    /**
     * 分享到微信好友
     */
    @Override
    public void onShareWeixinSessionListener() {
        final ChoiceMode mMode = ITEMS.get(mViewPager.getCurrentItem());
        final ChoiceItem mItem = mMode.getChoice();
        final SharePage mPage = mItem.getSharePage();

        ImageLoader.getInstance().loadImage(mPage.getIcon(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                shareWeiXinFriendWebPage(mPage.getUrl(), mPage.getTitle(), mPage.getDesc(), loadedImage);
            }
        });
    }

    /**
     * 分享到新浪微博
     */
    @Override
    public void onShareSinaListener() {
        HomeFragment mFragment = mAdapter.getFragments().get(mViewPager.getCurrentItem());
        if (mFragment.getItemInfo() != null) {
            final View v = mFragment.getItemInfo().floorView;
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Bitmap mBitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
                    final Canvas mCanvas = new Canvas(mBitmap);
                    v.layout(0, 0, v.getWidth(), v.getHeight());
                    v.draw(mCanvas);
                    final ChoiceMode mMode = ITEMS.get(mViewPager.getCurrentItem());
                    sinaSharePic(mBitmap, mMode.getChoice().getTitle());
                }
            }, 500);
        }
    }

    /**
     * 发布评论
     */
    public void publishComment() {
        final HomeFragment mFragment = mAdapter.getFragments().get(mViewPager.getCurrentItem());
        if (!LoginUtil.isLogin()) {
            startActivity(new Intents(this, LoginActivity.class).toIntent());
            return;
        }
        if (mCommentDialog == null) {
            mCommentDialog = new Dialog(this, R.style.dialog_theme);
            Window mWindow = mCommentDialog.getWindow();
            mWindow.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            mWindow.setContentView(R.layout.comment_dialog);
            WindowManager.LayoutParams mParams = mWindow.getAttributes();
            mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            mParams.gravity = Gravity.BOTTOM;
            mWindow.setAttributes(mParams);
            mCommentDialog.setCanceledOnTouchOutside(true);
            mCommentText = (EditText) mWindow.findViewById(R.id.comment_edit);
            mPublishView = (TextView) mWindow.findViewById(R.id.comment_publish);
            mCommentDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if (!isSuccess) {
                        mEditContent = mCommentText.getText().toString();
                    }
                }
            });
            mCommentDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        mCommentDialog.dismiss();
                    }
                    return false;
                }
            });
        }
        if (mCommentDialog != null && !mCommentDialog.isShowing()) {
            mCommentText.setText(mEditContent);
            mCommentDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE |
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            mCommentDialog.show();
        }
        if (mPublishView != null) {
            final ChoiceMode mMode = ITEMS.get(mViewPager.getCurrentItem());
            mPublishView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String mContent = mCommentText.getText().toString();
                    if (TextUtils.isEmpty(mContent)) {
                        ToastUtils.show(activity, R.string.content_empty);
                        return;
                    }
                    new ProgressDialogTask<Response>(activity) {

                        @Override
                        protected Response run(Object data) throws Exception {
                            final Map<String, String> mBodys = new HashMap<String, String>();
                            if (mCommentText != null) {
                                if (mMode != null && mMode.getChoice() != null) {
                                    mBodys.put("choiceId", String.valueOf(mMode.getChoice().getId()));
                                }
                                mBodys.put("message", mContent);
                            }
                            return (Response) HttpUtils.doRequest(HomeServices.createCommentPostRequest(mBodys)).result;
                        }

                        @Override
                        protected void onSuccessCallback(Response response) {
                            if (mCommentDialog != null && mCommentDialog.isShowing()) {
                                mCommentDialog.dismiss();
                            }
                            isSuccess = true;
                            mEditContent = "";
                            mFragment.refreshList();
                        }
                    }.execute();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PUBLISH_CHOICE && resultCode == RESULT_OK) {
            requestPage = 1;
            ITEMS.clear();
            currentItem = 0;
            isSuccess = true;
            mAdapter.notifyDataSetChanged();
            getSupportLoaderManager().restartLoader(LOADER_ID, null, mLoaderCallbacks);
        }
    }

    /**
     * 请求列表的Loader Callbacks
     */
    LoaderManager.LoaderCallbacks<ChoiceList> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<ChoiceList>() {
        @Override
        public Loader<ChoiceList> onCreateLoader(int id, Bundle args) {
            return new ThrowableLoader<ChoiceList>(activity) {
                @Override
                public ChoiceList loadData() throws Exception {
                    Map<String, String> mParams = new HashMap<String, String>();
                    mParams.put("page", requestPage + "");
                    mParams.put("count", requestCount + "");
                    GsonRequest<ChoiceList> mRequest = HomeServices.createHomePagerRequest(mParams);
                    return (ChoiceList) HttpUtils.doRequest(mRequest).result;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<ChoiceList> loader, final ChoiceList data) {
            if (data != null) {
                if (IConstant.STATE_OK.equals(data.getCode())) {
                    if (data.getRs() != null && !data.getRs().isEmpty()) {
                        final boolean hasMore = data.getRs().size() < requestCount;
                        ITEMS.addAll(data.getRs());
                        mAdapter.setItems(ITEMS, hasMore);
                        if (currentItem == 0) {
                            final ViewPager.OnPageChangeListener mPageChangeListener = onPageChangeListenerInit(ITEMS, hasMore);
                            mPageChangeListener.onPageSelected(0);
                            mViewPager.setOnPageChangeListener(mPageChangeListener);
                        }
                        if (isSuccess) {
                            mViewPager.setCurrentItem(0);
                            isSuccess = false;
                        }
                        currentItem += data.getRs().size();
                    }
                } else {
                    ToastUtils.show(activity, data.getMessage());
                }
            }
            if (mRefreshMenuItem != null) {
                View view = mRefreshMenuItem.getActionView();
                if (view != null) {
                    view.clearAnimation();
                    mRefreshMenuItem.setActionView(null);
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<ChoiceList> loader) {

        }
    };

    /**
     * ViewPager的监听
     *
     * @param mItems
     * @param hasMore
     * @return
     */
    private ViewPager.OnPageChangeListener onPageChangeListenerInit(final List<ChoiceMode> mItems, final boolean hasMore) {
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position + 1 == mItems.size()) {
                    if (!getSupportLoaderManager().hasRunningLoaders() && !hasMore) {
                        ++requestPage;
                        getSupportLoaderManager().restartLoader(LOADER_ID, null, mLoaderCallbacks);
                    }
                }
                mEditContent = "";
                if (mAdapter.getItem(position) instanceof LoadingFragment) {
                    mToolbarView.setVisibility(View.GONE);
                    mPublishChoiceButton.setVisibility(View.GONE);
                    return;
                }
                mToolbarView.setVisibility(View.VISIBLE);
                mPublishChoiceButton.setVisibility(View.VISIBLE);
                final ChoiceMode mMode = ITEMS.get(mViewPager.getCurrentItem());
                if (mMode.getFavorite() != null) {
                    mColletButton.setImageResource(R.drawable.btn_collect_choice);
                } else {
                    mColletButton.setImageResource(R.drawable.btn_collecting_choice);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, REFESH, Menu.NONE, "刷新")
                .setIcon(R.drawable.icon_refresh_choice)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(Menu.NONE, PERSION, Menu.NONE, "个人中心").setIcon(R.drawable.icon_profiles_choice).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case REFESH:
                mRefreshMenuItem = item;
                requestPage = 1;
                ITEMS.clear();
                isSuccess = true;
                mAdapter.notifyDataSetChanged();
                final LayoutInflater mInflater = getLayoutInflater();
                ImageView mImageView = (ImageView) mInflater.inflate(R.layout.actionbar_refresh, null);
                Animation mRotation = AnimationUtils.loadAnimation(this, R.anim.refesh_anim);
                mImageView.startAnimation(mRotation);
                item.setActionView(mImageView);
                getSupportLoaderManager().restartLoader(LOADER_ID, null, mLoaderCallbacks);
                break;
            case PERSION:
                if (LoginUtil.isLogin()) {
                    startActivity(new Intent(this, PercenterActivityGridview.class));
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ITEMS.clear();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mCommentDialog != null && mCommentDialog.isShowing()) {
                Keyboard.hideSoftInput(mCommentButton);
                mCommentDialog.dismiss();
                return true;
            }
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                AppManager.getAppManager().AppExit(this);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
