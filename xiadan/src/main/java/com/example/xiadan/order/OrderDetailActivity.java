package com.example.xiadan.order;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.example.xiadan.R;
import com.example.xiadan.bean.OrderItem;
import com.example.xiadan.bean.OrderListBean;
import com.example.xiadan.util.HttpClientUtil;
import com.example.xiadan.util.IConstant;
import com.example.xiadan.util.ProgressDlgUtil;
import com.example.xiadan.util.SaveSettingUtil;
import com.example.xiadan.whole.BaseActivity;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.HashMap;


/**
 * Created by 51wanh on 2015/3/5.
 */
public class OrderDetailActivity extends BaseActivity {
    //订单详情url
    private static final String DETAIL_URL = "page/api/v1/order/details";
    private static final int DETAIL_CODE = 67;
    //订单编号,寄件人信息,收件人信息
    private TextView mNumber, mJi, mShou;
    //支付类型，费用信息
    private TextView mPayType, mCost;

    /**
     * 详情结果
     */
    private String detailResult;
    /**
     * 物品信息
     */
    private TextView mGoodsInfo;
    /**
     * 其他
     */
    private TextView mOther;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_order_detail);
        int orderId = getIntent().getIntExtra(IConstant.ORDER_ID, 0);
        intUi();
        getOrderDetail(orderId);
    }

    private void getOrderDetail(int orderId) {
        ProgressDlgUtil.showProgressDlg(activity, "正在加载...");
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("sid", SaveSettingUtil.getUserSid(activity));//登录验证码
        map.put("orderid", String.valueOf(orderId));
        volley_get(DETAIL_URL, map);
/*        HttpClientUtil.getInstance().doGetAsyn(DETAIL_URL, map, new HttpClientUtil.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                detailResult = result;
                Message msg = Message.obtain();
                msg.what = DETAIL_CODE;
                mHandler.sendMessage(msg);
            }
        });*/
    }

    @Override
    public void correcttResponse(String response) {
        super.correcttResponse(response);
        ProgressDlgUtil.stopProgressDlg();
        Gson gson = new Gson();
        OrderItem data = gson.fromJson(response, OrderItem.class);
        setOrderData(data);
    }

/*    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DETAIL_CODE:
                    ProgressDlgUtil.stopProgressDlg();
                    if (detailResult.contains(IConstant.REQUST_FAIL)) {
//                        ToastUtils.show(activity, ordersResult);
                    } else {
                        Gson gson = new Gson();
                        OrderItem data = gson.fromJson(detailResult, OrderItem.class);
                        setOrderData(data);
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };*/

    private void setOrderData(OrderItem item) {
        mNumber.setText(" [编号：" + item.getOrderNum() + "]订单详情");
        mJi.setText("姓名: " + item.getjName() + "   手机号码：" + item.getjPhone() +
                "\n寄件地址：" + item.getjAddress());
        mShou.setText("姓名: " + item.getsName() + "   手机号码：" + item.getsPhone() +
                "\n收件地址：" + item.getsAddress());
        mPayType.setText(item.getPaymentType() + "/" + item.getType());
        mCost.setText(item.getDistance() + "公里/" + item.getPrice() + "元");
        if (!TextUtils.isEmpty(item.getGoodsName())) {
            if (!TextUtils.isEmpty(item.getRemark())) {
                mGoodsInfo.setText("重量: " + item.getWeight() + "公斤  物品名称：" + item.getGoodsName()
                        + "\n备注 " + item.getRemark());
            } else {
                mGoodsInfo.setText("重量: " + item.getWeight() + "公斤  物品名称：" + item.getGoodsName());
            }
        } else {
            if (!TextUtils.isEmpty(item.getRemark())) {
                mGoodsInfo.setText("重量: " + item.getWeight() + "公斤 "
                        + "\n备注 " + item.getRemark());
            } else {
                mGoodsInfo.setText("重量: " + item.getWeight() + "公斤 ");
            }
        }
        mOther.setText("寄件验证码: " + item.getjCode() + "  取件验证码: " + item.getsCode() +
                "\n飞派员：" + item.getEmployee() + " 下单时间: " + item.getPublishTime());
    }

    /**
     * 初始化view
     */
    private void intUi() {
        mNumber = (TextView) findViewById(R.id.number_text);
        mJi = (TextView) findViewById(R.id.ji_person);
        mShou = (TextView) findViewById(R.id.shou_person);
        mPayType = (TextView) findViewById(R.id.pay_type);
        mCost = (TextView) findViewById(R.id.cost_text);
        mGoodsInfo = (TextView) findViewById(R.id.goods_text);
        mOther = (TextView) findViewById(R.id.other_text);

    }
}
