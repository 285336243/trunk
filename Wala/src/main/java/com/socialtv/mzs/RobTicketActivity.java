package com.socialtv.mzs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.Intents;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.ThirdPartyShareActivity;
import com.socialtv.core.ThrowableLoader;
import com.socialtv.core.ToastUtils;
import com.socialtv.home.WebViewActivity;
import com.socialtv.http.HttpUtils;
import com.socialtv.mzs.entity.BuyPropRefresh;
import com.socialtv.mzs.entity.Game;
import com.socialtv.mzs.entity.GamePayments;
import com.socialtv.mzs.entity.GameTools;
import com.socialtv.mzs.entity.Lottery;
import com.socialtv.mzs.entity.Marquee;
import com.socialtv.mzs.entity.MarqueeResult;
import com.socialtv.mzs.entity.RobTicket;
import com.socialtv.shop.entity.GetShipResponse;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-9-2.
 * 抽奖页面
 */
@ContentView(R.layout.rob_ticket)
public class RobTicketActivity extends ThirdPartyShareActivity {

    private final static int HELP_ID = 1;

    private final static int LOADER_ID = 100;

    private final static int REQUEST = 100;

    @InjectExtra(value = IConstant.ROB_TICKET_ID, optional = true)
    private String robTicketId;

    @Inject
    private Activity activity;

    @Inject
    private MZSService service;

    @InjectView(R.id.rob_ticket_marquee_text)
    private ScrollTextView marqueeText;

    @InjectView(R.id.rob_ticket_key_image)
    private ImageView keyImage;

    @InjectView(R.id.rob_ticket_key_count)
    private TextView keyCountText;

    @InjectView(R.id.rob_ticket_add_key)
    private View addKeyView;

    @InjectView(R.id.rob_ticket_tools_image_1)
    private ImageView imageToolsOne;

    @InjectView(R.id.rob_ticket_tools_count_1)
    private TextView textToolsOne;

    @InjectView(R.id.rob_ticket_tools_title_1)
    private TextView titleToolsOne;

    @InjectView(R.id.rob_ticket_buy_1)
    private View buyToolsOne;

    @InjectView(R.id.rob_ticket_check_1)
    private CheckBox checkToolsOne;

    @InjectView(R.id.rob_ticket_tools_layout_1)
    private View toolsLayoutOne;

    @InjectView(R.id.rob_ticket_tools_image_2)
    private ImageView imageToolsTwo;

    @InjectView(R.id.rob_ticket_tools_count_2)
    private TextView textToolsTwo;

    @InjectView(R.id.rob_ticket_tools_title_2)
    private TextView titleToolsTwo;

    @InjectView(R.id.rob_ticket_buy_2)
    private View buyToolsTwo;

    @InjectView(R.id.rob_ticket_check_2)
    private CheckBox checkToolsTwo;

    @InjectView(R.id.rob_ticket_tools_layout_2)
    private View toolsLayoutTwo;

    @InjectView(R.id.rob_ticket_tools_image_3)
    private ImageView imageToolsThree;

    @InjectView(R.id.rob_ticket_tools_count_3)
    private TextView textToolsThree;

    @InjectView(R.id.rob_ticket_tools_title_3)
    private TextView titleToolsThree;

    @InjectView(R.id.rob_ticket_buy_3)
    private View buyToolsThree;

    @InjectView(R.id.rob_ticket_check_3)
    private CheckBox checkToolsThree;

    @InjectView(R.id.rob_ticket_tools_layout_3)
    private View toolsLayoutThree;

    @InjectView(R.id.rob_ticket_immediate_open)
    private View immediateOpenView;

    private Dialog dialog;

    private View dialogClose;

    private TextView dialogName;

    private TextView dialogTime;

    private TextView dialogVerse;

    private TextView dialogDesc;

    private EditText dialogNameEdit;

    private EditText dialogMobileEdit;

    private View dialogSubmit;

    private String rule;

    private GameTools keyTools;

    private GameTools propTools;

    private Dialog lotteryDialog;

    private TextView lotteryDialogTitle;

    private TextView lotteryDialogNotice;

    private TextView lotteryDialogIKnow;

    private View lotteryDialogSina;

    private View lotteryDialogTencent;

    private View lotteryDialogWeixinFriendCircle;

