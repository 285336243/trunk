package com.example.xiadan.order;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.android.volley.VolleyError;
import com.example.xiadan.R;
import com.example.xiadan.bean.ConponBean;
import com.example.xiadan.bean.ConponBeanList;
import com.example.xiadan.bean.OderResult;
import com.example.xiadan.core.ToastUtils;
import com.example.xiadan.loginregister.LoginActivity;
import com.example.xiadan.loginregister.UserProtocal;
import com.example.xiadan.util.HttpClientUtil;
import com.example.xiadan.util.IConstant;
import com.example.xiadan.util.ProgressDlgUtil;
import com.example.xiadan.util.SaveSettingUtil;
import com.example.xiadan.util.ToastLocationUtil;
import com.example.xiadan.whole.AppManager;
import com.example.xiadan.whole.BaseActivity;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

/**
 * Created by 51wanh on 2015/3/1.
 */
public class ResultActivity extends BaseActivity {
    /**
     * 确认订单url
     */
    private static final String SUBMIT_URL = "page/api/v1/order/affirm";
    private static final int SUMMIT_CODE = 0X83;
    /**
     * 优惠劵url
     */
    private static final String YOUHUI_URL = "page/api/v1/order/codes";
    private static final int YOUHUI_CODE = 13;
    //费用信息
    private TextView mCost;
    private Button subButton;

    /**
     * 是否开具发票,是否使用优惠卷
     */
    private CheckBox fapiaoCheck, youhuiCheck;
    /**
     * 发票标识
     */
    private int haveInvoice;
    /**
     * 发票公司，电话,地址
     */
    private EditText compEdit, telEdit, addresEdit;
    private ResultActivity context;
    private int useCode;
    /**
     * 支付方式选择
     */
    private RadioGroup payRadioGroup;
    //支付方式
    private String paymentType;
    //提交订单结果
    private String submiResult;
    /**
     * 优惠卷结果
     */
    private String youhuiResult;
    /**
     * 优惠劵RadioGroup
     */
    private RadioGroup youhuiRadioGroup;

    /**
     * 订单数据
     */
    private ConponBeanList dataBeanList;
    /**
     * 需要费用
     */
    private String price;
    /**
     * 优惠码
     */
    private String yuihuiCode;
    /**
     * 优惠卷数量
     */
    private int amountHui;
    /**
     * 没有优惠卷
     */
    private TextView emptyYouhui;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_result);
        actionBar.setTitle("提交订单");
        AppManager.getAppManager().addActivity(this);
        context = ResultActivity.this;
        String order = getIntent().getStringExtra(IConstant.ORDER_RESULT);
        Gson gson = new Gson();
        final OderResult data = gson.fromJson(order, OderResult.class);
        mCost = (TextView) findViewById(R.id.cost_text);
        price = data.getPrice();
        mCost.setText("费用信息：\n\n" + data.getDistance() + "公里/" + price + "元(包含" + data.getNightPrice() + "元晚间服务费)");

        //点击协议
        findViewById(R.id.submit_protocol_textview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(UserProtocal.class);
            }
        });
        //是否使用优惠卷
        youhuiCheck = (CheckBox) findViewById(R.id.youhui_check);
        //没有优惠卷
        emptyYouhui = (TextView) findViewById(R.id.empty_text);
        youhuiRadioGroup = (RadioGroup) findViewById(R.id.youhui_radiogroup);
        youhuiCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {


                    if (null == dataBeanList) {
                        getYouHuiJuan();
                    }
                    if (amountHui > 0) {
                        youhuiRadioGroup.setVisibility(View.VISIBLE);
                        emptyYouhui.setVisibility(View.GONE);
                    } else {
                        emptyYouhui.setVisibility(View.VISIBLE);
                    }
                } else {
                    //否使用优惠券
                    useCode = 0;
                    if (amountHui > 0) {
                        youhuiRadioGroup.setVisibility(View.GONE);
                        emptyYouhui.setVisibility(View.GONE);
                    } else {
                        emptyYouhui.setVisibility(View.GONE);
                    }
                }
            }
        });
        //是否开具发票
        fapiaoCheck = (CheckBox) findViewById(R.id.receipt_check);
        final View fapiaoView = findViewById(R.id.fapiao_layout);
        fapiaoCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    fapiaoView.setVisibility(View.VISIBLE);
                    haveInvoice = 1;
                } else {
                    fapiaoView.setVisibility(View.GONE);
                    haveInvoice = 0;
                }
            }
        });
        //发票公司名称
        compEdit = (EditText) findViewById(R.id.company_edit);
        //发票电话
        telEdit = (EditText) findViewById(R.id.teleph_edit);
        //发票地址
        addresEdit = (EditText) findViewById(R.id.address_edit);
        //支付方式选择
        payRadioGroup = (RadioGroup) findViewById(R.id.pay_radiogroup);
        payRadioGroup.check(R.id.arrivepay_button);
        paymentType = "到付";
        payRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    //网上支付
                    case R.id.netpay_button:
                        paymentType = "网上支付";
                        break;
                    //到付
                    case R.id.arrivepay_button:
                        paymentType = "到付";
                        break;
                    //寄付
                    case R.id.sendpay_button:
                        paymentType = "寄付";
                        break;
                    default:
                        break;
                }
            }
        });
        subButton = (Button) findViewById(R.id.submit_button);
        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final HashMap<String, String> map = new HashMap<String, String>();
                //选择开具发票
                if (haveInvoice == 1) {
                    //检查发票内容是否为空
                    String compName = compEdit.getText().toString().trim();
                    if (TextUtils.isEmpty(compName)) {
                        ToastUtils.show(context, "请填写公司名称");
                        return;
                    }
                    String telephone = telEdit.getText().toString().trim();
                    if (TextUtils.isEmpty(telephone)) {
                        ToastUtils.show(context, "请填写电话");
                        return;
                    }
                    String address = addresEdit.getText().toString().trim();
                    if (TextUtils.isEmpty(address)) {
                        ToastUtils.show(context, "请填写地址");
                        return;
                    }
                    // 发票信息(公司抬头)
                    map.put("title", compName);
                    // 发票信息(联系电话)
                    map.put("phone", telephone);
                    // 发票信息(收发票地址)
                    map.put("address", address);
                }

                //登陆验证码
                map.put("sid", SaveSettingUtil.getUserSid(context));
                //订单编号
                map.put("orderNum", data.getOrderNum());
                // 是否使用优惠券(1:是；0:否,默认为0)
                map.put("useCode", String.valueOf(useCode));
                // 优惠码(当useCode=1时，必填)
                if (useCode == 1) {
                    // 优惠码不为空时才上传
                    if (!TextUtils.isEmpty(yuihuiCode)) {
                        map.put("code", yuihuiCode);
                    }
                }
                // 是否需要发票(1:是；0:否,默认为0)
                map.put("haveInvoice", String.valueOf(haveInvoice));
                // 支付方式(网上支付;到付;寄付)
                map.put("paymentType", paymentType);
                //提交订单
                volley_post(SUBMIT_URL, map);
