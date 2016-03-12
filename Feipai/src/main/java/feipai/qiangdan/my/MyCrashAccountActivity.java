package feipai.qiangdan.my;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import feipai.qiangdan.R;
import feipai.qiangdan.core.DialogFragmentActivity;
import feipai.qiangdan.core.ToastUtils;
import feipai.qiangdan.javabean.AccountBean;
import feipai.qiangdan.util.HttpClientUtil;
import feipai.qiangdan.util.IConstant;
import feipai.qiangdan.util.NetWorkConnect;
import feipai.qiangdan.util.ProgressDlgUtil;
import feipai.qiangdan.util.SaveSettingUtil;
import feipai.qiangdan.util.VolleyUtil;
import roboguice.inject.InjectView;

/**
 * 我的现金账户
 */
public class MyCrashAccountActivity extends DialogFragmentActivity {
    private static final String PERSON_ACCOUNT_URL = "api/v1/account/money/info";
    private static final int ACCOUNT_INFOR = 0XF7;
    private static final String TRANS_ACCOUNT_URL = "api/v1/transfer/submit";
    private static final int TRANS_ACCOUNT_MESSAGE = 0XCE2;
    @InjectView(R.id.about_back)
    private View backView;
    private String resultResponse;
    // 账号余额
    @InjectView(R.id.total_income_text)
    private TextView totalIncome;
    // 可以金额
    @InjectView(R.id.use_income_text)
    private TextView useIncome;

    /**
     * 提现
     */
    @InjectView(R.id.submit_button)
    private Button submitButton;
    /**
     * 充值
     */
    @InjectView(R.id.recharge_button)
    private Button rechargeButton;

    //支付宝帐号
    @InjectView(R.id.your_acout_text)
    private TextView yourAccout;

    private EditText amountMoney;
    private View subButton, closeButton;
    private View shippingTips;
    private int goodsId;
    private String subName;
    private String subPhone;
    private String subAddress;
    private Dialog popDialog;
    private String mayUseMoney;
    private String transferResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_crash_account);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voidinitDialog();
            }
        });
        //充值
        rechargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyCrashAccountActivity.this, RechargeActivity.class));
            }
        });
        getAccountInfor();
    }

    private void getAccountInfor() {
        if (!NetWorkConnect.isConnect(this)) {
            ToastUtils.show(this, "您的网络不可用，请检查您的网络状况");
            return;
        }
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("sid", SaveSettingUtil.getUserSid(this));
        volley_get(PERSON_ACCOUNT_URL, map);
/*        HttpClientUtil.getInstance().doGetAsyn(PERSON_ACCOUNT_URL, map, new HttpClientUtil.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                resultResponse = result;
                Message msg = Message.obtain();
                msg.what = ACCOUNT_INFOR;
                mHandler.sendMessage(msg);

            }
        });*/
    }

    @Override
    public void correcttResponse(String response) {
        super.correcttResponse(response);
        Gson gson = new Gson();
        AccountBean responseBean = gson.fromJson(response, AccountBean.class);
        setPersonAccount(responseBean);
    }

    private void setPersonAccount(AccountBean data) {
        totalIncome.setText(data.getBalance());
        useIncome.setText(data.getUse_money());
        mayUseMoney = data.getUse_money();
        // 是否有支付宝账号(1:有；0：无)
        if (data.getHasAccount_zfb() == 1) {
            yourAccout.setText(data.getAccount_zfb());
        } else {
            yourAccout.setText("没有支付宝账号，请申请完成后与客服人员联系");
        }
    }

    private void voidinitDialog() {
        popDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        popDialog.setCanceledOnTouchOutside(false);
        popDialog.setContentView(R.layout.submit_crash_layout);
        amountMoney = (EditText) popDialog.findViewById(R.id.amount_money);
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
        showDialog();
    }

    private View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            postAccounts();

        }
    };

    private void postAccounts() {
        String amount = amountMoney.getText().toString().trim();
        if (TextUtils.isEmpty(amount)) {
            ToastUtils.show(this, "金额为空，请输入具体金额");
            return;
        }
        if (Float.valueOf(amount) <= 0.00f) {
            ToastUtils.show(this, "金额为0，请输入具体金额");
            return;
        }
        if (Float.valueOf(amount) > Float.valueOf(mayUseMoney)) {
            ToastUtils.show(this, "金额不能大于可用余额，请重新输入");
            return;
        }
//        ProgressDlgUtil.showProgressDlg(this, "正在提交");
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("sid", SaveSettingUtil.getUserSid(this));
        map.put("money", amount);
        VolleyUtil.getInstance(this).volley_post(TRANS_ACCOUNT_URL, map, new VolleyUtil.OnCompleteListener() {
            @Override
            public void correct(String str) {
                // status: 1 ， 操作结果(1:成功)
                ToastUtils.show(MyCrashAccountActivity.this, "提交成功，请耐心等待\n需要一到三个工作日的审核时间");
                cancelDialog();
                getAccountInfor();
            }

            @Override
            public void error(VolleyError error) {
                if (error.networkResponse.statusCode == 401) {
                    ToastUtils.show(activity, "登录超时，请登录");
                    startedActivity(LoginActivity.class);
                }
            }
        });
/*        HttpClientUtil.getInstance().doPostAsyn(TRANS_ACCOUNT_URL, map, new HttpClientUtil.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                transferResponse = result;
                Message msg = Message.obtain();
                msg.what = TRANS_ACCOUNT_MESSAGE;
                mHandler.sendMessage(msg);
            }
        });*/

    }

/*    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TRANS_ACCOUNT_MESSAGE:
                    ProgressDlgUtil.stopProgressDlg();
                    if (transferResponse.contains(IConstant.REQUST_FAIL)) {
                        ToastUtils.show(MyCrashAccountActivity.this, transferResponse);
                    } else {
//                        Gson gson = new Gson();
//                        MyInfoBean data = gson.fromJson(returnString, MyInfoBean.class);
                        // status: 1 ， 操作结果(1:成功)
                        ToastUtils.show(MyCrashAccountActivity.this, "提交成功，请耐心等待\n需要一到三个工作日的审核时间");
                        cancelDialog();
                        getAccountInfor();
                    }
                    break;
*//*                case ACCOUNT_INFOR:
                    if (resultResponse.contains(IConstant.REQUST_FAIL)) {
                        ToastUtils.show(MyCrashAccountActivity.this, resultResponse);
                    } else {
                        Gson gson = new Gson();
                        AccountBean response = gson.fromJson(resultResponse, AccountBean.class);
                        setPersonAccount(response);
                    }
                    break;*//*
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };*/

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