    private View lotteryDialogWeixinFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bdg_darktitbar_tomato));
        getSupportActionBar().setLogo(R.drawable.btn_back_tomato);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.rob_ticket_dialog);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    if (dialog != null && dialog.isShowing())
                        finish();
                    return true;
                }
                return false;
            }
        });
        dialogClose = dialog.findViewById(R.id.rob_ticket_dialog_close);
        dialogName = (TextView) dialog.findViewById(R.id.rob_ticket_dialog_name);
        dialogTime = (TextView) dialog.findViewById(R.id.rob_ticket_dialog_time);
        dialogVerse = (TextView) dialog.findViewById(R.id.rob_ticket_dialog_verse);
        dialogDesc = (TextView) dialog.findViewById(R.id.rob_ticket_dialog_desc);
        dialogNameEdit = (EditText) dialog.findViewById(R.id.rob_ticket_dialog_edit_name);
        dialogMobileEdit = (EditText) dialog.findViewById(R.id.rob_ticket_dialog_edit_mobile);
        dialogSubmit = dialog.findViewById(R.id.rob_ticket_dialog_submit);
        dialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (!dialog.isShowing()) {
            dialog.show();
        }

        getSupportLoaderManager().initLoader(LOADER_ID, null, callbacks);

        new AbstractRoboAsyncTask<Marquee>(activity){
            @Override
            protected Marquee run(Object data) throws Exception {
                return (Marquee) HttpUtils.doRequest(service.createMarqueeRequest(robTicketId)).result;
            }

            @Override
            protected void onSuccess(Marquee marquee) throws Exception {
                super.onSuccess(marquee);
                if (marquee != null) {
                    if (marquee.getResponseCode() == 0 && marquee.getResult() != null && !marquee.getResult().isEmpty()) {
                        StringBuilder builder = new StringBuilder();
                        for (int i = 0; i < marquee.getResult().size(); i++) {
                            MarqueeResult result = marquee.getResult().get(i);
                            if (result != null) {
                                builder.append(result.getMsg());
                                if (i != marquee.getResult().size())
                                    builder.append(", ");
                            }
                        }
                        marqueeText.setText(builder.toString());
                        marqueeText.startScroll();
                    }
                }
            }
        }.execute();
    }

    LoaderManager.LoaderCallbacks<RobTicket> callbacks = new LoaderManager.LoaderCallbacks<RobTicket>() {
        @Override
        public Loader<RobTicket> onCreateLoader(int i, Bundle bundle) {
            return new ThrowableLoader<RobTicket>(activity) {
                @Override
                public RobTicket loadData() throws Exception {
                    return (RobTicket) HttpUtils.doRequest(service.createRobTicketRequest(robTicketId)).result;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<RobTicket> robTicketLoader, RobTicket robTicket) {
            if (robTicket != null) {
                if (robTicket.getResponseCode() == 0) {
                    if (robTicket.getGame() != null) {
                        rule = robTicket.getGame().getRule();
                        showDialog(robTicket.getGame());
                        setRobTicketContent(robTicket.getGame().getTools());
                        immediateOpenOnClickListener();
                    }
                } else {
                    ToastUtils.show(activity, robTicket.getResponseMessage());
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<RobTicket> robTicketLoader) {

        }
    };

    private void setRobTicketContent(final List<GameTools> tools) {
        checkToolsOne.setChecked(false);
        checkToolsTwo.setChecked(false);
        checkToolsThree.setChecked(false);
        if (tools != null && !tools.isEmpty()) {
            for (int i=0,j=0; i<tools.size(); i++) {
                final GameTools tool = tools.get(i);
                if (tool != null) {
                    if ("ACCESS".equals(tool.getType())) {
                        ImageLoader.getInstance().displayImage(tool.getIcon(), keyImage, ImageUtils.imageLoader(activity, 0));
                        keyCountText.setText("x" + tool.getAmount());
                        addKeyOnClickListener(tool);
                        keyTools = tool;
                    } else if ("LUCK".equals(tool.getType())) {
                       switch (j){
                           case 0:
                               ImageLoader.getInstance().displayImage(tools.get(j).getIcon(), imageToolsOne, ImageUtils.imageLoader(activity, 0));
                               titleToolsOne.setText(tools.get(j).getTitle());
                               textToolsOne.setText("x" + tools.get(j).getAmount());
                               toolsLayoutOneOnClickListener(tools.get(j));
                               buyOneOnClickListener(tools.get(j));
                               break;
                           case 1:
                               ImageLoader.getInstance().displayImage(tools.get(j).getIcon(), imageToolsTwo, ImageUtils.imageLoader(activity, 0));
                               titleToolsTwo.setText(tools.get(j).getTitle());
                               textToolsTwo.setText("x" + tools.get(j).getAmount());
                               toolsLayoutTwoOnClickListener(tools.get(j));
                               buyTwoOnClickListener(tools.get(j));
                               break;
                           case 2:
                               ImageLoader.getInstance().displayImage(tools.get(j).getIcon(), imageToolsThree, ImageUtils.imageLoader(activity, 0));
                               titleToolsThree.setText(tools.get(j).getTitle());
                               textToolsThree.setText("x" + tools.get(j).getAmount());
                               toolsLayoutThreeOnClickListener(tools.get(j));
                               buyThreeOnClickListener(tools.get(j));
                               break;
                       }
                       j++;
                    }
                }
            }
        }
    }

    private void immediateOpenOnClickListener() {
        immediateOpenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final StringBuilder builder = new StringBuilder();
                if (keyTools != null) {
                    if (keyTools.getAmount() <= 0) {
                        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
                        dialog.setContentView(R.layout.rob_ticket_tip_dialog);
                        View buyView = dialog.findViewById(R.id.rob_ticket_tip_dialog_buy);
                        buyView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intents intents = new Intents(activity, BuyPropActivity.class)
                                        .add(IConstant.BUY_PROP_BG, keyTools.getImg())
                                        .add(IConstant.BUY_PROP_ID, keyTools.getId())
                                        .add(IConstant.BUY_PROP_DESC, keyTools.getDesc());
                                if (keyTools.getPayments() != null && !keyTools.getPayments().isEmpty()) {
                                    for (GamePayments payments : keyTools.getPayments()) {
                                        if (payments != null) {
                                            if ("SCORE".equals(payments.getPayType())) {
                                                intents.add(IConstant.BUY_PROP_PRICE_SCORE, payments);
                                            } else if ("ALIPAY".equals(payments.getPayType())) {
                                                intents.add(IConstant.BUY_PROP_PRICE_MONEY, payments);
                                            }
                                        }
                                    }
                                }
                                startActivityForResult(intents.toIntent(), REQUEST);
                                if (dialog.isShowing())
                                    dialog.dismiss();
                            }
                        });
                        View cancelView = dialog.findViewById(R.id.rob_ticket_tip_dialog_cancel);
                        cancelView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (dialog.isShowing())
                                    dialog.dismiss();
                            }
                        });
                        if (!dialog.isShowing())
                            dialog.show();
                        return;
                    }
                    builder.append(keyTools.getId())
                            .append(":")
                            .append("1")
                            .append(",");
                }
                if (propTools != null) {
                    builder.append(propTools.getId())
                            .append(":")
                            .append("1");
                }
                new ProgressDialogTask<Lottery>(activity){
                    @Override
                    protected Lottery run(Object data) throws Exception {
                        return (Lottery) HttpUtils.doRequest(service.createLotteryRequest(robTicketId, builder.toString())).result;
                    }

                    @Override
                    protected void onSuccess(Lottery lottery) throws Exception {
                        super.onSuccess(lottery);
                        if (lottery != null) {
                            if (lottery.getResponseCode() == 0) {
                                showLotteryDialog(lottery);
                                new AbstractRoboAsyncTask<BuyPropRefresh>(activity){
                                    @Override
                                    protected BuyPropRefresh run(Object data) throws Exception {
                                        return (BuyPropRefresh) HttpUtils.doRequest(service.createRobTicketRefresh(robTicketId)).result;
                                    }

                                    @Override
                                    protected void onSuccess(BuyPropRefresh buyPropRefresh) throws Exception {
                                        super.onSuccess(buyPropRefresh);
                                        if (buyPropRefresh != null) {
                                            if (buyPropRefresh.getResponseCode() == 0) {
                                                setRobTicketContent(buyPropRefresh.getTools());
                                            } else {
                                                ToastUtils.show(activity, buyPropRefresh.getResponseMessage());
                                            }
                                        }
                                    }
                                }.execute();
                            } else {
                                ToastUtils.show(activity, lottery.getResponseMessage());
                            }
                        }
                    }
                }.start("请稍候");
            }
        });
    }

    private void showLotteryDialog(Lottery lottery) {
        if (lotteryDialog == null) {
            lotteryDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            lotteryDialog.setContentView(R.layout.lottery_dialog);
            lotteryDialogTitle = (TextView) lotteryDialog.findViewById(R.id.lottery_dialog_title);
            lotteryDialogNotice = (TextView) lotteryDialog.findViewById(R.id.lottery_dialog_notice);
            lotteryDialogIKnow = (TextView) lotteryDialog.findViewById(R.id.lottery_i_know);
            lotteryDialogSina = lotteryDialog.findViewById(R.id.lottery_dialog_share_sina);
            lotteryDialogTencent = lotteryDialog.findViewById(R.id.lottery_dialog_share_tencent);
            lotteryDialogWeixinFriendCircle = lotteryDialog.findViewById(R.id.lottery_dialog_share_weixin);
            lotteryDialogWeixinFriend = lotteryDialog.findViewById(R.id.lottery_dialog_share_weixin_friend);
        }
        if (lottery.getResult() != null) {
            lotteryDialogTitle.setText(lottery.getResult().getTitle());
            lotteryDialogNotice.setText(lottery.getResult().getNotice());
            if (lottery.getResult().getPrizeLevel() == 0) {
                lotteryDialogIKnow.setText(getString(R.string.i_know));
            } else {
                lotteryDialogIKnow.setText(getString(R.string.repeat));
            }
        }
        lotteryDialogIKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lotteryDialog != null && lotteryDialog.isShowing())
                    lotteryDialog.dismiss();
            }
        });
        final ShareTemplete templete = lottery.getShareTemplete();
        lotteryDialogSina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (templete != null) {
                    if (TextUtils.isEmpty(templete.getShareImg())) {
                        sinaShareText(templete.getShareText());
                    } else {
                        ImageLoader.getInstance().loadImage(templete.getShareImg(), ImageUtils.imageLoader(activity, 0), new SimpleImageLoadingListener(){
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
        lotteryDialogTencent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (templete != null) {
                    if (TextUtils.isEmpty(templete.getShareImg())) {
                        tencentShareText(templete.getShareText());
                    } else {
                        ImageLoader.getInstance().loadImage(templete.getShareImg(), ImageUtils.imageLoader(activity, 0), new SimpleImageLoadingListener(){
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

        lotteryDialogWeixinFriendCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (templete != null) {
                    final SharePage page = templete.getSharePage();
                    if (page != null) {
                        if (TextUtils.isEmpty(page.getIcon())) {
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
                            shareWeiXinWebPage(page.getUrl(), page.getTitle(), page.getDesc(), bitmap);
                        } else {
                            ImageLoader.getInstance().loadImage(page.getIcon(), new SimpleImageLoadingListener(){
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

        lotteryDialogWeixinFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (templete != null) {
                    final SharePage page = templete.getSharePage();
                    if (page != null) {
                        if (TextUtils.isEmpty(page.getIcon())) {
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
                            shareWeiXinFriendWebPage(page.getUrl(), page.getTitle(), page.getDesc(), bitmap);
                        } else {
                            ImageLoader.getInstance().loadImage(page.getIcon(), new SimpleImageLoadingListener(){
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
        if (lotteryDialog != null && !lotteryDialog.isShowing())
            lotteryDialog.show();
    }

    private void toolsLayoutOneOnClickListener(final GameTools tools) {
        toolsLayoutOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkToolsOne.isChecked()) {
                    checkToolsOne.setChecked(false);
                } else {
                    startBuyPropActivity(tools);
                    checkToolsOne.setChecked(true);
                }
                checkToolsTwo.setChecked(false);
                checkToolsThree.setChecked(false);
                propTools = tools;
            }
        });
    }

    private void toolsLayoutTwoOnClickListener(final GameTools tools) {
        toolsLayoutTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkToolsOne.setChecked(false);
                if (checkToolsTwo.isChecked()) {
                    checkToolsTwo.setChecked(false);
                } else {
                    startBuyPropActivity(tools);
                    checkToolsTwo.setChecked(true);
                }
                checkToolsThree.setChecked(false);
                propTools = tools;
            }
        });
    }

    private void toolsLayoutThreeOnClickListener(final GameTools tools) {
        toolsLayoutThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkToolsOne.setChecked(false);
                checkToolsTwo.setChecked(false);
                if (checkToolsThree.isChecked()) {
                    checkToolsThree.setChecked(false);
                } else {
                    startBuyPropActivity(tools);
                    checkToolsThree.setChecked(true);
                }
                propTools = tools;
            }
        });
    }

    private void startBuyPropActivity(GameTools tools) {
        if (tools.getAmount() <= 0) {
            Intents intents = new Intents(activity, BuyPropActivity.class)
                    .add(IConstant.BUY_PROP_BG, tools.getImg())
                    .add(IConstant.BUY_PROP_ID, tools.getId())
                    .add(IConstant.BUY_PROP_DESC, tools.getDesc());
            if (tools.getPayments() != null && !tools.getPayments().isEmpty()) {
                for (GamePayments payments : tools.getPayments()) {
                    if (payments != null) {
                        if ("SCORE".equals(payments.getPayType())) {
                            intents.add(IConstant.BUY_PROP_PRICE_SCORE, payments);
                        } else if ("ALIPAY".equals(payments.getPayType())) {
                            intents.add(IConstant.BUY_PROP_PRICE_MONEY, payments);
                        }
                    }
                }
            }
            startActivityForResult(intents.toIntent(), REQUEST);
        }
    }

    private void addKeyOnClickListener(final GameTools tools) {
        addKeyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intents intents = new Intents(activity, BuyPropActivity.class)
                        .add(IConstant.BUY_PROP_BG, tools.getImg())
                        .add(IConstant.BUY_PROP_ID, tools.getId())
                        .add(IConstant.BUY_PROP_DESC, tools.getDesc());
                if (tools.getPayments() != null && !tools.getPayments().isEmpty()) {
                    for (GamePayments payments : tools.getPayments()) {
                        if (payments != null) {
                            if ("SCORE".equals(payments.getPayType())) {
                                intents.add(IConstant.BUY_PROP_PRICE_SCORE, payments);
                            } else if ("ALIPAY".equals(payments.getPayType())) {
                                intents.add(IConstant.BUY_PROP_PRICE_MONEY, payments);
                            }
                        }
                    }
                }
                startActivityForResult(intents.toIntent(), REQUEST);
            }
        });
    }

    private void buyOneOnClickListener(final GameTools tools) {
        buyToolsOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intents intents = new Intents(activity, BuyPropActivity.class)
                        .add(IConstant.BUY_PROP_BG, tools.getImg())
                        .add(IConstant.BUY_PROP_ID, tools.getId())
                        .add(IConstant.BUY_PROP_DESC, tools.getDesc());
                if (tools.getPayments() != null && !tools.getPayments().isEmpty()) {
                    for (GamePayments payments : tools.getPayments()) {
                        if (payments != null) {
                            if ("SCORE".equals(payments.getPayType())) {
                                intents.add(IConstant.BUY_PROP_PRICE_SCORE, payments);
                            } else if ("ALIPAY".equals(payments.getPayType())) {
                                intents.add(IConstant.BUY_PROP_PRICE_MONEY, payments);
                            }
                        }
                    }
                }
                startActivityForResult(intents.toIntent(), REQUEST);
            }
        });
    }

    private void buyTwoOnClickListener(final GameTools tools) {
        buyToolsTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intents intents = new Intents(activity, BuyPropActivity.class)
                        .add(IConstant.BUY_PROP_BG, tools.getImg())
                        .add(IConstant.BUY_PROP_ID, tools.getId())
                        .add(IConstant.BUY_PROP_DESC, tools.getDesc());
                if (tools.getPayments() != null && !tools.getPayments().isEmpty()) {
                    for (GamePayments payments : tools.getPayments()) {
                        if (payments != null) {
                            if ("SCORE".equals(payments.getPayType())) {
                                intents.add(IConstant.BUY_PROP_PRICE_SCORE, payments);
                            } else if ("ALIPAY".equals(payments.getPayType())) {
                                intents.add(IConstant.BUY_PROP_PRICE_MONEY, payments);
                            }
                        }
                    }
                }
                startActivityForResult(intents.toIntent(), REQUEST);
            }
        });
    }

    private void buyThreeOnClickListener(final GameTools tools) {
        buyToolsThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intents intents = new Intents(activity, BuyPropActivity.class)
                        .add(IConstant.BUY_PROP_BG, tools.getImg())
                        .add(IConstant.BUY_PROP_ID, tools.getId())
                        .add(IConstant.BUY_PROP_DESC, tools.getDesc());
                if (tools.getPayments() != null && !tools.getPayments().isEmpty()) {
                    for (GamePayments payments : tools.getPayments()) {
                        if (payments != null) {
                            if ("SCORE".equals(payments.getPayType())) {
                                intents.add(IConstant.BUY_PROP_PRICE_SCORE, payments);
                            } else if ("ALIPAY".equals(payments.getPayType())) {
                                intents.add(IConstant.BUY_PROP_PRICE_MONEY, payments);
                            }
                        }
                    }
                }
                startActivityForResult(intents.toIntent(), REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        checkToolsOne.setChecked(false);
        checkToolsTwo.setChecked(false);
        checkToolsThree.setChecked(false);
        if (requestCode == REQUEST && resultCode != RESULT_CANCELED) {
            new AbstractRoboAsyncTask<BuyPropRefresh>(activity){
                @Override
                protected BuyPropRefresh run(Object data) throws Exception {
                    return (BuyPropRefresh) HttpUtils.doRequest(service.createRobTicketRefresh(robTicketId)).result;
                }

                @Override
                protected void onSuccess(BuyPropRefresh buyPropRefresh) throws Exception {
                    super.onSuccess(buyPropRefresh);
                    if (buyPropRefresh != null) {
                        if (buyPropRefresh.getResponseCode() == 0) {
                            setRobTicketContent(buyPropRefresh.getTools());
                        } else {
                            ToastUtils.show(activity, buyPropRefresh.getResponseMessage());
                        }
                    }
                }
            }.execute();
        } else {
            propTools = null;
        }
    }

    private void showDialog(final Game game) {
        dialogName.setText(game.getName());
        dialogTime.setText(game.getTime());
        dialogVerse.setText(game.getVerse());
        dialogDesc.setText(game.getNotice());

        if (game.getIsStart() == 0) {
            dialogNameEdit.setVisibility(View.GONE);
            dialogMobileEdit.setVisibility(View.GONE);
            dialogSubmit.setVisibility(View.GONE);
        } else {
            new AbstractRoboAsyncTask<GetShipResponse>(activity){
                @Override
                protected GetShipResponse run(Object data) throws Exception {
                    return (GetShipResponse) HttpUtils.doRequest(service.cretaeGetMobileRequest()).result;
                }

                @Override
                protected void onSuccess(GetShipResponse response) throws Exception {
                    super.onSuccess(response);
                    if (response != null) {
                        if (response.getResponseCode() == 0) {
                            if (response.getShipping() != null) {
                                dialogNameEdit.setText(response.getShipping().getName());
                                dialogMobileEdit.setText(response.getShipping().getMobile());
                            }
                        }
                    }
                }
            }.execute();

            dialogSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String name = dialogNameEdit.getText().toString();
                    if (TextUtils.isEmpty(name)) {
                        ToastUtils.show(activity, "姓名不能为空");
                        return;
                    }
                    final String mobile = dialogMobileEdit.getText().toString();
                    if (TextUtils.isEmpty(mobile)) {
                        ToastUtils.show(activity, "手机号码不能为空");
                        return;
                    }
                    final Map<String, String> bodys = new HashMap<String, String>();
                    bodys.put("name", name);
                    bodys.put("mobile", mobile);
                    new ProgressDialogTask<GetShipResponse>(activity){
                        @Override
                        protected GetShipResponse run(Object data) throws Exception {
                            return (GetShipResponse) HttpUtils.doRequest(service.createSubmitMobileRequest(bodys)).result;
                        }

                        @Override
                        protected void onSuccess(GetShipResponse response) throws Exception {
                            super.onSuccess(response);
                            if (response != null) {
                                if (response.getResponseCode() == 0) {
                                    if (dialog != null && dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                } else {
                                    ToastUtils.show(activity, response.getResponseMessage());
                                }
                            }
                        }
                    }.start("请稍候");
                }
            });
        }
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
