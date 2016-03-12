package com.mzs.guaji.offical;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.GsonUtils;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.mzs.guaji.R;
import com.mzs.guaji.core.AbstractRoboAsyncTask;
import com.mzs.guaji.core.DialogFragmentActivity;
import com.mzs.guaji.core.Intents;
import com.mzs.guaji.core.PageIterator;
import com.mzs.guaji.core.ProgressDialogTask;
import com.mzs.guaji.core.ResourcePager;
import com.mzs.guaji.entity.ApplyType;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.entity.EntryForm;
import com.mzs.guaji.entity.Game;
import com.mzs.guaji.entity.GameList;
import com.mzs.guaji.offical.entity.ModuleSpecs;
import com.mzs.guaji.offical.entity.OfficialModules;
import com.mzs.guaji.ui.AQBWApplyActivity;
import com.mzs.guaji.ui.FNMSApplyActivity;
import com.mzs.guaji.ui.GSTXApplyActivity;
import com.mzs.guaji.ui.LoginActivity;
import com.mzs.guaji.ui.LoveExponentActivity;
import com.mzs.guaji.ui.NewsListActivity;
import com.mzs.guaji.ui.ScanRankingActivity;
import com.mzs.guaji.ui.ShakeActivity;
import com.mzs.guaji.ui.VideoListingActivity;
import com.mzs.guaji.ui.WebViewActivity;
import com.mzs.guaji.util.BroadcastActionUtil;
import com.mzs.guaji.util.ImageUtils;
import com.mzs.guaji.util.LoginUtil;
import com.mzs.guaji.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * 官方 activity
 * @author lenovo
 *
 */
@ContentView(R.layout.official_tv_circlelayout)
public class OfficialTvCircleActivity extends DialogFragmentActivity implements AdapterView.OnItemClickListener {

    @Inject
	private Context context;

    @Inject
    private OfficialService service;

    @Inject
    private OfficialTVAdapter adapter;

	private long tvCircleId;

    private String name;

    @InjectView(R.id.official_back)
    private View backView;

    @InjectView(R.id.official_add)
    private TextView addView;

    @InjectView(R.id.pb_loading)
    private View progressView;

    @InjectView(R.id.official_content_layout)
    private View contentView;

    @InjectView(R.id.official_pic_quan_image)
    private ImageView quanImage;

    @InjectView(R.id.official_progressbar)
    private View quanProgressView;

    @InjectView(R.id.official_name_text)
    private TextView nameText;

    @InjectView(R.id.official_grid)
    private GridView gridView;
	
