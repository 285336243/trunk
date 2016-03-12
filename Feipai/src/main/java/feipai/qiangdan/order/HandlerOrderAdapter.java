package feipai.qiangdan.order;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import feipai.qiangdan.R;
import feipai.qiangdan.core.DialogFragmentActivity;
import feipai.qiangdan.core.ToastUtils;
import feipai.qiangdan.javabean.OrderItem;
import feipai.qiangdan.javabean.OrderListBean;
import feipai.qiangdan.util.HttpClientUtil;
import feipai.qiangdan.util.IConstant;
import feipai.qiangdan.util.ProgressDlgUtil;
import feipai.qiangdan.util.SaveSettingUtil;
import feipai.qiangdan.util.Utils;
import feipai.qiangdan.util.VolleyUtil;

/**
 * Created by wlanjie on 13-6-18.
 */
public class HandlerOrderAdapter extends SingleTypeAdapter<OrderItem> {


    private static final String VERIFY_CODE_URL = "api/v1/order/affirmcode";
    private static final int VERIFY_CODE = 0XCE;
    private final Activity activity;
    private final Bundle bundle;
    private Drawable drawable;
    private String codeResponse;
    //需要转换状态的订单
    private OrderItem mItem;
    //转换标识,标识订单状态
    private String tag;
    //倒计时类
//    private SubCountDownTime sub;

    /**
     * Create adapter
     *
     * @param activity
     */
    public HandlerOrderAdapter(Activity activity) {
        super(activity, R.layout.layout_handler_order_adapter);
        this.activity = activity;
        bundle = new Bundle();
    }

    /**
     * Get child view ids to store
     * <p/>
     * The index of each id in the returned array should be used when using the
     * helpers to update a specific child view
     *
     * @return ids
     */
    @Override
    protected int[] getChildViewIds() {
        return new int[]{
                R.id.text_note_message,//提示信息 0
                R.id.text_discount_amount,//优惠信息  1
                R.id.text_from_address, //寄件地址 2
                R.id.state_textview,  //支付状态 3
                R.id.changestate_layout,//状态布局 4
                R.id.verifycode_textview,//验证码提示 5
                R.id.verifycode_editview,//验证码输入框 6
                R.id.sendcode_imageview, //发送验证码按钮 7
                R.id.text_order_time,  //抢单时间 8
                R.id.jname_text, //寄件人姓名 9
                R.id.jphone_text, //寄件人电话 10
                R.id.sname_text, //收件人姓名 11
                R.id.sphone_text, //收件人电话12
                R.id.look_map,   //查看地图 13
                R.id.remarks_text,  //备注   14
                R.id.time_to_take,//定时取 15
                R.id.time_to_send,  //定时送 16
                R.id.plan_time,  //计划用时 17
                R.id.text_type,  //订单类型 18
                R.id.single_item_layout, //单个布局 19
                R.id.upload_location //上传位置 20

        };
    }

    /**
     * Update item
     *
     * @param position 位置
     * @param item     单个订单
     */
    @Override
    protected void update(int position, final OrderItem item) {
        if (item != null) {
            //如果是定时的显示为黄色，否则显示为蓝色
            if (item.getIsTime() == 1 || item.getIsSong() == 1) {
                view(19).setBackgroundResource(R.drawable.time_background);
            } else {
                view(19).setBackgroundResource(R.drawable.rober_background);
            }
            view(20).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bundle.clear();
                    bundle.putSerializable(IConstant.ITEM, item);
                    activity.startActivity(new Intent(activity, UpLoadActivity.class).putExtras(bundle));
                }
            });
            setText(0, "：¥" + item.getPrice() + "元/" + item.getDistance() + "公里/" + item.getWeight() + "公斤/");
            setText(3, item.getPaymentType());
            setText(1, "优惠金额：" + item.getCode_price());//优惠金额
            setText(2, "取件地址：" + item.getjAddress());//发货地址
            setText(8, "收件地址：" + item.getsAddress() + "\n抢单时间：" + item.getRobOrderTime());//收获地址 抢单时间
            setText(9, "寄件人：" + item.getjName());
            textView(10).getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//电话加下划线
            setText(10, item.getjPhone());
            setText(11, "收件人：" + item.getsName());
            textView(12).getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//电话加下划线
            setText(12, item.getsPhone());
            setText(14, "物品名称：" + item.getGoodsName() + "\n客户备注 ：" + item.getRemark());
            //设为不可见
            view(15).setVisibility(View.GONE);
            view(16).setVisibility(View.GONE);
            if ("送".equals(item.getType())) {
                textView(18).setText("【快送】");
                if (item.getIsSong() == 1) {
                    textView(18).setText("【定时送】");
                }
            }
            if ("买".equals(item.getType())) {
                textView(18).setText("【代买】");
            }
            if (item.getIsTime() == 1) {
                view(15).setVisibility(View.VISIBLE);
                setText(15, "指定取件时间：" + item.getjTime());
            }
            if (item.getIsSong() == 1) {
                view(16).setVisibility(View.VISIBLE);
                setText(16, "指定送达时间：" + item.getEndTime());
            }

            //拨打寄件人电话
            view(10).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callTelephone(item.getjPhone());
                }
            });
