package com.socialtv.shop;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.github.kevinsawicki.wishlist.ViewFinder;
import com.google.inject.Inject;
import com.socialtv.R;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.DialogFragmentActivity;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.ToastUtils;
import com.socialtv.http.HttpUtils;
import com.socialtv.http.MultipartRequest;
import com.socialtv.personcenter.LoginActivity;
import com.socialtv.publicentity.Pics;
import com.socialtv.shop.entity.GetShipResponse;
import com.socialtv.shop.entity.Shipping;
import com.socialtv.shop.entity.ShopDetaildResponse;
import com.socialtv.util.IConstant;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;

import roboguice.inject.InjectView;

/**
 * 商品详情
 */
public class ShopDetailsActivity extends DialogFragmentActivity {
    @Inject
    ShopGetService service;

    @InjectView(R.id.shop_show_layout)
    private LinearLayout shopShowLayout;

    @InjectView(R.id.goods_name)
    private TextView goodsName;

    @InjectView(R.id.goods_description)
    private TextView goodsDescription;

    @InjectView(R.id.user_score)
    private TextView userScore;

    @InjectView(R.id.exchange_button)
    private Button exchangeButton;

    @InjectView(R.id.shop_details_root_view)
    private View rootView;

    @InjectView(R.id.pb_loading)
    private ProgressBar progressBar;

    @InjectView(android.R.id.empty)
    private TextView emptyTextView;

    @Inject
    private ShopBannerAdapter shopBannerAdapter;