/*                ProgressDlgUtil.showProgressDlg(context, "正在提交......");
                HttpClientUtil.getInstance().doPostAsyn(SUBMIT_URL, map, new HttpClientUtil.CallBack() {
                    @Override
                    public void onRequestComplete(String result) {
                        submiResult = result;
                        Message msg = Message.obtain();
                        msg.what = SUMMIT_CODE;
                        mHandler.sendMessage(msg);
                    }
                });*/
            }
        });
    }

    @Override
    public void correcttResponse(String response) {
        super.correcttResponse(response);
        //关掉下单页面
        if (null != XiaOrderActivity.instance) {
            XiaOrderActivity.instance.finish();
        }
        startActivity(new Intent(context, SuccesstActivity.class).putExtra(IConstant.ORDER_SUCCESS, response));
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public void errorResponse(VolleyError error) {
        super.errorResponse(error);
    }

    private void getYouHuiJuan() {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("sid", SaveSettingUtil.getUserSid(context));//登录验证码
        HttpClientUtil.getInstance().doGetAsyn(YOUHUI_URL, map, new HttpClientUtil.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                youhuiResult = result;
                Message msg = Message.obtain();
                msg.what = YOUHUI_CODE;
                mHandler.sendMessage(msg);
            }
        });
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
/*                //提交订单结果
                case SUMMIT_CODE:
                    ProgressDlgUtil.stopProgressDlg();
                    if (submiResult.contains(IConstant.REQUST_FAIL)) {
//                        ToastUtils.show(context, submiResult);
                        ToastUtils.show(context, "提交失败，请重新提交");
                    } else {
                        startActivity(new Intent(context, SuccesstActivity.class).putExtra(IConstant.ORDER_SUCCESS, submiResult));
                        finish();
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                    break;*/
                case YOUHUI_CODE:
                    if (youhuiResult.contains(IConstant.REQUST_FAIL)) {
//                        ToastUtils.show(activity, youhuiResult);
                    } else {
                        Gson gson = new Gson();
                        dataBeanList = gson.fromJson(youhuiResult, ConponBeanList.class);
                        setYouHuiData(dataBeanList);
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 设置优惠码
     *
     * @param data 优惠码
     */
    private void setYouHuiData(final ConponBeanList data) {
        final List<ConponBean> orderList = data.getList();
        amountHui = data.getSize();
        if (amountHui > 0) {
            youhuiRadioGroup.setVisibility(View.VISIBLE);
            emptyYouhui.setVisibility(View.GONE);
        } else {
            emptyYouhui.setVisibility(View.VISIBLE);
        }

        RadioButton rb;
        for (int i = 0; i < amountHui; i++) {
            rb = new RadioButton(context);
            rb.setText(orderList.get(i).getCode() + "(" + orderList.get(i).getPrice() + "元)");
            youhuiRadioGroup.addView(rb, i);

        }
        youhuiRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int checkIndex = 0;
                for (int i = 0; i < amountHui; i++) {
                    if (((RadioButton) youhuiRadioGroup.getChildAt(i)).isChecked()) {
                        checkIndex = i;
                        break;
                    }
                }
//                ToastLocationUtil.showToast(context, "选中 " + orderList.get(checkIndex).getPrice() + ",inex=" + checkIndex);
                //    优惠券使用限额(消费金额>=该字段才可以使用)
                if (Integer.valueOf(price) >= Integer.valueOf(orderList.get(checkIndex).getUsePrice())) {
                    yuihuiCode = orderList.get(checkIndex).getCode();
                    //是使用优惠券
                    useCode = 1;
                } else {
                    //否使用优惠券
                    useCode = 0;
                    ToastLocationUtil.showToast(context, "您的消费金额不足，请重新选择");
                }
            }
        });

    }


}