	@Override
	protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String imgUrl = getStringExtra("img");
        tvCircleId = getLongExtra("id");
        name = getStringExtra("name");

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (!TextUtils.isEmpty(imgUrl)) {
            ImageLoader.getInstance().displayImage(imgUrl, quanImage, ImageUtils.imageLoader(context, 0), new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    hide(quanProgressView);
                }
            });
        }

        nameText.setText(name);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);

        new AbstractRoboAsyncTask<OfficialModules>(context){
            @Override
            protected OfficialModules run(Object data) throws Exception {
                return new ResourcePager<OfficialModules>(){
                    @Override
                    public PageIterator<OfficialModules> createIterator(int page, int count) {
                        return service.pageModules(tvCircleId);
                    }
                }.start();
            }

            @Override
            protected void onSuccess(OfficialModules officialModules) throws Exception {
                super.onSuccess(officialModules);
                if (officialModules != null) {
                    hide(progressView);
                    show(contentView);
                    addTVCircleClickListener(officialModules);
                    adapter.setItems(officialModules.getModuleSpecs());
                    if (LoginUtil.isLogin(context)) {
                        if (officialModules.getIsJoined() == 0) {
                            addView.setText("加入");
                            addView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.btn_addq_tj), null);
                        } else {
                            addView.setText("退出");
                            addView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.btn_qexit_tj), null);
                        }
                    } else {
                        addView.setText("加入");
                        addView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.btn_addq_tj), null);
                    }
                }
            }
        }.execute();
    }

    private void addTVCircleClickListener(final OfficialModules modules) {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginUtil.isLogin(context)) {
                    if (modules.getIsJoined() == 1) {
                        new ProgressDialogTask<DefaultReponse>(context){
                            @Override
                            protected DefaultReponse run(Object data) throws Exception {
                                return new ResourcePager<DefaultReponse>(){
                                    @Override
                                    public PageIterator<DefaultReponse> createIterator(int page, int count) {
                                        return service.pageQuit(tvCircleId);
                                    }
                                }.start();
                            }

                            @Override
                            protected void onSuccess(DefaultReponse defaultReponse) throws Exception {
                                super.onSuccess(defaultReponse);
                                if (defaultReponse != null) {
                                    if (defaultReponse.getResponseCode() == 0) {
                                        modules.setIsJoined(0);
                                        addView.setText("加入");
                                        addView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.btn_addq_tj), null);
                                    } else {
                                        ToastUtil.showToast(context, defaultReponse.getResponseMessage());
                                    }
                                }
                            }
                        }.start("正在退出圈子");
                    } else {
                        new ProgressDialogTask<DefaultReponse>(context){
                            @Override
                            protected DefaultReponse run(Object data) throws Exception {
                                return new ResourcePager<DefaultReponse>(){

                                    @Override
                                    public PageIterator<DefaultReponse> createIterator(int page, int count) {
                                        return service.pageJoin(tvCircleId);
                                    }
                                }.start();
                            }

                            @Override
                            protected void onSuccess(DefaultReponse defaultReponse) throws Exception {
                                super.onSuccess(defaultReponse);
                                if (defaultReponse != null) {
                                    if (defaultReponse.getResponseCode() == 0) {
                                        if (defaultReponse.getGivenScore() != null) {
                                            showScoreDialog(defaultReponse.getGivenScore().getMessage());
                                        }
                                        modules.setIsJoined(1);
                                        addView.setText("退出");
                                        addView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.btn_qexit_tj), null);
                                        sendJoinAndQuitBroadcast();
                                    } else {
                                        ToastUtil.showToast(context, defaultReponse.getResponseMessage());
                                    }
                                }
                            }
                        }.start("正在加入圈子");
                    }
                } else {
                    startActivity(new Intents(context, LoginActivity.class).toIntent());
                }
            }
        };
        addView.setOnClickListener(clickListener);
    }

    private void showScoreDialog(String message) {
        final Dialog mDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.setContentView(R.layout.first_login_dialog);
        TextView mTextView = (TextView) mDialog.findViewById(R.id.first_login_text);
        ImageButton mCloseButton = (ImageButton) mDialog.findViewById(R.id.first_login_close);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
        mTextView.setText(message);
        if(!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    private void sendJoinAndQuitBroadcast() {
        Intent mIntent = new Intent();
        mIntent.setAction(BroadcastActionUtil.IS_JOIN_QUIT_ACTION);
        sendBroadcast(mIntent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ModuleSpecs specs = (ModuleSpecs) parent.getItemAtPosition(position);
        if (specs != null) {
            if("DESC".equals(specs.getName())) {
                //简介
                MobclickAgent.onEvent(context, "group_info");
                Intent introIntent = new Intent(context, WebViewActivity.class);
                introIntent.putExtra("title", "简介");
                introIntent.putExtra("url", "http://social.api.ttq2014.com/group/desc.html?id=" + tvCircleId);
                startActivity(introIntent);
            }else if ("MEMBERS".equals(specs.getName())) {
                //圈子成员
            }else if ("POSTS".equals(specs.getName())) {
                //聊天室
            }else if ("TOPICS".equals(specs.getName())) {
                //话题
                MobclickAgent.onEvent(context, "group_topic");
                Intent mOfficialTopicIntent = new Intent(context, OfficialTvCircleTopicActivity.class);
                mOfficialTopicIntent.putExtra("groupName", name);
                mOfficialTopicIntent.putExtra("tvcircleId", tvCircleId);
                startActivity(mOfficialTopicIntent);
            }else if ("NEWS".equals(specs.getName())) {
                //资讯
                MobclickAgent.onEvent(context, "group_news");
                Intent newsIntent = new Intent(context, NewsListActivity.class);
                newsIntent.putExtra("id", tvCircleId);
                startActivity(newsIntent);
            }else if ("VIDEOS".equals(specs.getName())) {
                //视频
                MobclickAgent.onEvent(context, "group_video");
                Intent videoIntent = new Intent(context, VideoListingActivity.class);
                videoIntent.putExtra("id", tvCircleId);
                startActivity(videoIntent);
            }else if ("GAME".equals(specs.getName())) {
                //互动
                if (LoginUtil.isLogin(context)) {
                    execGame();
                } else {
                    Intent mIntent = new Intent(context, LoginActivity.class);
                    startActivity(mIntent);
                }

            }else if ("ENTRY_FORM".equals(specs.getName())) {
                //报名
                MobclickAgent.onEvent(context, "group_baoming");
                execApplyType();
            }else if ("LINKS".equals(specs.getName())) {
                //相关链接
            }
        }
    }

    private void execApplyType() {
        new AbstractRoboAsyncTask<ApplyType>(context){
            @Override
            protected ApplyType run(Object data) throws Exception {
                return new ResourcePager<ApplyType>(){
                    @Override
                    public PageIterator<ApplyType> createIterator(int page, int count) {
                        return service.pageApply(tvCircleId);
                    }
                }.start();
            }

            @Override
            protected void onSuccess(ApplyType applyType) throws Exception {
                super.onSuccess(applyType);
                if(applyType != null) {
                    if(applyType.getResponseCode() == 0) {
                        EntryForm mEntryForm = applyType.getEntryForms().get(0);
                        if(mEntryForm != null) {
                            if(mEntryForm != null && "GSTX_ENTRY".equals(mEntryForm.getTemplateName())) {
                                Intent applyIntent = new Intent(context, GSTXApplyActivity.class);
                                applyIntent.putExtra("id", mEntryForm.getId());
                                startActivity(applyIntent);
                            }else if(mEntryForm != null && "AQBWZ_ENTRY".equals(mEntryForm.getTemplateName())) {
                                Intent mIntent = new Intent(context, AQBWApplyActivity.class);
                                mIntent.putExtra("id", mEntryForm.getId());
                                mIntent.putExtra("clause", mEntryForm.getClause());
                                startActivity(mIntent);
                            }else if(mEntryForm != null && "FNMS_ENTRY".equals(mEntryForm.getTemplateName())) {
                                Intent mIntent = new Intent(context, FNMSApplyActivity.class);
                                mIntent.putExtra("id", mEntryForm.getId());
                                startActivity(mIntent);
                            }
                        }
                    }else  {
                        ToastUtil.showToast(context, applyType.getResponseMessage());
                    }
                }
            }
        }.execute();
    }

    private void execGame() {
        new AbstractRoboAsyncTask<Game>(context){
            @Override
            protected Game run(Object data) throws Exception {
                return new ResourcePager<Game>(){
                    @Override
                    public PageIterator<Game> createIterator(int page, int count) {
                        return service.pageGame(tvCircleId);
                    }
                }.start();
            }

            @Override
            protected void onSuccess(Game game) throws Exception {
                super.onSuccess(game);
                if (game != null) {
                    if (game.getResponseCode() == 0) {
                        if (game.getGames() != null && game.getGames().size() > 0) {
                            GameList mGameList = game.getGames().get(0);
                            if (mGameList != null) {
                                if ("AFFECTION_INDEX".equals(mGameList.getType())) {
                                    MobclickAgent.onEvent(context, "game_lovetest");
                                    startLoveExponentActivity();
                                } else if ("QUESTION".equals(mGameList.getType())) {
                                    startQuestionActivity();
                                } else if ("DRAW_LOT".equals(mGameList.getType())) {
                                    startShakeActivity();
                                } else if ("WEB_VIEW".equals(mGameList.getType())) {
                                    startWebViewActivity(mGameList);
                                }
                            }
                        }
                    }else {
                        ToastUtil.showToast(context, game.getResponseMessage());
                    }
                }
            }
        }.execute();
    }

    private void startWebViewActivity(GameList gameList) {
        if (LoginUtil.isLogin(context)) {
            Map<String, String> maps= GsonUtils.createGson().fromJson(gameList.getParam(), new TypeToken<Map<String, String>>(){}.getType());
            if (maps != null) {
                String link = maps.get("link");
                String noTitle = maps.get("noTitle");
                String title = maps.get("title");
                String backKey = maps.get("backKey");
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", link);
                intent.putExtra("noTitle", noTitle);
                intent.putExtra("title", title);
                intent.putExtra("backKey", backKey);
                startActivity(intent);
            }
        } else {
            startActivity(new Intent(context, LoginActivity.class));
        }
    }

    private void startLoveExponentActivity() {
        if (LoginUtil.isLogin(context)) {
            Intent mIntent = new Intent(context, LoveExponentActivity.class);
            startActivity(mIntent);
        } else {
            Intent mIntent = new Intent(context, LoginActivity.class);
            startActivity(mIntent);
        }
    }

    private void startQuestionActivity() {
        if (LoginUtil.isLogin(context)) {
            Intent mIntent = new Intent(context, ScanRankingActivity.class);
            startActivity(mIntent);
        } else {
            Intent mIntent = new Intent(context, LoginActivity.class);
            startActivity(mIntent);
        }
    }

    private void startShakeActivity() {
        if (LoginUtil.isLogin(context)) {
            Intent mIntent = new Intent(context, ShakeActivity.class);
            startActivity(mIntent);
        } else {
            Intent mIntent = new Intent(context, LoginActivity.class);
            startActivity(mIntent);
        }
    }
}
