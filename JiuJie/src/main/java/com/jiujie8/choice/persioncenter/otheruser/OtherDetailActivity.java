package com.jiujie8.choice.persioncenter.otheruser;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.jiujie8.choice.R;
import com.jiujie8.choice.Response;
import com.jiujie8.choice.SharePage;
import com.jiujie8.choice.core.AbstractRoboAsyncTask;
import com.jiujie8.choice.core.Intents;
import com.jiujie8.choice.core.ProgressDialogTask;
import com.jiujie8.choice.core.ThirdPartyShareActivity;
import com.jiujie8.choice.core.ToastUtils;
import com.jiujie8.choice.home.HomeServices;
import com.jiujie8.choice.home.MoreDialog;
import com.jiujie8.choice.home.ShareDialog;
import com.jiujie8.choice.home.VoteResultActivity;
import com.jiujie8.choice.home.entity.ChoiceItem;
import com.jiujie8.choice.home.entity.ChoiceMode;
import com.jiujie8.choice.home.entity.Favorite;
import com.jiujie8.choice.http.HttpUtils;
import com.jiujie8.choice.myjiujie.CustomViewPager;
import com.jiujie8.choice.myjiujie.MyJiujieFragment;
import com.jiujie8.choice.myjiujie.MyJiujiePagerAdapter;
import com.jiujie8.choice.persioncenter.LoginActivity;
import com.jiujie8.choice.publicentity.User;
import com.jiujie8.choice.util.ChoiceTypeUtil;
import com.jiujie8.choice.util.HttpHelp;
import com.jiujie8.choice.util.IConstant;
import com.jiujie8.choice.util.LoginUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * 个人纠结页面
 */
@ContentView(R.layout.activity_myjiujie_layout)
public class OtherDetailActivity extends ThirdPartyShareActivity implements ShareDialog.OnShareListener, MoreDialog.OnMoreCallBack {

    private final static int REFESH = 0;

    private final static int PERSION = 1;


    private final static int LOADER_ID = 0;
    private static final String IGNORE_CHOICE_URL = "choice/ignore";

    private long firstTime = 0;

    @InjectView(R.id.home_page)
    private CustomViewPager mViewPager;

    private MyJiujiePagerAdapter mAdapter;

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
    private ChoiceMode choiceMode;
    private ArrayList<ChoiceMode> choiceModeList;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

         choiceMode = (ChoiceMode)getIntent().getSerializableExtra(IConstant.ChOICE_MODE);
         choiceModeList = (ArrayList<ChoiceMode>) getIntent().getSerializableExtra(IConstant.LIST);

        getSupportActionBar().setLogo(R.drawable.btn_back_choice);
        getSupportActionBar().setTitle(choiceMode.getChoice().getUser().getNickname()+"的纠结");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdapter = new MyJiujiePagerAdapter(this);
        mViewPager.setAdapter(mAdapter);
        //新加，设置纠结
        mAdapter.setMode(choiceMode);
        //固定第一页显示
        mViewPager.setCurrentItem(0);

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
                startActivity(new Intents(activity, VoteResultActivity.class).add(IConstant.MODE_ITEM, choiceMode).toIntent());
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

    private synchronized void colletClickListenenr() {
        if (LoginUtil.isLogin()) {
            final MyJiujieFragment mFragment = mAdapter.getFragments();
            final ChoiceItem mItem = choiceMode.getChoice();
            final ChoiceTypeUtil.ItemInfo mItemInfo = mFragment.getItemInfo();
            if (mItemInfo != null) {
                final Favorite mFavorite = choiceMode.getFavorite();
                if (mFavorite != null) {
                    mColletButton.setImageResource(R.drawable.btn_collecting_choice);
                    choiceMode.setFavorite(null);
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
                    choiceMode.setFavorite(new Favorite());
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_OK);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showMoreDialog() {
        MoreDialog mDialog = (MoreDialog) getSupportFragmentManager().findFragmentByTag("more");
        if (mDialog == null) {
            mDialog = new MoreDialog();
        }
        mDialog.setOnDeleteCallback(this);
        final Bundle args = new Bundle();
        final ChoiceItem mItem =  choiceMode.getChoice();
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

    @Override
    public void onNotReadListener() {
//        ITEMS.remove(mViewPager.getCurrentItem());
//        mAdapter.notifyDataSetChanged();
        final Map<String, String> map = new HashMap<String, String>();
        map.put(IConstant.CHOICE_ID, String.valueOf(choiceMode.getChoice().getId()));

        HttpHelp.getInstance().getHttp(this,Response.class,IGNORE_CHOICE_URL,map,new HttpHelp.OnCompleteListener<Response>() {
            @Override
            public void onComplete(Response response) {
                ToastUtils.show(OtherDetailActivity.this,"已忽略，下次不会看到");
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    public void onDeleteSuccess() {
        int index=2;
        for(int i=1;i<choiceModeList.size();i++){
            if(choiceModeList.get(i).getChoice().getId()==choiceMode.getChoice().getId()){
                index=i;
                break;
            }
        }
        if(index+1<choiceModeList.size()) {
            // 导入下一个
            choiceMode = (ChoiceMode) choiceModeList.get(index + 1);
                     finish();
            Bundle mBundle = new Bundle();
            mBundle.putSerializable(IConstant.ChOICE_MODE,choiceMode);
//                        mBundle.putParcelableArrayList(IConstant.LIST, (ArrayList)jiuChoice);
            mBundle.putSerializable(IConstant.LIST, (ArrayList)choiceModeList);
            activity.startActivity(new Intent(activity, OtherDetailActivity.class).putExtras(mBundle));
        }else {
            ToastUtils.show(this,"已经到最后一项了,木有了");
            setResult(RESULT_OK);
            finish();
        }
    }

    private void showShareDialog() {
        ShareDialog mDialog = (ShareDialog) getSupportFragmentManager().findFragmentByTag("share");
        if (mDialog == null) {
            mDialog = new ShareDialog();
        }
        mDialog.setShareListener(this);
        mDialog.show(getSupportFragmentManager(), "share");
    }

    @Override
    public void onShareWeixinTimelineListener() {
        final ChoiceItem mItem = choiceMode.getChoice();
        final SharePage mPage = mItem.getSharePage();

        ImageLoader.getInstance().loadImage(mPage.getIcon(), new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                shareWeiXinWebPage(mPage.getUrl(), mPage.getTitle(), mPage.getDesc(), loadedImage);
            }
        });
    }

    @Override
    public void onShareWeixinSessionListener() {
        final ChoiceItem mItem = choiceMode.getChoice();
        final SharePage mPage = mItem.getSharePage();

        ImageLoader.getInstance().loadImage(mPage.getIcon(), new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                shareWeiXinFriendWebPage(mPage.getUrl(), mPage.getTitle(), mPage.getDesc(), loadedImage);
            }
        });
    }

    @Override
    public void onShareSinaListener() {
        MyJiujieFragment mFragment = mAdapter.getFragments();
        if (mFragment.getItemInfo() != null) {
            final View v = mFragment.getItemInfo().floorView;
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Bitmap mBitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
                    final Canvas mCanvas = new Canvas(mBitmap);
                    v.layout(0, 0, v.getWidth(), v.getHeight());
                    v.draw(mCanvas);
                    final ChoiceMode mMode =choiceMode;
                    sinaSharePic(mBitmap, mMode.getChoice().getTitle());
                }
            }, 500);
        }
    }

    private void publishComment() {
        final MyJiujieFragment mFragment = mAdapter.getFragments();
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
                                mBodys.put("choiceId", String.valueOf(choiceMode.getChoice().getId()));
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