    private Dialog popDialog;
    private Context context = ShopDetailsActivity.this;
    private String id;
    private int width;
    private ViewPager pager;
    private CirclePageIndicator indicator;
    private final Handler timerHandler = new Handler();
    private int currentItem = 0;
    private String exchangeMode;
    private static final String INFORMATION_SUB_URL = "shop/exchange.json";
    private EditText userName;
    private EditText userTelphone;
    private EditText userAddress;
    private View subButton, closeButton;
    private View shippingTips;
    private int goodsId;
    private String subName;
    private String subPhone;
    private String subAddress;
    private DefineBroadcastReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_details_layout);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bdg_darktitbar_tomato));
        getSupportActionBar().setIcon(R.drawable.btn_roundback_tomato);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//不显示标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左上角图标可点击
        setSupportProgressBarIndeterminateVisibility(false);
        id = getStringExtra(IConstant.SHOP_ID);

        addBannerView();
        exchangeButton.setOnClickListener(onClickListener);
        popDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        popDialog.setCanceledOnTouchOutside(false);
        loadShopData();
        mReceiver = new DefineBroadcastReceiver();
        registerReceiver(mReceiver, new IntentFilter(IConstant.USER_LOGIN));
    }
    private class DefineBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (IConstant.USER_LOGIN.equals(intent.getAction())) {
                    loadShopData();
                }
            }

        }

    }


    private void initCongfirmDialog(String responseMessage) {
        final Dialog confirmDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        confirmDialog.setCanceledOnTouchOutside(false);
        confirmDialog.setContentView(R.layout.confirm_dialog);
        TextView showResponeShow = (TextView) confirmDialog.findViewById(R.id.message_show_textview);
        TextView confirm = (TextView) confirmDialog.findViewById(R.id.confirm_textview);
        showResponeShow.setText(responseMessage);
        if (!confirmDialog.isShowing()) {
            confirmDialog.show();
        }
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmDialog.isShowing() || confirmDialog != null)
                    confirmDialog.dismiss();
            }
        });

    }

    public enum ExchangeMode {
        SHIPPING, NO_LOGIN, NO_ENOUGH_SCORE, NONE;

        public static ExchangeMode getString(String string) {
            return valueOf(string);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (null != exchangeMode) {
                switch (ExchangeMode.getString(exchangeMode)) {
                    case SHIPPING://SHIPPING    展示物流页面
                        voidinitDialog();
                        getShippingData();
                        break;

                    case NO_LOGIN:    //NO_LOGIN  未登录
                        ShopDetailsActivity.this.startActivity(new Intent(context, LoginActivity.class));
//                        finish();
                        break;
                    case NO_ENOUGH_SCORE: //NO_ENOUGH_SCORE 积分不足
                        initCongfirmDialog("番茄币不足");
                        break;
                    case NONE:        //NONE           直接兑换
                        submitShippingInfor(false);
                        break;

                    default:
                        break;
                }
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void addBannerView() {
        View headerview = View.inflate(context, R.layout.shop_details_header, null);
        ViewFinder finder = new ViewFinder(headerview);
        pager = finder.find(R.id.shop_header_pager);
        pager.setAdapter(shopBannerAdapter);
        indicator = finder.find(R.id.banner_indicator);
        indicator.setViewPager(pager);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, (int) (width / 0.88));
        pager.setLayoutParams(params);
        shopShowLayout.addView(headerview, 0);
    }

    private void loadShopData() {
        new AbstractRoboAsyncTask<ShopDetaildResponse>(context) {
            @Override
            protected ShopDetaildResponse run(Object data) throws Exception {
                return (ShopDetaildResponse) HttpUtils.doRequest(service.createShopDetailsRequest(id)).result;
            }

            @Override
            protected void onSuccess(ShopDetaildResponse data) throws Exception {
                super.onSuccess(data);
                if (data != null) {
                    if (data.getResponseCode() == 0) {
                        show(rootView);
                        hide(progressBar);
                        if (data.getShop() != null && !data.getShop().getPics().isEmpty()) {
                            goodsName.setText(data.getShop().getName());
                            goodsDescription.setText(data.getShop().getDesc());
                            exchangeMode = data.getShop().getExchangeMode();
                            userScore.setText(data.getShop().getPrice());
                            setShopBannerConent(data.getShop().getPics());
                            goodsId = data.getShop().getId();
                        }
                    } else {
                        ToastUtils.show(ShopDetailsActivity.this, data.getResponseMessage());

                    }
                }
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                super.onException(e);
                emptyTextView.setText("暂无信息");
                ToastUtils.show(ShopDetailsActivity.this, "商品详情加载失败");
                hide(progressBar);
                show(emptyTextView);
            }
        }.execute();
    }

    private void setShopBannerConent(List<Pics> pics) {
        shopBannerAdapter.setItems(pics);
        indicator.setOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pager.setCurrentItem(currentItem);

    }

    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            currentItem = (pager.getCurrentItem() + 1) % pager.getAdapter().getCount();
            handler.sendEmptyMessage(currentItem);
            timerHandler.postDelayed(this, 4000);
        }
    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pager.setCurrentItem(msg.what, true);
        }
    };

    private void voidinitDialog() {
        popDialog.setContentView(R.layout.shop_shipping_layout);
        userName = (EditText) popDialog.findViewById(R.id.user_name);
        userTelphone = (EditText) popDialog.findViewById(R.id.user_telphone);
        userAddress = (EditText) popDialog.findViewById(R.id.user_address);
        subButton = popDialog.findViewById(R.id.login_button);
        closeButton = popDialog.findViewById(R.id.close_button);
        shippingTips = popDialog.findViewById(R.id.shipp_tips);
        subButton.setOnClickListener(submitListener);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDialog();
            }
        });
    }

    private void showNoInformationShippingDialog() {

        if (View.GONE == shippingTips.getVisibility()) {
            shippingTips.setVisibility(View.VISIBLE);
        }
        showDialog();
    }

    private void getShippingData() {
        new AbstractRoboAsyncTask<GetShipResponse>(context) {
            @Override
            protected GetShipResponse run(Object data) throws Exception {
                return (GetShipResponse) HttpUtils.doRequest(service.creatShippingRequest()).result;
            }

            @Override
            protected void onSuccess(GetShipResponse data) throws Exception {
                super.onSuccess(data);
                if (data != null) {
                    if (data.getResponseCode() == 0) {
                        if (data.getShipping() == null) {
                            showNoInformationShippingDialog();
                        } else {
                            showConfirmShippingDialog(data.getShipping());
                        }

                    } else {
                        ToastUtils.show(ShopDetailsActivity.this, data.getResponseMessage());

                    }
                }
            }
        }.execute();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
        if (mReceiver != null) {
          unregisterReceiver(mReceiver);
        }
    }

    private void showConfirmShippingDialog(Shipping data) {
        userName.setText(data.getName());
        userTelphone.setText(data.getMobile());
        userAddress.setText(data.getAddress());
        if (View.VISIBLE == shippingTips.getVisibility()) {
            shippingTips.setVisibility(View.GONE);
        }
        if (!popDialog.isShowing())
            popDialog.show();
    }

    private View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            submitShippingInfor(true);

        }
    };

    private void submitShippingInfor(final boolean isShipping) {

        if (isShipping) {
            subName = userName.getText().toString().trim();
            subPhone = userTelphone.getText().toString().trim();
            subAddress = userAddress.getText().toString().trim();
            if (TextUtils.isEmpty(subName)) {
                ToastUtils.show(ShopDetailsActivity.this, "姓名不能为空");
                return;
            } else if (TextUtils.isEmpty(subPhone)) {
                ToastUtils.show(ShopDetailsActivity.this, "联系电话不能为空");
                return;
            } else if (TextUtils.isEmpty(subAddress)) {
                ToastUtils.show(ShopDetailsActivity.this, "邮寄地址不能为空");
                return;
            }
            if(subPhone.length()!=11){
                ToastUtils.show(ShopDetailsActivity.this, "请输入正确的手机号码");
                return;
            }
        }
        if (TextUtils.isEmpty(String.valueOf(goodsId))) {
            ToastUtils.show(ShopDetailsActivity.this, "物品不能为空");
            return;
        }

        new ProgressDialogTask<GetShipResponse>(context) {

            @Override
            protected GetShipResponse run(Object data) throws Exception {
                MultipartRequest<GetShipResponse> request = new MultipartRequest<GetShipResponse>(INFORMATION_SUB_URL);
                request.setClazz(GetShipResponse.class);
                request.addMultipartStringEntity("id", String.valueOf(goodsId));
                if (isShipping) {
                    request.addMultipartStringEntity("name", subName);
                    request.addMultipartStringEntity("mobile", subPhone);
                    request.addMultipartStringEntity("address", subAddress);
                }
                return (GetShipResponse) HttpUtils.doRequest(request).result;
            }

            @Override
            protected void onSuccess(GetShipResponse response) throws Exception {
                super.onSuccess(response);
                if (response != null) {
                    if (response.getResponseCode() == 0) {
                        cancelDialog();
                        initCongfirmDialog(response.getResponseMessage());
                    } else {
                        ToastUtils.show(ShopDetailsActivity.this, response.getResponseMessage());

                    }

                }

            }
        }.start("正在提交");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
        if (!popDialog.isShowing()) {
            popDialog.show();
        }
    }

    private void cancelDialog() {
        if (popDialog.isShowing() || popDialog != null)
            popDialog.dismiss();
    }
}
