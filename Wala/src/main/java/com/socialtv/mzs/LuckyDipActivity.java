package com.socialtv.mzs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.socialtv.R;
import com.socialtv.SharePage;
import com.socialtv.ShareTemplete;
import com.socialtv.core.Intents;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.ThirdPartyShareActivity;
import com.socialtv.core.ThrowableLoader;
import com.socialtv.core.ToastUtils;
import com.socialtv.core.Utils;
import com.socialtv.home.WebViewActivity;
import com.socialtv.http.HttpUtils;
import com.socialtv.mzs.entity.Game;
import com.socialtv.mzs.entity.GameVotes;
import com.socialtv.mzs.entity.LuckyDip;
import com.socialtv.mzs.entity.VotesResult;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-9-9.
 */
@ContentView(R.layout.lucky_dip)
public class LuckyDipActivity extends ThirdPartyShareActivity {

    private final static int HELP_ID = 0;

    private final static int LOADER_ID = 1;

    @Inject
    private Activity activity;

    @Inject
    private MZSService service;

    @InjectView(R.id.lucky_dip_image_layout)
    private FrameLayout imageLayout;

    @InjectView(R.id.lucky_dip_image)
    private ImageView imageView;

    @InjectView(R.id.lucky_dip_image_mark)
    private ImageView markImageView;

    @InjectView(R.id.lucky_dip_notice)
    private TextView noticeText;

    @InjectView(R.id.lucky_dip_name)
    private TextView nameText;

    @InjectView(R.id.lucky_dip_pass_and_elimination)
    private View passAndEliminationLayout;

    @InjectView(R.id.lucky_dip_pass)
    private TextView passText;

    @InjectView(R.id.lucky_dip_elimination)
    private TextView eliminationText;

    @InjectView(R.id.lucky_dip_normal)
    private TextView normalText;

    @InjectView(R.id.lucky_dip_normal_and_pass_layout)
    private View normalAndPassLayout;

    @InjectView(R.id.lucky_dip_result_msg)
    private TextView resultMsgText;

    @InjectView(R.id.lucky_dip_get_award)
    private View getAwardText;

    private String luckyDipId;

    private String rule;

    private Dialog dialog;

    private TextView dialogTitle;

    private TextView dialogNotice;

    private TextView dialogIKnow;

    private View dialogSina;

    private View dialogTencent;

    private View dialogWeixinFriendCircle;

    private View dialogWeixinFriend;

