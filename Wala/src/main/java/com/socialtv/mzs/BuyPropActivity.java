package com.socialtv.mzs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.alipay.android.app.sdk.AliPay;
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.DialogFragmentActivity;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.ToastUtils;
import com.socialtv.http.HttpUtils;
import com.socialtv.mzs.entity.AliPayResult;
import com.socialtv.mzs.entity.BuyPropSuccess;
import com.socialtv.mzs.entity.GamePayments;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;

import java.util.HashMap;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-9-3.
 * 梦之声中的购买道具的页面
 */
@ContentView(R.layout.buy_prop)
public class BuyPropActivity extends DialogFragmentActivity {

    private final static int RQF_PAY = 1;

    @InjectView(R.id.buy_prop_image)
    private ImageView imageView;

    @InjectView(R.id.buy_prop_desc)
    private TextView descText;

    @InjectView(R.id.buy_prop_score)
    private TextView scoreText;

    @InjectView(R.id.buy_prop_money)
    private TextView moneyText;

    @InjectView(R.id.buy_prop_score_layout)
    private View scoreLayout;

    @InjectView(R.id.buy_prop_money_layout)
    private View moneyLayout;

    @InjectExtra(value = IConstant.BUY_PROP_ID, optional = true)
    private long buyPropId;

    @InjectExtra(value = IConstant.BUY_PROP_PRICE_SCORE, optional = true)
    private GamePayments scoreGamePayments;

    @InjectExtra(value = IConstant.BUY_PROP_PRICE_MONEY, optional = true)
    private GamePayments moneyGamePayments;

    @Inject
    private Activity activity;

    @Inject
    private MZSService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bdg_darktitbar_tomato));
        getSupportActionBar().setLogo(R.drawable.btn_roundback_tomato);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (int) (metrics.widthPixels / 0.88));
        imageView.setLayoutParams(params);
        ImageLoader.getInstance().displayImage(getStringExtra(IConstant.BUY_PROP_BG), imageView, ImageUtils.imageLoader(this, 0));
        descText.setText(getStringExtra(IConstant.BUY_PROP_DESC));
        scoreText.setText(scoreGamePayments.getShowPrice());
        moneyText.setText(moneyGamePayments.getShowPrice());

        scoreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ProgressDialogTask<BuyPropSuccess>(activity){
                    @Override
                    protected BuyPropSuccess run(Object data) throws Exception {
                        return (BuyPropSuccess) HttpUtils.doRequest(service.createBuyPropRequest(buyPropId+"", scoreGamePayments.getId() + "")).result;
                    }

                    @Override
                    protected void onSuccess(BuyPropSuccess buyPropSuccess) throws Exception {
                        super.onSuccess(buyPropSuccess);
                        if (buyPropSuccess != null) {
                            if (buyPropSuccess.getResponseCode() == 0) {
                                ToastUtils.show(activity, "兑换成功");
                                setResult(RESULT_OK);
                            } else {
                                ToastUtils.show(activity, buyPropSuccess.getResponseMessage());
                            }
                        }
                    }
                }.start("请稍候");
            }
        });

        moneyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ProgressDialogTask<BuyPropSuccess>(activity){
                    @Override
                    protected BuyPropSuccess run(Object data) throws Exception {
                        return (BuyPropSuccess) HttpUtils.doRequest(service.createBuyPropRequest(buyPropId + "", moneyGamePayments.getId() + "")).result;
                    }

                    @Override
                    protected void onSuccess(final BuyPropSuccess buyPropSuccess) throws Exception {
                        super.onSuccess(buyPropSuccess);
                        if (buyPropSuccess != null) {
                            if (buyPropSuccess.getResponseCode() == 0) {
                                if (buyPropSuccess.getReceipt() != null) {
                                    new Thread(){
                                        @Override
                                        public void run() {
                                            super.run();
                                            AliPay aliPay = new AliPay(activity, mHandler);
                                            String result = aliPay.pay(buyPropSuccess.getReceipt().getPurchaseInfo());
                                            AliPayResult aliPayResult = new AliPayResult();
                                            aliPayResult.setResult(result);
                                            aliPayResult.setTranId(buyPropSuccess.getReceipt().getTransactionId());
                                            Message msg = Message.obtain();
                                            msg.what = RQF_PAY;
                                            msg.obj = aliPayResult;
                                            mHandler.sendMessage(msg);
                                        }
                                    }.start();
                                }
                            } else {
                                ToastUtils.show(activity, buyPropSuccess.getResponseMessage());
                            }
                        }
                    }
                }.start("请稍候");
            }
        });
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == RQF_PAY) {
                final AliPayResult result = (AliPayResult) msg.obj;
                new AbstractRoboAsyncTask<BuyPropSuccess>(activity) {
                    @Override
                    protected BuyPropSuccess run(Object data) throws Exception {
                        final Map<String, String> bodys = new HashMap<String, String>();
                        bodys.put("tranId", result.getTranId());
                        bodys.put("receipt", result.getResult());
                        return (BuyPropSuccess) HttpUtils.doRequest(service.createBuyPropConfirmRequest(bodys)).result;
                    }

                    @Override
                    protected void onSuccess(BuyPropSuccess response) throws Exception {
                        super.onSuccess(response);
                        if (response != null) {
                            if (response.getResponseCode() == 0) {
                                setResult(RESULT_OK);
                            } else {
                                ToastUtils.show(activity, response.getResponseMessage());
                            }
                        }
                    }
                }.execute();
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