//            if(position==1) {
//                sub = new SubCountDownTime(textView(13), Long.valueOf(item.getjTimePlan()) * 60 * 1000 * 1l);
//                sub.start();
//            }
            final String state = item.getState();
            final EditText virifyEdit = view(6);
            view(12).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callTelephone(item.getsPhone());
                }
            });

            if (state.equals("取件中")) {
                view(4).setVisibility(View.VISIBLE);
                setText(5, "取件中-请输入寄件验证码并发送");
                setText(17, "请在" + item.getjTimePlan() + "分钟内到 " + item.getjAddress() + "取件");
                view(7).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String verifyCode = virifyEdit.getText().toString().trim();
                        if (TextUtils.isEmpty(verifyCode)) {
                            ToastUtils.show(activity, "验证码为空，请输入");
                            return;
                        }
                        mItem = item;
                        tag = "取件中";
                        sendVerifyCode(item.getId(), verifyCode);
                    }
                });

                view(13).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.startActivity(new Intent(activity, TransModeActivity.class).putExtra(IConstant.JADDRESS, item.getjAddress()));
                    }
                });
            }
            if (state.equals("送货中")) {
                view(4).setVisibility(View.VISIBLE);
                setText(5, "送件中-请输入收件验证码并发送");
                setText(17, "请在" + item.getsTimePlan() + "分钟内送到 " + item.getsAddress());
                view(7).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String verifyCode = virifyEdit.getText().toString().trim();
                        if (TextUtils.isEmpty(verifyCode)) {
                            ToastUtils.show(activity, "验证码为空，请输入");
                            return;
                        }
                        mItem = item;
                        tag = "送货中";
                        sendVerifyCode(item.getId(), verifyCode);
                    }
                });
                view(13).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.startActivity(new Intent(activity, TransModeActivity.class).putExtra(IConstant.SADDRESS, item.getsAddress()));
                    }
                });
            }
            if (state.equals("已完结") || state.equals("待评价")) {
                setText(3, "订单状态：配送成功");
                view(4).setVisibility(View.GONE);
            }


        }
    }

    private class SubCountDownTime extends CountDownTimer {
        private TextView conutTime;

        public SubCountDownTime(TextView textView, long millisInFuture) {
            super(millisInFuture, 1000);
            this.conutTime = textView;
        }

        /**
         * Callback fired on regular interval.
         *
         * @param millisUntilFinished The amount of time until finished.
         */
        @Override
        public void onTick(long millisUntilFinished) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");// 初始化Formatter的转换格式。
            formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
            String hms = formatter.format(millisUntilFinished);
            conutTime.setText(hms);
            conutTime.setText(String.valueOf(millisUntilFinished / 1000));
        }

        /**
         * Callback fired when the time is up.
         */
        @Override
        public void onFinish() {
            conutTime.setText("时间已到");
        }
    }

    private void callTelephone(String number) {
        //用intent启动拨打电话
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        activity.startActivity(intent);
    }

    private void sendVerifyCode(int id, String verifyCode) {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("code", verifyCode);
        map.put("orderid", String.valueOf(id));
        map.put("sid", SaveSettingUtil.getUserSid(activity));
        VolleyUtil.getInstance(activity).volley_put(VERIFY_CODE_URL, map, new VolleyUtil.OnCompleteListener() {
            @Override
            public void correct(String str) {
                setSateCode(str);
            }

            @Override
            public void error(VolleyError error) {

            }
        });
/*        new Thread() {
            @Override
            public void run() {
                try {
                    Message msg = Message.obtain();
                    //待处理订单请求
                    codeResponse = HttpClientUtil.getInstance().getPutData(VERIFY_CODE_URL, map);
                    msg.what = VERIFY_CODE;
                    mHandler.sendMessage(msg);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }.start();*/
    }

    private void setSateCode(String codeResponse) {
        Gson gson = new Gson();
        OrderListBean data = gson.fromJson(codeResponse, OrderListBean.class);
        // 验证结果(1:成功；0:失败)
        int state = data.getState();
        if (state == 1) {
            ToastUtils.show(activity, "验证成功");
            //状态改变
            if (tag.equals("取件中")) {
                mItem.setState("送货中");
            }
            if (tag.equals("送货中")) {
                mItem.setState("已完结");
            }
            notifyDataSetChanged();
            mItem = null;
            tag = null;
        }
        if (state == 0) {
            ToastUtils.show(activity, "验证失败");
        }
    }


/*    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VERIFY_CODE:
                    ProgressDlgUtil.stopProgressDlg();
                    if (codeResponse.contains(IConstant.REQUST_FAIL)) {
                        ToastUtils.show(activity, codeResponse);
                    } else {
                        Gson gson = new Gson();
                        OrderListBean data = gson.fromJson(codeResponse, OrderListBean.class);
                        // 验证结果(1:成功；0:失败)
                        int state = data.getState();
                        if (state == 1) {
                            ToastUtils.show(activity, "验证成功");
                            //状态改变
                            if (tag.equals("取件中")) {
                                mItem.setState("送货中");
                            }
                            if (tag.equals("送货中")) {
                                mItem.setState("已完结");
                            }
                            notifyDataSetChanged();
                            mItem = null;
                            tag = null;
                        }
                        if (state == 0) {
                            ToastUtils.show(activity, "验证失败");
                        }

                    }
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };*/
}