    private final Handler timerHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        luckyDipId = getStringExtra(IConstant.LUCKY_DIP_ID);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bdg_darktitbar_tomato));
        getSupportActionBar().setLogo(R.drawable.btn_back_tomato);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportLoaderManager().initLoader(LOADER_ID, null, callbacks);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = (int) ((metrics.widthPixels - Utils.dip2px(this, 32) * 2) / 0.8);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(metrics.widthPixels, height);
        params.leftMargin = Utils.dip2px(activity, 32);
        params.rightMargin = Utils.dip2px(activity, 32);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageLayout.setLayoutParams(params);
        getAwardText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(luckyDipId)) {
                    startActivity(new Intents(activity, GetAwardActivity.class).add(IConstant.LUCKY_DIP_ID, luckyDipId).toIntent());
                }
            }
        });
        timerHandler.postDelayed(timerRunnable, 15 * 1000);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            getSupportLoaderManager().restartLoader(LOADER_ID, null, callbacks);
        }
    };

    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(0);
            timerHandler.postDelayed(this, 15 * 1000);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
    }

    LoaderManager.LoaderCallbacks<LuckyDip> callbacks = new LoaderManager.LoaderCallbacks<LuckyDip>() {
        @Override
        public Loader<LuckyDip> onCreateLoader(int i, Bundle bundle) {
            return new ThrowableLoader<LuckyDip>(activity){
                @Override
                public LuckyDip loadData() throws Exception {
                    return (LuckyDip) HttpUtils.doRequest(service.createLuckyDipRequest(luckyDipId)).result;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<LuckyDip> luckyDipLoader, LuckyDip luckyDip) {
            if (luckyDip != null) {
                if (luckyDip.getResponseCode() == 0) {
                    Game game = luckyDip.getGame();
                    if (game != null) {
                        getSupportActionBar().setTitle(game.getName());
                        rule = game.getRule();
                        if (game.getIsStart() == 0) {
                            //未开始
                            setNotStart(game);
                        } else if (game.getIsStart() == 1) {
                            //已开始
                            setStart(game);
                        }
                    }
                } else {
                    ToastUtils.show(activity, luckyDip.getResponseMessage());
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<LuckyDip> luckyDipLoader) {

        }
    };

    private void setNotStart(Game game) {
        ImageLoader.getInstance().displayImage(game.getImg(), imageView, ImageUtils.imageLoader(activity, 2));
    }

    private void setStart(Game game) {
        noticeText.setText(game.getNotice());
        if (game.getVotes() != null && !game.getVotes().isEmpty()) {
            GameVotes votes = game.getVotes().get(0);
            if (votes != null) {
                nameText.setText(votes.getName());
                ImageLoader.getInstance().displayImage(votes.getImg(), imageView, ImageUtils.imageLoader(activity, 2));
                if (!TextUtils.isEmpty(votes.getResultMsg())) {
                    normalAndPassLayout.setVisibility(View.GONE);
                    resultMsgText.setVisibility(View.VISIBLE);
                    resultMsgText.setText(votes.getResultMsg());
                    markImageView.setVisibility(View.VISIBLE);
                } else {
                    normalAndPassLayout.setVisibility(View.VISIBLE);
                    resultMsgText.setVisibility(View.GONE);
                    markImageView.setVisibility(View.GONE);
                }

                if (TextUtils.isEmpty(votes.getVoteAction())) {
                    passAndEliminationLayout.setVisibility(View.VISIBLE);
                    normalText.setVisibility(View.GONE);
                } else {
                    passAndEliminationLayout.setVisibility(View.GONE);
                    normalText.setVisibility(View.VISIBLE);
                    if ("PASS".equals(votes.getVoteAction())) {
                        normalText.setText("通过");
                        normalText.setTextColor(getResources().getColor(R.color.cambridge_blue));
                    } else if ("FAILED".equals(votes.getVoteAction())) {
                        normalText.setText("淘汰");
                        normalText.setTextColor(getResources().getColor(R.color.red));
                    }
                }
                setPassOnClickListener(votes);
                setEliminationOnClickListener(votes);
            }
        }
    }

    private void setPassOnClickListener(final GameVotes votes) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ProgressDialogTask<VotesResult>(activity){
                    @Override
                    protected VotesResult run(Object data) throws Exception {
                        return (VotesResult) HttpUtils.doRequest(service.createVotesRequest(votes.getVid(), "PASS")).result;
                    }

                    @Override
                    protected void onSuccess(VotesResult response) throws Exception {
                        super.onSuccess(response);
                        if (response != null) {
                            if (response.getResponseCode() == 0) {
                                showDialog(response);
                            } else {
                                ToastUtils.show(activity, response.getResponseMessage());
                            }
                        }
                    }
                }.start("请稍候");
            }
        };
        passText.setOnClickListener(listener);
    }

    private void setEliminationOnClickListener(final GameVotes votes) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ProgressDialogTask<VotesResult>(activity){
                    @Override
                    protected VotesResult run(Object data) throws Exception {
                        return (VotesResult) HttpUtils.doRequest(service.createVotesRequest(votes.getVid(), "FAILED")).result;
                    }

                    @Override
                    protected void onSuccess(VotesResult response) throws Exception {
                        super.onSuccess(response);
                        if (response.getResponseCode() == 0) {
                            showDialog(response);
                        } else {
                            ToastUtils.show(activity, response.getResponseMessage());
                        }
                    }
                }.start("请稍候");
            }
        };
        eliminationText.setOnClickListener(listener);
    }

    private void showDialog(VotesResult result) {
        if (dialog == null) {
            dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setContentView(R.layout.lottery_dialog);
            dialogTitle = (TextView) dialog.findViewById(R.id.lottery_dialog_title);
            dialogNotice = (TextView) dialog.findViewById(R.id.lottery_dialog_notice);
            dialogIKnow = (TextView) dialog.findViewById(R.id.lottery_i_know);
            dialogIKnow.setText(R.string.i_know);
            dialogSina = dialog.findViewById(R.id.lottery_dialog_share_sina);
            dialogTencent = dialog.findViewById(R.id.lottery_dialog_share_tencent);
            dialogWeixinFriendCircle = dialog.findViewById(R.id.lottery_dialog_share_weixin);
            dialogWeixinFriend = dialog.findViewById(R.id.lottery_dialog_share_weixin_friend);
        }
        if (result != null) {
            dialogTitle.setText(result.getTitle());
            dialogNotice.setText(result.getNotice());

        }
        dialogIKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                getSupportLoaderManager().restartLoader(LOADER_ID, null, callbacks);
            }
        });
        final ShareTemplete templete = result.getShareTemplete();
        dialogSina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
        dialogTencent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        dialogWeixinFriendCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        dialogWeixinFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (templete != null) {
                    final SharePage page = templete.getSharePage();
                    if (page != null) {
                        if (TextUtils.isEmpty(page.getIcon())) {
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
                            shareWeiXinFriendWebPage(page.getUrl(), page.getTitle(), page.getDesc(), bitmap);
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
            }
        });
        if (dialog != null && !dialog.isShowing())
            dialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, HELP_ID, 0, "帮助")
                .setIcon(R.drawable.btn_help_mzs)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == HELP_ID) {
            startActivity(new Intents(activity, WebViewActivity.class).add(IConstant.URL, rule).toIntent());
        }
        return super.onOptionsItemSelected(item);
    }
}
